package co.exploracolombia.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.exploracolombia.domain.model.SiteBrief

/**
 * Pantalla de entrada de la app (ver MainActivity/AppRoot). Mapa real
 * (OpenStreetMap, ver RealMap.kt) a pantalla completa; el panel de
 * gamificación flota encima como una tarjeta con sombra. Tocar un pin abre
 * SiteDetailSheet; su botón "Escanear" dispara [onScanRequested].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onScanRequested: (SiteBrief) -> Unit,
) {
    LocationPermissionGate {
        MapContent(viewModel, onScanRequested)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapContent(
    viewModel: MapViewModel,
    onScanRequested: (SiteBrief) -> Unit,
) {
    val gamification by viewModel.gamification.collectAsState()
    val selectedSite by viewModel.selectedSite.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val reachableSiteIds by viewModel.reachableSiteIds.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Ubicación de bajo consumo solo mientras el mapa está en foreground —
    // misma disciplina de batería que ScanScreen (ver android/ARCHITECTURE.md).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startLocationUpdates()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopLocationUpdates()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopLocationUpdates()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        RealMap(
            sites = viewModel.sites,
            reachableSiteIds = reachableSiteIds,
            userLocation = userLocation,
            onSiteTap = { viewModel.selectSite(it) },
            modifier = Modifier.fillMaxSize(),
        )

        GamificationHeader(
            gamification = gamification,
            sites = viewModel.sites,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }

    selectedSite?.let { site ->
        SiteDetailSheet(
            site = site,
            sheetState = sheetState,
            onDismiss = { viewModel.dismissSiteDetail() },
            onScanClick = {
                viewModel.dismissSiteDetail()
                onScanRequested(site)
            },
        )
    }
}
