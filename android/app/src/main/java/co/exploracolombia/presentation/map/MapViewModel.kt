package co.exploracolombia.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.exploracolombia.data.local.SiteCatalog
import co.exploracolombia.data.location.LocationTracker
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.domain.model.VisitResult
import co.exploracolombia.domain.util.haversineMeters
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(private val locationTracker: LocationTracker) : ViewModel() {

    val sites: List<SiteBrief> = SiteCatalog.all

    private val _gamification = MutableStateFlow(GamificationState())
    val gamification: StateFlow<GamificationState> = _gamification.asStateFlow()

    private val _selectedSite = MutableStateFlow<SiteBrief?>(null)
    val selectedSite: StateFlow<SiteBrief?> = _selectedSite.asStateFlow()

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation.asStateFlow()

    private val _reachableSiteIds = MutableStateFlow<Set<String>>(emptySet())
    val reachableSiteIds: StateFlow<Set<String>> = _reachableSiteIds.asStateFlow()

    private var locationJob: Job? = null

    /**
     * Ubicación de bajo consumo (`trackApproximateLocation`, no la de alta
     * precisión que solo se usa en ScanScreen) — para el mapa alcanza con
     * saber en qué cuadra estás, no con precisión de metro. Llamar desde
     * ON_RESUME de MapScreen.
     */
    fun startLocationUpdates() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            locationTracker.trackApproximateLocation().collect { (lat, lng) ->
                _userLocation.value = lat to lng
                _reachableSiteIds.value = sites.filter { site ->
                    haversineMeters(lat, lng, site.lat, site.lng) <= site.geofenceRadiusMeters
                }.map { it.id }.toSet()
            }
        }
    }

    fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

    fun selectSite(site: SiteBrief) {
        _selectedSite.value = site
    }

    fun dismissSiteDetail() {
        _selectedSite.value = null
    }

    /** Llamar cuando ScanScreen termina una visita con éxito. La lámina queda "conseguida" pero gris hasta que se pegue. */
    fun onVisitCompleted(result: VisitResult) {
        if (!result.success) return
        _gamification.update { current ->
            current.copy(
                totalXp = result.totalXp,
                unlockedBadgeCodes = result.badge?.let { current.unlockedBadgeCodes + it.code }
                    ?: current.unlockedBadgeCodes,
            )
        }
    }

    /** Llamar cuando el usuario toca una silueta gris en AlbumScreen para "pegarla". */
    fun pasteLamina(badgeCode: String) {
        _gamification.update { current ->
            if (badgeCode !in current.unlockedBadgeCodes) return@update current
            current.copy(pastedBadgeCodes = current.pastedBadgeCodes + badgeCode)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}
