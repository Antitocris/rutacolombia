package co.exploracolombia.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import co.exploracolombia.domain.model.BadgeRarity

/**
 * Paleta "de aventura colonial" de la app — la misma que ya se usaba en
 * BadgeUnlockAnimation.kt, centralizada aquí para que el mapa, el visor de
 * cámara y la tarjeta de detalle se sientan como una sola app y no como
 * pantallas sueltas con colores distintos cada una.
 */
object RutaColors {
    val JungleGreenDark = Color(0xFF12271B)
    val JungleGreen = Color(0xFF1E3D2C)
    val JungleGreenLight = Color(0xFF3A7D5C)
    val Parchment = Color(0xFFFBF7EE)
    val ParchmentDim = Color(0xFFE9E0CB)
    val Gold = Color(0xFFF4C542)
    val GoldSoft = Color(0xFFFFE08A)
    val GoldInk = Color(0xFF4A3610)
    val StoneGrey = Color(0xFF6B6357)
    val ErrorRed = Color(0xFFE0554B)

    fun rarityGradient(rarity: BadgeRarity): Brush = Brush.verticalGradient(
        when (rarity) {
            BadgeRarity.LEGENDARY -> listOf(Color(0xFFB8860B), Color(0xFF5C3D0A))
            BadgeRarity.EPIC -> listOf(Color(0xFF7B2FF7), Color(0xFF3B0F70))
            BadgeRarity.RARE -> listOf(Color(0xFF2F80ED), Color(0xFF14355E))
            BadgeRarity.COMMON -> listOf(JungleGreenLight, JungleGreenDark)
        },
    )

    fun raritySolid(rarity: BadgeRarity): Color = when (rarity) {
        BadgeRarity.LEGENDARY -> Color(0xFFB8860B)
        BadgeRarity.EPIC -> Color(0xFF7B2FF7)
        BadgeRarity.RARE -> Color(0xFF2F80ED)
        BadgeRarity.COMMON -> JungleGreenLight
    }
}
