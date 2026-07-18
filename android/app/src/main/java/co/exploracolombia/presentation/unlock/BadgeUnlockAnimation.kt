package co.exploracolombia.presentation.unlock

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import co.exploracolombia.domain.model.VisitResult
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Coreografía completa del desbloqueo: la cámara "se congela", un destello
 * blanco corta a un cofre 2D que tiembla y se abre, dispara partículas, y
 * el contenido se resuelve en un flip 3D hacia la tarjeta de la insignia.
 * Los tiempos vienen de UnlockPhase; la física final usa `spring()` para que
 * el asentamiento de la tarjeta se sienta "vivo" y no simplemente lineal.
 */
@Composable
fun BadgeUnlockAnimation(
    result: VisitResult,
    onDismiss: () -> Unit,
) {
    var phase by remember { mutableStateOf(UnlockPhase.FLASH) }

    LaunchedEffect(result) {
        for (next in UnlockPhase.entries) {
            phase = next
            kotlinx.coroutines.delay(next.durationMs.toLong())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        FlashOverlay(visible = phase == UnlockPhase.FLASH)

        if (phase == UnlockPhase.CHEST_SHAKE || phase == UnlockPhase.CHEST_OPEN) {
            ChestBurst(isOpening = phase == UnlockPhase.CHEST_OPEN)
        }

        if (phase == UnlockPhase.CARD_REVEAL || phase == UnlockPhase.SETTLE) {
            BadgeCardReveal(
                result = result,
                settled = phase == UnlockPhase.SETTLE,
                onContinue = onDismiss,
            )
        }
    }
}

@Composable
private fun FlashOverlay(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(90)),
        exit = fadeOut(tween(90)),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White))
    }
}

/** Cofre que tiembla y luego se "parte" en dos mitades que se separan, con partículas de luz. */
@Composable
private fun ChestBurst(isOpening: Boolean) {
    val shakeX = remember { Animatable(0f) }
    val lidOffsetY = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Temblor: 4 oscilaciones cortas antes de abrir.
        repeat(4) {
            shakeX.animateTo(14f, tween(45, easing = LinearOutSlowInEasing))
            shakeX.animateTo(-14f, tween(45, easing = LinearOutSlowInEasing))
        }
        shakeX.animateTo(0f, tween(30))
    }

    LaunchedEffect(isOpening) {
        if (isOpening) {
            lidOffsetY.animateTo(-120f, tween(350, easing = EaseOutBack))
            glowAlpha.animateTo(1f, tween(250))
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
        if (isOpening) {
            ParticleField(alpha = glowAlpha.value)
        }

        Box(
            modifier = Modifier
                .graphicsLayer { translationX = shakeX.value }
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFF4C542), Color(0xFFB8801E))),
                ),
        )

        // Tapa del cofre: se desplaza hacia arriba al abrir, simulando la bisagra.
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = shakeX.value
                    translationY = lidOffsetY.value
                    rotationX = if (isOpening) -35f else 0f
                }
                .size(width = 160.dp, height = 56.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFFFFD873)),
        )
    }
}

@Composable
private fun ParticleField(alpha: Float) {
    val particles = remember {
        List(24) {
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = Random.nextFloat() * 180f + 60f
            Offset(x = cos(angle).toFloat() * speed, y = sin(angle).toFloat() * speed)
        }
    }
    val progress by animateFloatAsState(
        targetValue = if (alpha > 0f) 1f else 0f,
        animationSpec = tween(600, easing = LinearOutSlowInEasing),
        label = "particle-progress",
    )

    Canvas(modifier = Modifier.size(280.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        particles.forEach { vector ->
            val pos = center + vector * progress
            drawCircle(
                color = Color(0xFFFFE08A).copy(alpha = alpha * (1f - progress) + 0.15f),
                radius = 6f * (1f - progress * 0.6f),
                center = pos,
            )
        }
    }
}

/**
 * Flip 3D de la tarjeta de insignia: rota en Y de 90°→0° para simular que
 * "voltea" hacia el usuario, seguido de un settle con spring bouncy.
 */
@Composable
private fun BadgeCardReveal(
    result: VisitResult,
    settled: Boolean,
    onContinue: () -> Unit,
) {
    val rotationY = remember { Animatable(90f) }
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        rotationY.animateTo(0f, tween(400, easing = EaseOutBack))
    }
    LaunchedEffect(settled) {
        if (settled) {
            scale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            )
        }
    }

    Column(
        modifier = Modifier
            .graphicsLayer {
                this.rotationY = rotationY.value
                this.scaleX = scale.value
                this.scaleY = scale.value
                cameraDistance = 12f * density
                compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
            }
            .padding(24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(badgeBackgroundBrush(result))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        result.badge?.let { badge ->
            AsyncImage(
                model = badge.iconUrl,
                contentDescription = badge.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp),
            )
            Text(
                text = badge.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Text(
            text = "+${result.xpAwarded} XP",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFE08A),
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = result.narrative,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(top = 12.dp),
        )

        if (settled) {
            Button(onClick = onContinue, modifier = Modifier.padding(top = 20.dp)) {
                Text("Continuar explorando")
            }
        }
    }
}

private fun badgeBackgroundBrush(result: VisitResult) = Brush.verticalGradient(
    when (result.badge?.rarity?.name) {
        "LEGENDARY" -> listOf(Color(0xFFB8860B), Color(0xFF5C3D0A))
        "EPIC" -> listOf(Color(0xFF7B2FF7), Color(0xFF3B0F70))
        "RARE" -> listOf(Color(0xFF2F80ED), Color(0xFF14355E))
        else -> listOf(Color(0xFF3A7D5C), Color(0xFF1E3D2C))
    },
)
