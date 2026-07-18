package co.exploracolombia.presentation.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import co.exploracolombia.domain.model.SiteBrief

/**
 * Pantalla de entrada de la app (ver MainActivity/AppRoot): panel de
 * gamificación arriba, mapa abajo. Tocar un pin abre SiteDetailSheet; su
 * botón "Escanear" dispara [onScanRequested] con el sitio elegido — ese
 * callback es responsabilidad de AppRoot, que decide navegar a ScanScreen.
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

    Column(modifier = Modifier.fillMaxSize()) {
        GamificationHeader(gamification = gamification, sites = viewModel.sites)

        // Mientras exista solo un modo simulado, cualquier sitio del catálogo
        // se considera "alcanzable" para que su pin aparezca activo en el
        // mapa — cuando haya GPS real de vuelta, esto se calcula con
        // haversineMeters() contra la ubicación en vivo (ver GeoMath.kt).
        val reachableSiteIds = viewModel.sites.map { it.id }.toSet()

        StylizedMap(
            sites = viewModel.sites,
            reachableSiteIds = reachableSiteIds,
            onSiteTap = { viewModel.selectSite(it) },
            modifier = Modifier.weight(1f),
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
