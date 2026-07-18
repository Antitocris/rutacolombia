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

const val HISTORY_POINTS_PER_POST = 25
const val HINT_COST_POINTS = 50

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

    /**
     * Llamar cuando ScanScreen termina una visita con éxito. La lámina queda
     * "conseguida" pero gris hasta que se pegue. Si el hito tiene una foto
     * histórica real (ver SiteBrief.historicalPhotoUrl), esta visita también
     * habilita una publicación nueva en el Feed de la Historia — eso
     * otorga Puntos de Historia de una vez, no hace falta "publicar" un
     * paso aparte.
     */
    fun onVisitCompleted(siteId: String, result: VisitResult) {
        if (!result.success) return
        _gamification.update { current ->
            val isNewFeedPost = result.photoUrl.isNotBlank() &&
                siteId !in current.capturedPhotoUrls &&
                sites.find { it.id == siteId }?.historicalPhotoUrl != null
            current.copy(
                totalXp = result.totalXp,
                unlockedBadgeCodes = result.badge?.let { current.unlockedBadgeCodes + it.code }
                    ?: current.unlockedBadgeCodes,
                capturedPhotoUrls = if (result.photoUrl.isNotBlank()) {
                    current.capturedPhotoUrls + (siteId to result.photoUrl)
                } else {
                    current.capturedPhotoUrls
                },
                historyPoints = if (isNewFeedPost) current.historyPoints + HISTORY_POINTS_PER_POST else current.historyPoints,
            )
        }
    }

    /**
     * Llamar cuando el usuario toca una silueta gris en AlbumScreen para
     * "pegarla". Si esta lámina era la última que faltaba de su página, la
     * página completa se marca como conseguida — eso desbloquea el título
     * de perfil y suma al multiplicador de XP (ver GamificationState).
     */
    fun pasteLamina(badgeCode: String) {
        _gamification.update { current ->
            if (badgeCode !in current.unlockedBadgeCodes) return@update current
            val newPasted = current.pastedBadgeCodes + badgeCode
            val site = sites.find { it.badge.code == badgeCode }
            val newCompletedPages = if (site != null) {
                val pageComplete = sites.filter { it.albumPage == site.albumPage }.all { it.badge.code in newPasted }
                if (pageComplete) current.completedPages + site.albumPage else current.completedPages
            } else {
                current.completedPages
            }
            current.copy(pastedBadgeCodes = newPasted, completedPages = newCompletedPages)
        }
    }

    /**
     * Reto fotográfico comunitario de la tarjeta de un hito (ver
     * ReviewsSection): otorga XP local al toque, una sola vez por sitio. Sin
     * verificación por IA de que la foto en verdad corresponda al reto —
     * limitación conocida de esta primera versión, documentada en
     * GamificationState. El multiplicador de páginas completas SÍ aplica
     * acá (a diferencia del XP de visita, que es autoridad del backend y
     * nunca se infla en el cliente).
     */
    fun completePhotoChallenge(siteId: String, bonusXp: Int) {
        _gamification.update { current ->
            if (siteId in current.completedPhotoChallengeSiteIds) return@update current
            current.copy(
                totalXp = current.totalXp + (bonusXp * current.xpMultiplier).toInt(),
                completedPhotoChallengeSiteIds = current.completedPhotoChallengeSiteIds + siteId,
            )
        }
    }

    /**
     * Gasta Puntos de Historia en una Pista: revela cuál es la misión sin
     * descubrir más cercana (nombre + distancia real si hay GPS). Devuelve
     * null si no hay puntos suficientes o si ya no queda nada por descubrir
     * — el llamador (FeedScreen) decide cómo comunicar cada caso.
     */
    fun redeemHint(): HintResult? {
        val current = _gamification.value
        if (current.historyPoints < HINT_COST_POINTS) return null
        val undiscovered = sites.filter { it.badge.code !in current.unlockedBadgeCodes }
        if (undiscovered.isEmpty()) return null
        val userLoc = _userLocation.value
        val target = if (userLoc != null) {
            undiscovered.minByOrNull { haversineMeters(userLoc.first, userLoc.second, it.lat, it.lng) }
        } else {
            undiscovered.first()
        } ?: return null
        val distanceMeters = userLoc?.let { haversineMeters(it.first, it.second, target.lat, target.lng).toInt() }
        _gamification.update { it.copy(historyPoints = it.historyPoints - HINT_COST_POINTS) }
        return HintResult(missionTitleEs = target.missionTitleEs, city = target.city, distanceMeters = distanceMeters)
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates()
    }
}

data class HintResult(val missionTitleEs: String, val city: String, val distanceMeters: Int?)
