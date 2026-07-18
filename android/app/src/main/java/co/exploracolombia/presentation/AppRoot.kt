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
import co.exploracolombia.presentation.album.AlbumScreen
import co.exploracolombia.presentation.map.MapScreen
import co.exploracolombia.presentation.map.MapViewModel
import co.exploracolombia.presentation.visit.VisitScreen
import co.exploracolombia.presentation.visit.VisitViewModel

/**
 * Navegación de la app: sin Navigation Compose a propósito (un grafo
 * completo es de más para 3 pantallas) — solo un id de sitio en pantalla de
 * escaneo, o el Álbum, o ninguno de los dos. Mapa es la puerta de entrada;
 * Escaneo solo se alcanza tocando "Escanear" en la tarjeta de un hito (ver
 * SiteDetailSheet.kt); Álbum se alcanza desde el acceso directo del panel de
 * gamificación (ver GamificationHeader.kt). Álbum y Mapa comparten la MISMA
 * instancia de MapViewModel a propósito: "pegar" una lámina en el Álbum debe
 * reflejarse de inmediato en el contador del panel del mapa.
 */
@Composable
fun AppRoot(appContainer: AppContainer) {
    val mapViewModel: MapViewModel = viewModel(factory = appContainer.mapViewModelFactory())
    var scanningSiteId by rememberSaveable { mutableStateOf<String?>(null) }
    var showAlbum by rememberSaveable { mutableStateOf(false) }
    val scanningSite = scanningSiteId?.let { id -> mapViewModel.sites.find { it.id == id } }

    BackHandler(enabled = scanningSite != null) {
        scanningSiteId = null
    }
    BackHandler(enabled = scanningSite == null && showAlbum) {
        showAlbum = false
    }

    if (scanningSite != null) {
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
    } else if (showAlbum) {
        AlbumScreen(
            viewModel = mapViewModel,
            onBack = { showAlbum = false },
        )
    } else {
        MapScreen(
            viewModel = mapViewModel,
            onScanRequested = { site -> scanningSiteId = site.id },
            onAlbumRequested = { showAlbum = true },
        )
    }
}
