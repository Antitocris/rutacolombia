package co.exploracolombia.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.exploracolombia.domain.model.SiteBrief
import co.exploracolombia.presentation.theme.RutaColors

/**
 * Tarjeta de detalle del hito: historia en español E inglés a la vez (tal
 * como se pidió), siempre sobre `RutaColors.Parchment` — nunca texto claro
 * sobre franjas oscuras sin querer, y sin alturas fijas que puedan recortar
 * el párrafo si el narrativa es larga (todo el contenido crece con la hoja).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailSheet(
    site: SiteBrief,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onScanClick: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RutaColors.Parchment,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            SiteCoverArt(rarity = site.badge.rarity)

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = RutaColors.JungleGreen,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${site.city}, ${site.department}",
                    color = RutaColors.StoneGrey,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = site.titleEs,
                color = RutaColors.JungleGreenDark,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = site.titleEn,
                color = RutaColors.StoneGrey,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(modifier = Modifier.height(16.dp))

            XpRewardChip(xp = site.xpReward, badgeName = site.badge.nameEs)

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = site.narrativeEs,
                color = RutaColors.JungleGreenDark,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = RutaColors.StoneGrey.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = site.narrativeEn,
                color = RutaColors.StoneGrey,
                fontStyle = FontStyle.Italic,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onScanClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Escanear monumento para coleccionar insignia",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun SiteCoverArt(rarity: co.exploracolombia.domain.model.BadgeRarity) {
    Surface(
        color = RutaColors.JungleGreen,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().height(160.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Sin foto real todavía (cover_image_url llega vacío del backend):
            // se usa la insignia como arte de portada en vez de un hueco en
            // blanco o un ícono de cámara genérico.
            BadgeIcon(rarity = rarity, locked = false, size = 84.dp)
        }
    }
}

@Composable
private fun XpRewardChip(xp: Int, badgeName: String) {
    Surface(
        color = RutaColors.Gold.copy(alpha = 0.18f),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = RutaColors.GoldInk,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "+$xp XP · $badgeName",
                color = RutaColors.GoldInk,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
