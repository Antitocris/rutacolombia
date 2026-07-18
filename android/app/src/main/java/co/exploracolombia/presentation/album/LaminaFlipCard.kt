package co.exploracolombia.presentation.album

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors

/**
 * El reverso de la lámina, "tal cual las láminas de Jet": se abre con un
 * giro 3D (rotationY) como si la estuvieras dando vuelta con la mano, y
 * muestra el dato curioso + el XP que ganaste, no la historia formal (esa
 * ya se mostró en el mapa antes de escanear).
 */
@Composable
fun LaminaFlipCard(site: SiteBrief, totalXp: Int, onDismiss: () -> Unit) {
    val rotation = remember { Animatable(90f) }

    LaunchedEffect(site.id) {
        rotation.animateTo(0f, tween(420, easing = EaseOutBack))
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = RutaColors.raritySolid(site.badge.rarity),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 14f * density
                    compositingStrategy = CompositingStrategy.Offscreen
                },
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lámina #${site.laminaNumber}",
                            color = RutaColors.Gold,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = site.titleEs,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        text = site.funFactEs,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(16.dp),
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = site.funFactEn,
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = RutaColors.Gold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+${site.xpReward} XP ganados aquí · $totalXp XP en total",
                        color = RutaColors.Gold,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar")
                }
            }
        }
    }
}
