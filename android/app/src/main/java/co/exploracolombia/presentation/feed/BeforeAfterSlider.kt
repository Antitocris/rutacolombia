package co.exploracolombia.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Comparador "antes/después" arrastrable, estilo publicación de red social:
 * la foto histórica real queda fija de fondo a todo el ancho, la foto actual
 * del usuario se dibuja encima a ese MISMO ancho pero recortada a la
 * fracción del slider — así el recorte no reescala la imagen, solo revela
 * más o menos de ella según dónde esté el dedo.
 */
@Composable
fun BeforeAfterSlider(
    beforeImageUrl: String,
    afterImageUrl: String,
    beforeLabel: String,
    afterLabel: String,
    modifier: Modifier = Modifier,
) {
    var sliderFraction by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(18.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consume()
                    sliderFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                }
            },
    ) {
        val fullWidth = maxWidth

        AsyncImage(
            model = beforeImageUrl,
            contentDescription = beforeLabel,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(sliderFraction)
                .clipToBounds(),
        ) {
            AsyncImage(
                model = afterImageUrl,
                contentDescription = afterLabel,
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(fullWidth).fillMaxHeight(),
            )
        }

        val handleOffset = fullWidth * sliderFraction
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(start = handleOffset - 1.dp)
                .width(2.dp)
                .background(Color.White),
        )
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = handleOffset - 18.dp)
                .size(36.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Arrastra para comparar", tint = RutaColors.JungleGreenDark, modifier = Modifier.size(20.dp))
            }
        }

        LabelChip(text = beforeLabel, modifier = Modifier.align(Alignment.TopStart).padding(10.dp))
        LabelChip(text = afterLabel, modifier = Modifier.align(Alignment.TopEnd).padding(10.dp))
    }
}

@Composable
private fun LabelChip(text: String, modifier: Modifier = Modifier) {
    Surface(color = Color.Black.copy(alpha = 0.55f), shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}
