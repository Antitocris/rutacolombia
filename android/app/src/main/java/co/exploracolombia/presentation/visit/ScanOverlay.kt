package co.exploracolombia.presentation.visit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.presentation.map.BadgeIcon
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Visor de "cacería de tesoros" sobre la vista de cámara: cuatro esquinas
 * estilo HUD de aventura (no un recuadro genérico de app de fotos) y, en el
 * centro, la silueta de la insignia que se está por ganar — refuerza que la
 * cámara es el paso final de una colección, no el propósito de la pantalla.
 */
@Composable
fun ScanOverlay(rarity: BadgeRarity, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val margin = size.width * 0.1f
            val frameSize = Size(size.width - margin * 2, size.width - margin * 2)
            val topLeft = Offset(margin, size.height / 2f - frameSize.height / 2f)
            drawViewfinderCorners(topLeft, frameSize)
        }

        BadgeIcon(rarity = rarity, locked = true, size = 96.dp)
    }
}

private fun DrawScope.drawViewfinderCorners(topLeft: Offset, frameSize: Size) {
    val cornerLength = frameSize.width * 0.12f
    val strokeWidth = 6f
    val color = RutaColors.Gold
    val corners = listOf(
        // esquina, dirección x, dirección y
        Triple(topLeft, 1f, 1f),
        Triple(Offset(topLeft.x + frameSize.width, topLeft.y), -1f, 1f),
        Triple(Offset(topLeft.x, topLeft.y + frameSize.height), 1f, -1f),
        Triple(Offset(topLeft.x + frameSize.width, topLeft.y + frameSize.height), -1f, -1f),
    )
    corners.forEach { (corner, dx, dy) ->
        val path = Path().apply {
            moveTo(corner.x, corner.y + cornerLength * dy)
            lineTo(corner.x, corner.y)
            lineTo(corner.x + cornerLength * dx, corner.y)
        }
        drawPath(path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }

    // Línea punteada tenue conectando las esquinas, para que el visor se
    // lea como un marco completo sin tapar la escena de la cámara.
    drawRoundRect(
        color = color.copy(alpha = 0.35f),
        topLeft = topLeft,
        size = frameSize,
        cornerRadius = CornerRadius(24f, 24f),
        style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f))),
    )
}
