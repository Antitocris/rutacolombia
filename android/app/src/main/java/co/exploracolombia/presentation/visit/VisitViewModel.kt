package co.exploracolombia.presentation.visit

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.exploracolombia.BuildConfig
import co.exploracolombia.data.camera.CameraCaptureManager
import co.exploracolombia.data.location.LocationTracker
import co.exploracolombia.domain.model.HistoricalSite
import co.exploracolombia.domain.model.VisitFailureReason
import co.exploracolombia.domain.usecase.ValidateVisitUseCase
import co.exploracolombia.domain.util.haversineMeters
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sitio de prueba fijo — coincide exactamente con supabase/seed.sql
 * (Baluarte de San Francisco, Cartagena). Cuando exista selección real de
 * sitios desde una ruta, este valor llega por argumento de navegación en
 * lugar de estar fijo aquí; por ahora es la única forma de probar el flujo
 * completo contra el backend local sin tener aún esa pantalla.
 */
val TEST_SITE = HistoricalSite(
    id = "11111111-1111-1111-1111-111111111111",
    code = "cartagena-murallas-baluarte-san-francisco",
    department = "Bolívar",
    city = "Cartagena de Indias",
    lat = 10.4236,
    lng = -75.5478,
    geofenceRadiusMeters = 40,
    title = "Baluarte de San Francisco",
    narrative = "",
    coverImageUrl = null,
    xpReward = 100,
    isUnlocked = false,
)

class VisitViewModel(
    private val locationTracker: LocationTracker,
    private val cameraCaptureManager: CameraCaptureManager,
    private val validateVisitUseCase: ValidateVisitUseCase,
) : ViewModel() {

    val targetSite: HistoricalSite = TEST_SITE

    private val _uiState = MutableStateFlow<VisitUiState>(VisitUiState.Idle)
    val uiState: StateFlow<VisitUiState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<VisitLocation?>(null)
    val location: StateFlow<VisitLocation?> = _location.asStateFlow()

    private var locationJob: Job? = null
    private var lastKnownLocation: Pair<Double, Double>? = null

    /** Llamar desde ON_RESUME de la pantalla — nunca antes, para no encender GPS en segundo plano. */
    fun startLocationUpdates() {
        // MOCK_LOCATION_ENABLED (BuildConfig, ver app/build.gradle.kts): en
        // el build de prueba, en vez de esperar un GPS real se reporta
        // directo la coordenada exacta del hito, para poder validar el
        // flujo completo (incluida la llamada real al backend) desde
        // cualquier lugar, sin viajar a Cartagena. `false` siempre en
        // release — ahí sí se exige GPS real.
        if (BuildConfig.MOCK_LOCATION_ENABLED) {
            lastKnownLocation = targetSite.lat to targetSite.lng
            _location.value = VisitLocation(
                lat = targetSite.lat,
                lng = targetSite.lng,
                distanceMeters = 0,
                withinRange = true,
            )
            return
        }

        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            locationTracker.trackPreciseLocation().collect { (lat, lng) ->
                lastKnownLocation = lat to lng
                val distance = haversineMeters(lat, lng, targetSite.lat, targetSite.lng)
                _location.value = VisitLocation(
                    lat = lat,
                    lng = lng,
                    distanceMeters = distance.toInt(),
                    withinRange = distance <= targetSite.geofenceRadiusMeters,
                )
            }
        }
    }

    /** Llamar desde ON_PAUSE / onDispose de la pantalla. */
    fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

    suspend fun bindCamera(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        cameraCaptureManager.bind(lifecycleOwner, previewView)
    }

    fun unbindCamera() {
        cameraCaptureManager.unbind()
    }

    fun validateVisit() {
        if (_uiState.value == VisitUiState.Loading) return

        val (lat, lng) = lastKnownLocation ?: run {
            _uiState.value = VisitUiState.Error(
                message = "Todavía no tenemos tu ubicación GPS. Espera unos segundos e intenta de nuevo.",
                reason = null,
            )
            return
        }

        _uiState.value = VisitUiState.Loading
        viewModelScope.launch {
            val photoBytes = try {
                cameraCaptureManager.capturePhotoBytes()
            } catch (e: Exception) {
                _uiState.value = VisitUiState.Error(
                    message = "No se pudo capturar la foto: ${e.message ?: "error desconocido de la cámara"}.",
                    reason = null,
                )
                return@launch
            }

            validateVisitUseCase(
                siteId = targetSite.id,
                lat = lat,
                lng = lng,
                photoJpegBytes = photoBytes,
            ).onSuccess { result ->
                _uiState.value = if (result.success) {
                    VisitUiState.Success(result)
                } else {
                    VisitUiState.Error(
                        message = result.message ?: defaultMessageFor(result.failureReason),
                        reason = result.failureReason,
                    )
                }
            }.onFailure { error ->
                _uiState.value = VisitUiState.Error(
                    message = "No se pudo conectar con el servidor: ${error.message ?: "revisa tu conexión"}.",
                    reason = VisitFailureReason.NETWORK_ERROR,
                )
            }
        }
    }

    fun dismissError() {
        _uiState.value = VisitUiState.Idle
    }

    fun dismissSuccess() {
        _uiState.value = VisitUiState.Idle
    }

    private fun defaultMessageFor(reason: VisitFailureReason?): String = when (reason) {
        VisitFailureReason.OUT_OF_RANGE -> "Estás muy lejos del hito. Acércate e inténtalo de nuevo."
        VisitFailureReason.PHOTO_MISMATCH -> "La foto no coincide con el monumento esperado. Intenta con otro ángulo."
        VisitFailureReason.ALREADY_VISITED -> "Ya desbloqueaste este hito anteriormente."
        VisitFailureReason.VISION_UNAVAILABLE -> "No se pudo analizar la imagen. Intenta de nuevo en unos segundos."
        VisitFailureReason.NETWORK_ERROR, null -> "No se pudo completar la validación. Revisa tu conexión."
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
        cameraCaptureManager.unbind()
    }
}
