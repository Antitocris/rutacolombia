package co.exploracolombia.presentation.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors
import kotlinx.coroutines.launch

/** Centro de referencia para posicionar pines — el centro histórico de Cartagena. */
private const val REGION_CENTER_LAT = 10.4236
private const val REGION_CENTER_LNG = -75.5478

/** Cuántos "grados de mundo" caben de borde a borde del lienzo con zoom 1x. */
private const val WORLD_SPAN_DEGREES = 0.016

private const val PIN_SIZE_DP = 52

// MIN_SCALE bajo a propósito: con 4 hitos repartidos en ~500m, hace falta
// poder alejar la cámara para verlos todos a la vez, no solo el más cercano.
private const val MIN_SCALE = 0.35f
private const val MAX_SCALE = 4f

/**
 * Mapa dibujado a mano (Canvas), no un mapa de calles real — ver la
 * explicación en la respuesta sobre por qué se evitó Google Maps SDK para
 * este MVP (evita la cuenta de Google Cloud + facturación + API key).
 *
 * SÍ tiene gestos táctiles reales: arrastrar (un dedo), zoom (pellizco) y
 * rotación (girar con dos dedos) — los tres vienen "gratis" del mismo
 * detector, `detectTransformGestures`. Lo único que NO se implementa es la
 * inclinación 3D ("tilt"): en un lienzo 2D ilustrado no hay terreno que
 * inclinar, y fingirlo con una transformación de perspectiva se vería
 * falso — ese gesto es exclusivo de un SDK de mapas 3D real.
 */
@Composable
fun StylizedMap(
    sites: List<SiteBrief>,
    reachableSiteIds: Set<String>,
    onSiteTap: (SiteBrief) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val infinite = rememberInfiniteTransition(label = "map-pulse")
    val radarProgress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart),
        label = "radar-progress",
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RutaColors.ParchmentDim, RutaColors.Parchment)))
            .pointerInput(Unit) {
                detectTransformGestures(panZoomLock = false) { _, pan, zoom, rot ->
                    scope.launch {
                        scale.snapTo((scale.value * zoom).coerceIn(MIN_SCALE, MAX_SCALE))
                        offsetX.snapTo(offsetX.value + pan.x)
                        offsetY.snapTo(offsetY.value + pan.y)
                    }
                    rotation += rot
                }
            },
    ) {
        val widthDp = maxWidth
        val heightDp = maxHeight

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offsetX.value
                    translationY = offsetY.value
                    rotationZ = rotation
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawTerrain(size)
            }

            sites.forEach { site ->
                val (nx, ny) = normalizedPosition(site.lat, site.lng)
                val reachable = reachableSiteIds.contains(site.id)
                val pinOffsetDp = PIN_SIZE_DP / 2

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = widthDp * nx - pinOffsetDp.dp,
                            y = heightDp * ny - pinOffsetDp.dp,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    MapPin(
                        rarity = site.badge.rarity,
                        locked = !reachable,
                        radarProgress = if (reachable) radarProgress else null,
                        onClick = { onSiteTap(site) },
                    )
                }
            }
        }

        // La brújula NO rota con el mapa: es una referencia fija de "hacia
        // dónde es el norte de la pantalla", como en cualquier app de mapas.
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCompassRose(Offset(size.width - 56f, 56f), 30f)
        }

        FloatingActionButton(
            onClick = {
                scope.launch {
                    launch { scale.animateTo(1f, tween(350)) }
                    launch { offsetX.animateTo(0f, tween(350)) }
                    launch { offsetY.animateTo(0f, tween(350)) }
                }
                rotation = 0f
            },
            containerColor = RutaColors.Gold,
            contentColor = RutaColors.GoldInk,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = "Centrar en mi ubicación")
        }
    }
}

/** Devuelve una posición normalizada (0f..1f, 0f..1f) dentro del lienzo. */
private fun normalizedPosition(lat: Double, lng: Double): Pair<Float, Float> {
    val dx = (lng - REGION_CENTER_LNG) / WORLD_SPAN_DEGREES
    val dy = (REGION_CENTER_LAT - lat) / WORLD_SPAN_DEGREES // lat crece hacia arriba en el mundo real, hacia abajo en pantalla
    val nx = (0.5 + dx).coerceIn(0.12, 0.88)
    val ny = (0.5 + dy).coerceIn(0.16, 0.8)
    return nx.toFloat() to ny.toFloat()
}

@Composable
private fun MapPin(
    rarity: BadgeRarity,
    locked: Boolean,
    radarProgress: Float?,
    onClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        if (radarProgress != null) {
            RadarRipple(
                progress = radarProgress,
                color = RutaColors.Gold,
                modifier = Modifier.size((PIN_SIZE_DP * 2.4f).dp),
            )
        }
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .size(PIN_SIZE_DP.dp),
            contentAlignment = Alignment.Center,
        ) {
            BadgeIcon(rarity = rarity, locked = locked, size = 44.dp)
        }
    }
}

/** Tres anillos escalonados que se expanden y se desvanecen — ping de radar, no un simple pulso. */
@Composable
private fun RadarRipple(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val maxRadius = size.minDimension / 2f
        repeat(3) { i ->
            val phase = (progress + i / 3f) % 1f
            val radius = maxRadius * phase
            val alpha = (1f - phase) * 0.55f
            drawCircle(color = color.copy(alpha = alpha.coerceIn(0f, 1f)), radius = radius, center = center)
        }
    }
}

private fun DrawScope.drawTerrain(canvasSize: Size) {
    val seaWidth = canvasSize.width * 0.2f

    // Franja de "mar" a la izquierda — Cartagena es costera, ancla el mapa visualmente.
    drawRect(
        color = Color(0xFFBFD8D4),
        topLeft = Offset(0f, 0f),
        size = Size(seaWidth, canvasSize.height),
    )

    drawWallsOutline(canvasSize, seaWidth)
    drawStreetGrid(canvasSize, seaWidth)
    drawParks(canvasSize)
}

/**
 * El contorno de "las murallas" que le dan nombre a la ruta: un polígono
 * irregular tipo fuerte estrellado, en línea punteada dorada — más temático
 * que un rectángulo, y refuerza visualmente de qué trata el juego.
 */
private fun DrawScope.drawWallsOutline(canvasSize: Size, seaWidth: Float) {
    val left = seaWidth + canvasSize.width * 0.04f
    val right = canvasSize.width * 0.94f
    val top = canvasSize.height * 0.12f
    val bottom = canvasSize.height * 0.88f

    val path = Path().apply {
        moveTo(left, top + (bottom - top) * 0.25f)
        lineTo(left + (right - left) * 0.18f, top)
        lineTo(left + (right - left) * 0.55f, top + (bottom - top) * 0.06f)
        lineTo(right, top + (bottom - top) * 0.22f)
        lineTo(right - (right - left) * 0.1f, bottom * 0.7f)
        lineTo(right, bottom)
        lineTo(left + (right - left) * 0.4f, bottom)
        lineTo(left, bottom - (bottom - top) * 0.2f)
        close()
    }
    drawPath(
        path,
        color = RutaColors.Gold.copy(alpha = 0.55f),
        style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f))),
    )
    drawPath(path, color = RutaColors.Gold.copy(alpha = 0.05f), style = Fill)
}

/** Calle principal + una cuadrícula irregular de calles secundarias, tipo centro colonial. */
private fun DrawScope.drawStreetGrid(canvasSize: Size, seaWidth: Float) {
    val streetColor = RutaColors.StoneGrey.copy(alpha = 0.35f)
    val avenueColor = RutaColors.StoneGrey.copy(alpha = 0.5f)

    // Avenida principal, paralela a la costa.
    drawLine(
        color = avenueColor,
        start = Offset(seaWidth + 12f, canvasSize.height * 0.1f),
        end = Offset(seaWidth + 12f, canvasSize.height * 0.9f),
        strokeWidth = 5f,
    )

    val verticalFractions = listOf(0.32f, 0.46f, 0.6f, 0.74f, 0.88f)
    verticalFractions.forEach { fx ->
        drawLine(
            color = streetColor,
            start = Offset(canvasSize.width * fx, canvasSize.height * 0.08f),
            end = Offset(canvasSize.width * fx, canvasSize.height * 0.92f),
            strokeWidth = 2.5f,
        )
    }

    val horizontalFractions = listOf(0.22f, 0.38f, 0.54f, 0.7f, 0.85f)
    horizontalFractions.forEach { fy ->
        drawLine(
            color = streetColor,
            start = Offset(seaWidth, canvasSize.height * fy),
            end = Offset(canvasSize.width * 0.94f, canvasSize.height * fy),
            strokeWidth = 2.5f,
        )
    }
}

/** Bloques verdes redondeados simulando plazas/parques del centro histórico. */
private fun DrawScope.drawParks(canvasSize: Size) {
    val parkColor = RutaColors.JungleGreenLight.copy(alpha = 0.4f)
    val parks = listOf(
        Offset(canvasSize.width * 0.4f, canvasSize.height * 0.3f) to canvasSize.minDimension * 0.07f,
        Offset(canvasSize.width * 0.68f, canvasSize.height * 0.68f) to canvasSize.minDimension * 0.06f,
        Offset(canvasSize.width * 0.5f, canvasSize.height * 0.75f) to canvasSize.minDimension * 0.045f,
    )
    parks.forEach { (center, radius) ->
        drawCircle(color = parkColor, radius = radius, center = center)
        drawCircle(color = parkColor.copy(alpha = 0.7f), radius = radius, center = center, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawCompassRose(center: Offset, r: Float) {
    val color = RutaColors.StoneGrey.copy(alpha = 0.5f)
    drawCircle(color = color, radius = r, center = center, style = Stroke(width = 2f))
    drawLine(color, Offset(center.x, center.y - r), Offset(center.x, center.y + r), strokeWidth = 2f)
    drawLine(color, Offset(center.x - r, center.y), Offset(center.x + r, center.y), strokeWidth = 2f)
}
