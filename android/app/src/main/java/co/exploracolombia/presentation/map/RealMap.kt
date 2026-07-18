package co.exploracolombia.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

/** Bogotá, Plaza de Bolívar — solo se usa si todavía no hay ninguna posición real del GPS. */
private val FALLBACK_CENTER = GeoPoint(4.598146, -74.076004)
private const val DEFAULT_ZOOM = 15.5
private const val MIN_ZOOM = 2.5 // suficiente para ver el planeta completo
private const val MAX_ZOOM = 20.0
private const val BLINK_INTERVAL_MS = 650L

/**
 * Mapa real (OpenStreetMap vía osmdroid) — YA NO es un lienzo ilustrado.
 * Calles, ríos, parques y edificios de cualquier lugar del mundo vienen del
 * servidor público de tiles de OSM; pan/pinch-zoom/rotación son gestos
 * nativos de osmdroid, no hay que reimplementarlos a mano.
 */
@Composable
fun RealMap(
    sites: List<SiteBrief>,
    reachableSiteIds: Set<String>,
    pastedBadgeCodes: Set<String>,
    userLocation: Pair<Double, Double>?,
    onSiteTap: (SiteBrief) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            minZoomLevel = MIN_ZOOM
            maxZoomLevel = MAX_ZOOM
            overlays.add(RotationGestureOverlay(this).apply { isEnabled = true })
            controller.setZoom(DEFAULT_ZOOM)
            controller.setCenter(FALLBACK_CENTER)
        }
    }

    // osmdroid mantiene su propio estado interno de tiles/threads: hay que
    // avisarle explícitamente cuándo la pantalla está visible o no, igual
    // que con CameraX — si no, sigue pidiendo tiles en segundo plano.
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    var hasCenteredOnUser by remember { mutableStateOf(false) }

    LaunchedEffect(userLocation) {
        val (lat, lng) = userLocation ?: return@LaunchedEffect
        if (!hasCenteredOnUser) {
            mapView.controller.setCenter(GeoPoint(lat, lng))
            mapView.controller.setZoom(DEFAULT_ZOOM)
            hasCenteredOnUser = true
        }
    }

    var missionMarkers by remember { mutableStateOf<List<Marker>>(emptyList()) }

    LaunchedEffect(sites, reachableSiteIds, pastedBadgeCodes, userLocation) {
        missionMarkers = rebuildOverlays(context, mapView, sites, reachableSiteIds, pastedBadgeCodes, userLocation, onSiteTap)
    }

    // "Icono parpadeante" para las misiones sin coleccionar: osmdroid dibuja
    // sobre una Canvas de Android View, no de Compose, así que la animación
    // no puede ser un AnimatedFloat normal — se alterna el alpha del bitmap
    // a mano y se pide un redibujado (invalidate) en cada paso.
    LaunchedEffect(missionMarkers) {
        if (missionMarkers.isEmpty()) return@LaunchedEffect
        var bright = true
        while (true) {
            delay(BLINK_INTERVAL_MS)
            bright = !bright
            missionMarkers.forEach { it.alpha = if (bright) 1f else 0.45f }
            mapView.invalidate()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = {
                val target = userLocation?.let { (lat, lng) -> GeoPoint(lat, lng) } ?: FALLBACK_CENTER
                mapView.controller.animateTo(target)
                mapView.controller.setZoom(DEFAULT_ZOOM)
            },
            containerColor = RutaColors.Gold,
            contentColor = RutaColors.GoldInk,
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp),
        ) {
            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "Centrar en mi ubicación")
        }
    }
}

/** Devuelve los marcadores de sitios TODAVÍA NO pegados en el Álbum — esos son los que parpadean. */
private fun rebuildOverlays(
    context: android.content.Context,
    mapView: MapView,
    sites: List<SiteBrief>,
    reachableSiteIds: Set<String>,
    pastedBadgeCodes: Set<String>,
    userLocation: Pair<Double, Double>?,
    onSiteTap: (SiteBrief) -> Unit,
): List<Marker> {
    // Se limpian y se vuelven a agregar todos los overlays en cada
    // recomposición relevante — para el tamaño de catálogo de esta app
    // (unos pocos sitios) es más simple y menos propenso a bugs que llevar
    // un diff incremental de marcadores.
    mapView.overlays.removeAll { it is Marker }

    val missionMarkers = mutableListOf<Marker>()

    sites.forEach { site ->
        val reachable = reachableSiteIds.contains(site.id)
        val collected = pastedBadgeCodes.contains(site.badge.code)
        val marker = Marker(mapView).apply {
            position = GeoPoint(site.lat, site.lng)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = MarkerIconFactory.createLabeledPin(
                context = context,
                label = "Lámina #${site.laminaNumber} · ${site.titleEs}",
                rarity = site.badge.rarity,
                locked = !reachable,
            )
            setOnMarkerClickListener { _, _ ->
                onSiteTap(site)
                true
            }
        }
        mapView.overlays.add(marker)
        if (!collected) missionMarkers.add(marker)
    }

    if (userLocation != null) {
        val (lat, lng) = userLocation
        val youMarker = Marker(mapView).apply {
            position = GeoPoint(lat, lng)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = MarkerIconFactory.createUserDot(context)
        }
        mapView.overlays.add(youMarker)
    }

    mapView.invalidate()
    return missionMarkers
}
