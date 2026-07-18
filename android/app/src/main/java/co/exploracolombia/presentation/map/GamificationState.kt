package co.exploracolombia.presentation.map

import co.exploracolombia.domain.model.AlbumPage

private const val XP_PER_LEVEL = 500
private const val XP_MULTIPLIER_PER_COMPLETED_PAGE = 0.1f

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
    // Páginas del Álbum 100% pegadas — cada una otorga un título de perfil
    // real (ver AlbumPage.rewardTitleEs/En) y suma al multiplicador de XP.
    // Un Set, no un contador: idempotente, no se puede "completar dos veces"
    // la misma página por error.
    val completedPages: Set<AlbumPage> = emptySet(),
    // "Puntos de Historia": moneda del Feed. Se otorgan al publicar con
    // éxito una comparación antes/después (ver MapViewModel.onVisitCompleted)
    // y se gastan en Pistas. Versión honesta de lo pedido: el sistema
    // original imaginaba puntos "basados en likes de otros usuarios", pero
    // eso requeriría un backend de posts+likes compartido entre usuarios
    // (fuera del alcance de esta pasada) — por ahora es una recompensa
    // inmediata al publicar, no una que dependa de que otros la aprueben.
    val historyPoints: Int = 0,
) {
    val level: Int get() = (totalXp / XP_PER_LEVEL) + 1
    val xpIntoLevel: Int get() = totalXp % XP_PER_LEVEL
    val xpForNextLevel: Int get() = XP_PER_LEVEL
    val levelProgress: Float get() = xpIntoLevel / XP_PER_LEVEL.toFloat()

    /** +10% de XP por cada página completa — recompensa acumulable real, no cosmética. */
    val xpMultiplier: Float get() = 1f + completedPages.size * XP_MULTIPLIER_PER_COMPLETED_PAGE

    /** El título más reciente desbloqueado (orden de declaración del enum) — el que se muestra en el HUD. */
    val activeTitleEs: String? get() = completedPages.maxByOrNull { it.ordinal }?.rewardTitleEs
}

/** Los 3 estados posibles de una lámina en el Álbum — igual que un cromo Jet de verdad. */
enum class LaminaState { LOCKED, EARNED_UNPASTED, PASTED }

fun GamificationState.laminaStateFor(badgeCode: String): LaminaState = when {
    pastedBadgeCodes.contains(badgeCode) -> LaminaState.PASTED
    unlockedBadgeCodes.contains(badgeCode) -> LaminaState.EARNED_UNPASTED
    else -> LaminaState.LOCKED
}
