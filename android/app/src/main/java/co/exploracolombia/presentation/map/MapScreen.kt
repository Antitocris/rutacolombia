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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteBrief

/**
 * Pantalla de entrada de la app (ver MainActivity/AppRoot). El mapa ocupa
 * TODA la pantalla — es el lienzo del juego — y el panel de gamificación
 * flota encima como una tarjeta con sombra, no como una franja que lo
 * divide en dos. Tocar un pin abre SiteDetailSheet; su botón "Escanear"
 * dispara [onScanRequested], que AppRoot usa para navegar a ScanScreen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onScanRequested: (SiteBrief) -> Unit,
) {
    val gamification by viewModel.gamification.collectAsState()
    val selectedSite by viewModel.selectedSite.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Mientras exista solo modo simulado, cualquier sitio del catálogo
        // se considera "alcanzable" para que su pin aparezca activo en el
        // mapa — cuando haya GPS real de vuelta, esto se calcula con
        // haversineMeters() contra la ubicación en vivo (ver GeoMath.kt).
        val reachableSiteIds = viewModel.sites.map { it.id }.toSet()

        StylizedMap(
            sites = viewModel.sites,
            reachableSiteIds = reachableSiteIds,
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
