package co.exploracolombia.presentation.map

import android.graphics.Point
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
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
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

/** Radio de agrupación en píxeles de pantalla — no en metros, a propósito: así el
 *  mismo umbral "se ve" igual de apretado en cualquier zoom, y los grupos se
 *  separan solos al acercar porque los píxeles entre dos puntos geográficos
 *  fijos crecen con el zoom (sin necesidad de recalcular el radio). */
private const val CLUSTER_RADIUS_PX = 100

/**
 * Basemap "limpio" (CartoDB Voyager) en vez del Mapnik estándar de OSM: menos
 * ruido de rótulos secundarios (nombres de barrio diminutos, íconos de POI
 * ajenos a la app) y una paleta de grises/verdes suaves — se ve a propósito, no
 * la calcomanía técnica de un mapa de navegación GPS. Sigue siendo 100% datos
 * de OpenStreetMap por debajo, solo cambia el estilo de dibujo; no requiere
 * API key. Atribución obligatoria de CARTO incluida en el copyright del tile source.
 */
private val CARTO_VOYAGER = object : OnlineTileSourceBase(
    "CartoDBVoyager",
    0,
    20,
    256,
    ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
        "https://d.basemaps.cartocdn.com/rastertiles/voyager/",
    ),
    "© OpenStreetMap contributors © CARTO",
) {
    override fun getTileURLString(pMapTileIndex: Long): String =
        baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
            MapTileIndex.getX(pMapTileIndex) + "/" +
            MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
}

/**
 * Mapa real (OpenStreetMap vía osmdroid, con basemap CartoDB Voyager) — pan,
 * pinch-zoom y rotación son gestos nativos de osmdroid. Los hitos cercanos
 * entre sí se agrupan en un círculo con contador (estilo "Pokémon GO") para
 * que las etiquetas nunca se pisen; tocar un grupo hace zoom hacia él para
 * separarlo en sus hitos individuales.
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
            setTileSource(CARTO_VOYAGER)
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

    // "Época" que se incrementa cada vez que el usuario mueve o hace zoom al
    // mapa — los grupos se recalculan en píxeles de pantalla, así que
    // cualquier cambio de cámara puede separar o unir hitos.
    var cameraEpoch by remember { mutableStateOf(0) }

    DisposableEffect(mapView) {
        val listener = object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                cameraEpoch++
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                cameraEpoch++
                return true
            }
        }
        mapView.addMapListener(listener)
        onDispose { mapView.removeMapListener(listener) }
    }

    var missionMarkers by remember { mutableStateOf<List<Marker>>(emptyList()) }

    LaunchedEffect(sites, reachableSiteIds, pastedBadgeCodes, userLocation, cameraEpoch) {
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

private class MarkerCluster(seed: SiteBrief) {
    val sites = mutableListOf(seed)
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
    val projection = mapView.projection

    // Clustering greedy por distancia en píxeles de pantalla (ver
    // CLUSTER_RADIUS_PX): cada semilla arrastra a los sitios cercanos
    // todavía sin asignar. No es el algoritmo más sofisticado, pero para
    // unas pocas docenas de hitos es indistinguible en resultado de uno más
    // elaborado, y no requiere ninguna librería extra.
    val screenPoints = sites.associateWith { site ->
        val point = Point()
        projection.toPixels(GeoPoint(site.lat, site.lng), point)
        point
    }
    val unassigned = sites.toMutableList()
    val clusters = mutableListOf<MarkerCluster>()
    while (unassigned.isNotEmpty()) {
        val seed = unassigned.removeAt(0)
        val cluster = MarkerCluster(seed)
        val seedPoint = screenPoints.getValue(seed)
        val iterator = unassigned.iterator()
        while (iterator.hasNext()) {
            val candidate = iterator.next()
            val candidatePoint = screenPoints.getValue(candidate)
            val dx = (candidatePoint.x - seedPoint.x).toDouble()
            val dy = (candidatePoint.y - seedPoint.y).toDouble()
            if (kotlin.math.sqrt(dx * dx + dy * dy) <= CLUSTER_RADIUS_PX) {
                cluster.sites.add(candidate)
                iterator.remove()
            }
        }
        clusters.add(cluster)
    }

    clusters.forEach { cluster ->
        if (cluster.sites.size == 1) {
            val site = cluster.sites.first()
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
        } else {
            val centerLat = cluster.sites.sumOf { it.lat } / cluster.sites.size
            val centerLng = cluster.sites.sumOf { it.lng } / cluster.sites.size
            val hasPendingMission = cluster.sites.any { !pastedBadgeCodes.contains(it.badge.code) }
            val cities = cluster.sites.map { it.city }.toSet()
            val label = if (cities.size == 1) "${cities.first()}: ${cluster.sites.size} láminas" else "${cluster.sites.size} láminas"

            val clusterMarker = Marker(mapView).apply {
                position = GeoPoint(centerLat, centerLng)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                icon = MarkerIconFactory.createClusterBadge(
                    context = context,
                    count = cluster.sites.size,
                    label = label,
                    hasPendingMission = hasPendingMission,
                )
                setOnMarkerClickListener { _, _ ->
                    val lats = cluster.sites.map { it.lat }
                    val lngs = cluster.sites.map { it.lng }
                    // Un poco de margen alrededor del grupo para que, tras el
                    // zoom, los pines no queden pegados al borde de la pantalla.
                    val latPad = ((lats.max() - lats.min()).coerceAtLeast(0.002)) * 0.6
                    val lngPad = ((lngs.max() - lngs.min()).coerceAtLeast(0.002)) * 0.6
                    val box = BoundingBox(
                        lats.max() + latPad,
                        lngs.max() + lngPad,
                        lats.min() - latPad,
                        lngs.min() - lngPad,
                    )
                    mapView.zoomToBoundingBox(box, true)
                    true
                }
            }
            mapView.overlays.add(clusterMarker)
            if (hasPendingMission) missionMarkers.add(clusterMarker)
        }
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
