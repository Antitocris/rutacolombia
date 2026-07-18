package co.exploracolombia.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import co.exploracolombia.di.AppContainer
import co.exploracolombia.domain.model.toHistoricalSite
import co.exploracolombia.presentation.map.MapScreen
import co.exploracolombia.presentation.map.MapViewModel
import co.exploracolombia.presentation.visit.VisitScreen
import co.exploracolombia.presentation.visit.VisitViewModel

/**
 * Navegación de la app: sin Navigation Compose a propósito (un grafo
 * completo es de más para 2 pantallas) — solo un id de sitio en pantalla de
 * escaneo o no. Mapa es la puerta de entrada; Escaneo solo se alcanza
 * tocando "Escanear" en la tarjeta de un hito (ver SiteDetailSheet.kt).
 */
@Composable
fun AppRoot(appContainer: AppContainer) {
    val mapViewModel: MapViewModel = viewModel()
    var scanningSiteId by rememberSaveable { mutableStateOf<String?>(null) }
    val scanningSite = scanningSiteId?.let { id -> mapViewModel.sites.find { it.id == id } }

    BackHandler(enabled = scanningSite != null) {
        scanningSiteId = null
    }

    if (scanningSite == null) {
        MapScreen(
            viewModel = mapViewModel,
            onScanRequested = { site -> scanningSiteId = site.id },
        )
    } else {
        val visitViewModel: VisitViewModel = viewModel(
            key = "scan-${scanningSite.id}",
            factory = appContainer.visitViewModelFactory(scanningSite.toHistoricalSite(isUnlocked = false)),
        )
        VisitScreen(
            viewModel = visitViewModel,
            badgeRarity = scanningSite.badge.rarity,
            onExit = { scanningSiteId = null },
            onVisitSuccess = { result ->
                mapViewModel.onVisitCompleted(result)
                scanningSiteId = null
            },
        )
    }
}
