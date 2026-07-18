package co.exploracolombia.presentation.map

private const val XP_PER_LEVEL = 500

/**
 * Progreso del jugador. Vive solo en memoria (ver MapViewModel) mientras no
 * exista un endpoint "mi perfil" en el backend — hoy `profiles.xp` sí se
 * actualiza en Supabase en cada visita exitosa (ver index.ts), pero nadie
 * lo vuelve a leer todavía, así que el progreso mostrado en pantalla se
 * reinicia si se cierra la app. El XP real de cada visita en particular sí
 * viene siempre del backend (VisitResult.totalXp), nunca se inventa aquí.
 */
data class GamificationState(
    val totalXp: Int = 0,
    // "Conseguida" al validar la visita con la cámara (ver VisitViewModel /
    // AppRoot.onVisitSuccess) — todavía en gris hasta que el usuario la
    // "pegue" a mano en el Álbum, igual que un cromo suelto de verdad.
    val unlockedBadgeCodes: Set<String> = emptySet(),
    // "Pegada": el usuario tocó la silueta gris en AlbumScreen y la lámina
    // quedó a todo color. Solo entonces cuenta como completada de verdad.
    val pastedBadgeCodes: Set<String> = emptySet(),
    // Foto REAL que el propio usuario capturó al validar cada hito (la URL
    // que ya devuelve validate-visit, subida a Supabase Storage) — clave
    // para el Feed de la Historia: es el lado "después" del comparador, no
    // una imagen de stock. Vive solo en memoria por ahora, igual que el
    // resto de este estado.
    val capturedPhotoUrls: Map<String, String> = emptyMap(),
    // Retos fotográficos comunitarios completados (ver ReviewsSection /
    // SiteDetailSheet) — mecánica local de esta primera versión: otorga XP
    // al toque, sin validación por IA de que la foto en verdad muestre el
    // detalle pedido (a diferencia del XP de la visita principal, que sí
    // pasa por Vision API en el backend). Documentado así a propósito para
    // no aparentar una verificación que todavía no existe.
    val completedPhotoChallengeSiteIds: Set<String> = emptySet(),
) {
    val level: Int get() = (totalXp / XP_PER_LEVEL) + 1
    val xpIntoLevel: Int get() = totalXp % XP_PER_LEVEL
    val xpForNextLevel: Int get() = XP_PER_LEVEL
    val levelProgress: Float get() = xpIntoLevel / XP_PER_LEVEL.toFloat()
}

/** Los 3 estados posibles de una lámina en el Álbum — igual que un cromo Jet de verdad. */
enum class LaminaState { LOCKED, EARNED_UNPASTED, PASTED }

fun GamificationState.laminaStateFor(badgeCode: String): LaminaState = when {
    pastedBadgeCodes.contains(badgeCode) -> LaminaState.PASTED
    unlockedBadgeCodes.contains(badgeCode) -> LaminaState.EARNED_UNPASTED
    else -> LaminaState.LOCKED
}
