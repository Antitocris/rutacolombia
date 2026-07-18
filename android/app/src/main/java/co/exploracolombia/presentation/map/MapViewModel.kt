package co.exploracolombia.presentation.map

import androidx.lifecycle.ViewModel
import co.exploracolombia.data.local.SiteCatalog
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.domain.model.VisitResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {

    val sites: List<SiteBrief> = SiteCatalog.all

    private val _gamification = MutableStateFlow(GamificationState())
    val gamification: StateFlow<GamificationState> = _gamification.asStateFlow()

    private val _selectedSite = MutableStateFlow<SiteBrief?>(null)
    val selectedSite: StateFlow<SiteBrief?> = _selectedSite.asStateFlow()

    fun selectSite(site: SiteBrief) {
        _selectedSite.value = site
    }

    fun dismissSiteDetail() {
        _selectedSite.value = null
    }

    /** Llamar cuando ScanScreen termina una visita con éxito. */
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
}
