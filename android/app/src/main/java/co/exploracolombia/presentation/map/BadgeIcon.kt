package co.exploracolombia.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.presentation.theme.RutaColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Insignia dibujada a mano (forma de escudo/medalla) en vez de cargar un
 * ícono de red: se ve igual sin conexión, nunca "parpadea" mientras carga,
 * y el mismo dibujo sirve tanto bloqueado (silueta oscura, para motivar a
 * completarlo) como desbloqueado (color según rareza + destello).
 */
@Composable
fun BadgeIcon(
    rarity: BadgeRarity,
    locked: Boolean,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val fillColor = if (locked) RutaColors.JungleGreenDark.copy(alpha = 0.55f) else RutaColors.raritySolid(rarity)
    val strokeColor = if (locked) RutaColors.StoneGrey.copy(alpha = 0.5f) else RutaColors.Gold

    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        if (!locked) {
            drawCircle(
                color = RutaColors.GoldSoft.copy(alpha = 0.35f),
                radius = w * 0.62f,
                center = Offset(w / 2f, h / 2f),
            )
        }

        val shield = Path().apply {
            moveTo(w * 0.5f, h * 0.06f)
            lineTo(w * 0.88f, h * 0.2f)
            lineTo(w * 0.88f, h * 0.55f)
            cubicTo(w * 0.88f, h * 0.8f, w * 0.7f, h * 0.94f, w * 0.5f, h * 1f)
            cubicTo(w * 0.3f, h * 0.94f, w * 0.12f, h * 0.8f, w * 0.12f, h * 0.55f)
            lineTo(w * 0.12f, h * 0.2f)
            close()
        }
        drawPath(shield, color = fillColor, style = Fill)
        drawPath(shield, color = strokeColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.035f))

        // Estrella central de 5 puntas — el mismo trazo para todas las rarezas,
        // lo que cambia es el color de relleno del escudo detrás.
        val starColor = if (locked) RutaColors.StoneGrey.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.92f)
        val outerR = w * 0.2f
        val innerR = w * 0.09f
        val cx = w * 0.5f
        val cy = h * 0.48f
        val star = Path()
        for (i in 0 until 10) {
            val angle = -Math.PI / 2 + i * Math.PI / 5
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()
            if (i == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()
        drawPath(star, color = starColor, style = Fill)
    }
}
