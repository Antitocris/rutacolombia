package co.exploracolombia.presentation.map

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors

/** Centro de referencia para posicionar pines — el centro histórico de Cartagena. */
private const val REGION_CENTER_LAT = 10.4236
private const val REGION_CENTER_LNG = -75.5478

/** Cuántos "grados de mundo" caben de borde a borde del lienzo — controla el zoom ilustrado. */
private const val WORLD_SPAN_DEGREES = 0.01

private const val PIN_SIZE_DP = 52

/**
 * Mapa dibujado a mano (Canvas), no un mapa de calles real — ver la
 * explicación en la respuesta sobre por qué se evitó Google Maps SDK para
 * este MVP. Posiciona los pines por la diferencia real de lat/lng contra
 * REGION_CENTER, así que si mañana hay 5 sitios en vez de 1, se van a
 * distribuir de forma consistente con su ubicación real relativa, no al azar.
 */
@Composable
fun StylizedMap(
    sites: List<SiteBrief>,
    reachableSiteIds: Set<String>,
    onSiteTap: (SiteBrief) -> Unit,
    modifier: Modifier = Modifier,
) {
    val infinite = rememberInfiniteTransition(label = "map-pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "pin-pulse",
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(RutaColors.ParchmentDim, RutaColors.Parchment))),
    ) {
        val widthDp = maxWidth
        val heightDp = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTerrain(size)
            drawCompassRose(Offset(size.width - 56f, 56f), 30f)
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
                    pulseScale = if (reachable) pulse else 1f,
                    onClick = { onSiteTap(site) },
                )
            }
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
    pulseScale: Float,
    onClick: () -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        if (!locked) {
            Box(
                modifier = Modifier
                    .size(PIN_SIZE_DP.dp * pulseScale)
                    .background(RutaColors.Gold.copy(alpha = 0.25f), shape = CircleShape),
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

private fun DrawScope.drawTerrain(canvasSize: Size) {
    // Franja de "mar" a la izquierda — Cartagena es costera, ancla el mapa visualmente.
    drawRect(
        color = Color(0xFFBFD8D4),
        topLeft = Offset(0f, 0f),
        size = Size(canvasSize.width * 0.22f, canvasSize.height),
    )
    // Un par de curvas de nivel suaves, decorativas, para que no se vea vacío.
    val contourColor = RutaColors.StoneGrey.copy(alpha = 0.18f)
    for (i in 1..3) {
        val r = canvasSize.minDimension * (0.25f + i * 0.14f)
        drawCircle(
            color = contourColor,
            radius = r,
            center = Offset(canvasSize.width * 0.62f, canvasSize.height * 0.55f),
            style = Stroke(width = 2.5f),
        )
    }
}

private fun DrawScope.drawCompassRose(center: Offset, r: Float) {
    val color = RutaColors.StoneGrey.copy(alpha = 0.5f)
    drawCircle(color = color, radius = r, center = center, style = Stroke(width = 2f))
    drawLine(color, Offset(center.x, center.y - r), Offset(center.x, center.y + r), strokeWidth = 2f)
    drawLine(color, Offset(center.x - r, center.y), Offset(center.x + r, center.y), strokeWidth = 2f)
}
