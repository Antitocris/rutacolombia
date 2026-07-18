package co.exploracolombia.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteBrief
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
private fun FeedPostCard(site: SiteBrief, historicalUrl: String, capturedUrl: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RutaColors.Parchment),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(site.titleEs, color = RutaColors.JungleGreenDark, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
