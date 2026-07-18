package co.exploracolombia.presentation.album

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.map.LaminaState
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Una lámina del Álbum, con sus 3 estados (ver LaminaState): bloqueada
 * (silueta gris, no interactiva), conseguida-sin-pegar (silueta gris con
 * brillo dorado, tocar la "pega") y pegada (a todo color, tocar la voltea
 * — ver LaminaFlipCard). La transición gris -> color ES la animación de
 * "pegar el monito", con AnimatedContent (no hace falta una pantalla
 * completa aparte para eso).
 */
@Composable
fun LaminaSlot(
    site: SiteBrief,
    state: LaminaState,
    onPaste: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickable = state != LaminaState.LOCKED
    Box(
        modifier = modifier
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(14.dp))
            .then(
                if (clickable) {
                    Modifier.clickable {
                        if (state == LaminaState.EARNED_UNPASTED) onPaste() else onFlip()
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        AnimatedContent(
            targetState = state == LaminaState.PASTED,
            transitionSpec = {
                (fadeIn(tween(280)) + scaleIn(tween(280), initialScale = 0.85f))
                    .togetherWith(fadeOut(tween(120)))
            },
            label = "lamina-paste",
        ) { pasted ->
            if (pasted) {
                PastedFace(site = site)
            } else {
                GreySilhouetteFace(site = site, glowing = state == LaminaState.EARNED_UNPASTED)
            }
        }
    }
}

@Composable
private fun PastedFace(site: SiteBrief) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = site.coverImageUrl,
            contentDescription = site.titleEs,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))))
                .padding(top = 24.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
        ) {
            Text(
                text = site.titleEs,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LaminaNumberBadge(number = site.laminaNumber, modifier = Modifier.align(Alignment.TopStart).padding(6.dp))
    }
}

@Composable
private fun GreySilhouetteFace(site: SiteBrief, glowing: Boolean) {
    val borderColor = if (glowing) RutaColors.Gold else RutaColors.StoneGrey.copy(alpha = 0.3f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (glowing) RutaColors.JungleGreenDark else RutaColors.ParchmentDim)
            .padding(2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (glowing) RutaColors.JungleGreen else RutaColors.ParchmentDim),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            SilhouetteIcon(
                tint = if (glowing) RutaColors.Gold.copy(alpha = 0.85f) else RutaColors.StoneGrey.copy(alpha = 0.4f),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp, start = 6.dp, end = 6.dp),
        ) {
            Text(
                text = if (glowing) site.titleEs else "¿ ? ? ?",
                color = if (glowing) RutaColors.Gold else RutaColors.StoneGrey,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        LaminaNumberBadge(
            number = site.laminaNumber,
            modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
            dimmed = !glowing,
        )
    }
}

@Composable
private fun LaminaNumberBadge(number: Int, modifier: Modifier = Modifier, dimmed: Boolean = false) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (dimmed) Color.Black.copy(alpha = 0.25f) else RutaColors.Gold)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    ) {
        Text(
            text = "#$number",
            color = if (dimmed) Color.White.copy(alpha = 0.7f) else RutaColors.GoldInk,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

/** Silueta genérica de monumento — no hay arte único por sitio todavía, así que se usa la misma forma para las 7 láminas. */
@Composable
private fun SilhouetteIcon(tint: Color) {
    Canvas(modifier = Modifier.fillMaxSize().padding(28.dp)) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            lineTo(w * 0.72f, h * 0.32f)
            lineTo(w * 0.62f, h * 0.32f)
            lineTo(w * 0.62f, h * 0.85f)
            lineTo(w * 0.38f, h * 0.85f)
            lineTo(w * 0.38f, h * 0.32f)
            lineTo(w * 0.28f, h * 0.32f)
            close()
        }
        drawPath(path, color = tint, style = Fill)
        drawRect(color = tint, topLeft = Offset(w * 0.18f, h * 0.85f), size = androidx.compose.ui.geometry.Size(w * 0.64f, h * 0.06f))
    }
}
