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
    val unlockedBadgeCodes: Set<String> = emptySet(),
) {
    val level: Int get() = (totalXp / XP_PER_LEVEL) + 1
    val xpIntoLevel: Int get() = totalXp % XP_PER_LEVEL
    val xpForNextLevel: Int get() = XP_PER_LEVEL
    val levelProgress: Float get() = xpIntoLevel / XP_PER_LEVEL.toFloat()
}
