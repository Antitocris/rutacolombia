package co.exploracolombia.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Tarjeta flotante (elevación real, no una franja que corta la pantalla) —
 * MapScreen la posiciona sobre el mapa con márgenes, para que el mapa se
 * sienta como el lienzo de fondo del juego y esto como un panel de HUD
 * encima. La fila de insignias sueltas se reemplazó por un acceso directo
 * al Álbum completo (ver AlbumScreen.kt) — ahí es donde de verdad viven las
 * láminas, con sus 3 estados y el reverso; acá solo se resume el progreso.
 */
@Composable
fun GamificationHeader(
    gamification: GamificationState,
    sites: List<SiteBrief>,
    onOpenAlbum: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pastedCount = sites.count { gamification.laminaStateFor(it.badge.code) == LaminaState.PASTED }
    val newlyEarnedCount = sites.count { gamification.laminaStateFor(it.badge.code) == LaminaState.EARNED_UNPASTED }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RutaColors.JungleGreenDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RutaColors.Gold),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = gamification.level.toString(),
                        color = RutaColors.GoldInk,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Nivel ${gamification.level}",
                            color = RutaColors.Parchment,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${gamification.xpIntoLevel} / ${gamification.xpForNextLevel} XP",
                            color = RutaColors.Parchment.copy(alpha = 0.75f),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    XpBar(progress = gamification.levelProgress)
                }
            }

            gamification.activeTitleEs?.let { title ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🏆 $title · XP x%.1f".format(gamification.xpMultiplier),
                    color = RutaColors.Gold,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(onClick = onOpenAlbum)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector = Icons.Filled.MenuBook, contentDescription = null, tint = RutaColors.Gold)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Álbum: $pastedCount/${sites.size} láminas",
                        color = RutaColors.Parchment,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    if (newlyEarnedCount > 0) {
                        Text(
                            text = "¡Tienes $newlyEarnedCount por pegar!",
                            color = RutaColors.Gold,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
                Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = RutaColors.Parchment.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun XpBar(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.12f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(RutaColors.Gold),
        )
    }
}
