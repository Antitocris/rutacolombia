package co.exploracolombia.presentation.album

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.AlbumPage
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.map.GamificationState
import co.exploracolombia.presentation.map.LaminaState
import co.exploracolombia.presentation.map.MapViewModel
import co.exploracolombia.presentation.map.laminaStateFor
import co.exploracolombia.presentation.theme.RutaColors

/**
 * El Álbum Jet de RutaColombia: páginas temáticas con láminas numeradas.
 * Comparte el mismo MapViewModel que MapScreen (ver AppRoot.kt) — no hay un
 * estado de progreso separado, "pegar" acá es lo mismo que verse
 * desbloqueado en la barra de XP del mapa.
 */
@Composable
fun AlbumScreen(viewModel: MapViewModel, onBack: () -> Unit) {
    val gamification by viewModel.gamification.collectAsState()
    var flippedSite by remember { mutableStateOf<SiteBrief?>(null) }

    val pages = viewModel.sites.groupBy { it.albumPage }.toSortedMap(compareBy { it.ordinal })
    val pastedCount = viewModel.sites.count { gamification.laminaStateFor(it.badge.code) == LaminaState.PASTED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RutaColors.Parchment),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RutaColors.JungleGreenDark)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver al mapa", tint = RutaColors.Parchment)
            }
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = "Álbum RutaColombia",
                    color = RutaColors.Parchment,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "$pastedCount de ${viewModel.sites.size} láminas pegadas",
                    color = RutaColors.Gold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            pages.forEach { (page, sitesInPage) ->
                item(key = "header-${page.name}") {
                    AlbumPageHeader(page = page, sitesInPage = sitesInPage, gamification = gamification)
                }
                item(key = "grid-${page.name}") {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(((sitesInPage.size + 2) / 3 * 170).dp),
                    ) {
                        items(sitesInPage, key = { it.id }) { site ->
                            val state = gamification.laminaStateFor(site.badge.code)
                            LaminaSlot(
                                site = site,
                                state = state,
                                onPaste = { viewModel.pasteLamina(site.badge.code) },
                                onFlip = { flippedSite = site },
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }

    flippedSite?.let { site ->
        LaminaFlipCard(site = site, totalXp = gamification.totalXp, onDismiss = { flippedSite = null })
    }
}

@Composable
private fun AlbumPageHeader(page: AlbumPage, sitesInPage: List<SiteBrief>, gamification: GamificationState) {
    val pastedInPage = sitesInPage.count { gamification.laminaStateFor(it.badge.code) == LaminaState.PASTED }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = page.titleEs,
            color = RutaColors.JungleGreenDark,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "$pastedInPage/${sitesInPage.size}",
            color = RutaColors.StoneGrey,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
