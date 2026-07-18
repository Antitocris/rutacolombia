package co.exploracolombia.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.map.HINT_COST_POINTS
import co.exploracolombia.presentation.map.HISTORY_POINTS_PER_POST
import co.exploracolombia.presentation.map.MapViewModel
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Feed de la Historia: una publicación por cada hito donde el usuario YA
 * capturó su propia foto al validar la visita Y el hito tiene una fotografía
 * histórica real y verificada (ver SiteCatalog.kt — hoy solo Plaza de
 * Bolívar y Capitolio Nacional, honestamente, porque son los únicos dos
 * donde se encontró una foto de época genuina en Wikimedia Commons; no se
 * rellenó el resto con fotos modernas disfrazadas de antiguas).
 */
@Composable
fun FeedScreen(viewModel: MapViewModel) {
    val gamification by viewModel.gamification.collectAsState()
    var hintMessage by remember { mutableStateOf<String?>(null) }

    val posts = viewModel.sites.mapNotNull { site ->
        val capturedUrl = gamification.capturedPhotoUrls[site.id] ?: return@mapNotNull null
        val historicalUrl = site.historicalPhotoUrl ?: return@mapNotNull null
        Triple(site, historicalUrl, capturedUrl)
    }

    val visitedWithoutHistoricalPhoto = viewModel.sites.count { site ->
        gamification.capturedPhotoUrls.containsKey(site.id) && site.historicalPhotoUrl == null
    }

    Column(modifier = Modifier.fillMaxSize().background(RutaColors.Parchment)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RutaColors.JungleGreenDark)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Text("Feed de la Historia", color = RutaColors.Parchment, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Text(
                "Compara cómo se veía cada hito hace décadas contra tu propia foto de hoy",
                color = RutaColors.Parchment.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(10.dp))
            HistoryPointsBar(
                points = gamification.historyPoints,
                onRedeemHint = {
                    val result = viewModel.redeemHint()
                    hintMessage = if (result == null) {
                        if (gamification.historyPoints < HINT_COST_POINTS) {
                            "Necesitas al menos $HINT_COST_POINTS Puntos de Historia para canjear una pista."
                        } else {
                            "¡Ya descubriste todas las misiones! No queda nada por revelar."
                        }
                    } else {
                        val distanceText = result.distanceMeters?.let { m ->
                            if (m >= 1000) " a unos %.1f km".format(m / 1000f) else " a unos $m m"
                        } ?: ""
                        "La misión sin descubrir más cercana es \"${result.missionTitleEs}\", en ${result.city}$distanceText."
                    }
                },
            )
        }

        hintMessage?.let { message ->
            AlertDialog(
                onDismissRequest = { hintMessage = null },
                confirmButton = { TextButton(onClick = { hintMessage = null }) { Text("Entendido") } },
                title = { Text("Pista") },
                text = { Text(message) },
            )
        }

        if (posts.isEmpty()) {
            EmptyFeedState(visitedWithoutHistoricalPhoto = visitedWithoutHistoricalPhoto)
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                items(posts, key = { it.first.id }) { (site, historicalUrl, capturedUrl) ->
                    FeedPostCard(site = site, historicalUrl = historicalUrl, capturedUrl = capturedUrl)
                }
                if (visitedWithoutHistoricalPhoto > 0) {
                    item {
                        Text(
                            "$visitedWithoutHistoricalPhoto hito${if (visitedWithoutHistoricalPhoto == 1) "" else "s"} más visitado${if (visitedWithoutHistoricalPhoto == 1) "" else "s"} todavía sin foto histórica real disponible para comparar.",
                            color = RutaColors.StoneGrey,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryPointsBar(points: Int, onRedeemHint: () -> Unit) {
    Surface(color = Color.White.copy(alpha = 0.08f), shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "🪙 $points Puntos de Historia",
                    color = RutaColors.Gold,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Ganas $HISTORY_POINTS_PER_POST al publicar una comparación con éxito",
                    color = RutaColors.Parchment.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Button(onClick = onRedeemHint) {
                Text("Canjear Pista ($HINT_COST_POINTS)")
            }
        }
    }
}

@Composable
private fun FeedPostCard(site: SiteBrief, historicalUrl: String, capturedUrl: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RutaColors.Parchment),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(site.missionTitleEs, color = RutaColors.JungleGreenDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                "${site.city} · Lámina #${site.laminaNumber}",
                color = RutaColors.StoneGrey,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            BeforeAfterSlider(
                beforeImageUrl = historicalUrl,
                afterImageUrl = capturedUrl,
                beforeLabel = site.historicalPhotoYear ?: "Antes",
                afterLabel = "Hoy",
            )
            Text(
                text = "🪙 +$HISTORY_POINTS_PER_POST Puntos de Historia ganados aquí",
                color = RutaColors.GoldInk,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun EmptyFeedState(visitedWithoutHistoricalPhoto: Int) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = null, tint = RutaColors.StoneGrey, modifier = Modifier.padding(bottom = 12.dp))
            Text(
                text = "Todavía no tienes publicaciones",
                color = RutaColors.JungleGreenDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Visita Plaza de Bolívar o el Capitolio Nacional y valida tu foto: son los hitos con foto histórica real disponible para comparar por ahora.",
                color = RutaColors.StoneGrey,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp),
            )
            if (visitedWithoutHistoricalPhoto > 0) {
                Text(
                    text = "Ya visitaste $visitedWithoutHistoricalPhoto hito${if (visitedWithoutHistoricalPhoto == 1) "" else "s"} sin foto histórica todavía disponible.",
                    color = RutaColors.StoneGrey,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}
