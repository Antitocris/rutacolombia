package co.exploracolombia.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import co.exploracolombia.di.AppContainer
import co.exploracolombia.domain.model.toHistoricalSite
import co.exploracolombia.presentation.album.AlbumScreen
import co.exploracolombia.presentation.feed.FeedScreen
import co.exploracolombia.presentation.map.MapScreen
import co.exploracolombia.presentation.map.MapViewModel
import co.exploracolombia.presentation.theme.RutaColors
import co.exploracolombia.presentation.visit.VisitScreen
import co.exploracolombia.presentation.visit.VisitViewModel

private enum class RootTab(val labelEs: String) {
    MAP("Mapa"),
    ALBUM("Álbum"),
    FEED("Feed"),
}

/**
 * Navegación de la app: sin Navigation Compose a propósito (3 pestañas fijas
 * no necesitan un grafo completo) — una barra inferior persistente para
 * Mapa/Álbum/Feed, y la pantalla de Escaneo como overlay de pantalla
 * completa fuera de la barra (se llega tocando "Escanear" en la tarjeta de
 * un hito, ver SiteDetailSheet.kt). Las 3 pestañas comparten la MISMA
 * instancia de MapViewModel a propósito: "pegar" una lámina en el Álbum o
 * validar una visita se reflejan de inmediato en las otras pestañas.
 */
@Composable
fun AppRoot(appContainer: AppContainer) {
    val mapViewModel: MapViewModel = viewModel(factory = appContainer.mapViewModelFactory())
    var scanningSiteId by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedTab by rememberSaveable { mutableStateOf(RootTab.MAP) }
    val scanningSite = scanningSiteId?.let { id -> mapViewModel.sites.find { it.id == id } }

    BackHandler(enabled = scanningSite != null) {
        scanningSiteId = null
    }
    BackHandler(enabled = scanningSite == null && selectedTab != RootTab.MAP) {
        selectedTab = RootTab.MAP
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
                mapViewModel.onVisitCompleted(scanningSite.id, result)
                scanningSiteId = null
            },
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = RutaColors.JungleGreenDark) {
                NavigationBarItem(
                    selected = selectedTab == RootTab.MAP,
                    onClick = { selectedTab = RootTab.MAP },
                    icon = { Icon(Icons.Filled.Map, contentDescription = null) },
                    label = { Text(RootTab.MAP.labelEs) },
                    colors = tabColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == RootTab.ALBUM,
                    onClick = { selectedTab = RootTab.ALBUM },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                    label = { Text(RootTab.ALBUM.labelEs) },
                    colors = tabColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == RootTab.FEED,
                    onClick = { selectedTab = RootTab.FEED },
                    icon = { Icon(Icons.Filled.PhotoLibrary, contentDescription = null) },
                    label = { Text(RootTab.FEED.labelEs) },
                    colors = tabColors(),
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
            when (selectedTab) {
                RootTab.MAP -> MapScreen(
                    viewModel = mapViewModel,
                    reviewsViewModelFactory = appContainer::reviewsViewModelFactory,
                    onScanRequested = { site -> scanningSiteId = site.id },
                    onAlbumRequested = { selectedTab = RootTab.ALBUM },
                )
                RootTab.ALBUM -> AlbumScreen(
                    viewModel = mapViewModel,
                    onBack = { selectedTab = RootTab.MAP },
                )
                RootTab.FEED -> FeedScreen(viewModel = mapViewModel)
            }
        }
    }
}

@Composable
private fun tabColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = RutaColors.GoldInk,
    selectedTextColor = RutaColors.Gold,
    indicatorColor = RutaColors.Gold,
    unselectedIconColor = RutaColors.Parchment.copy(alpha = 0.6f),
    unselectedTextColor = RutaColors.Parchment.copy(alpha = 0.6f),
)
