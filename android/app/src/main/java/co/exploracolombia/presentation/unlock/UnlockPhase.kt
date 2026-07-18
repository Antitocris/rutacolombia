package co.exploracolombia.presentation.unlock

/**
 * Máquina de estados de la secuencia de desbloqueo. Cada fase tiene una
 * duración fija que compone la coreografía completa (~2.1s en total).
 */
enum class UnlockPhase(val durationMs: Int) {
    FLASH(180),        // destello blanco que "cierra" la vista de cámara
    CHEST_SHAKE(420),  // el cofre tiembla anticipando la apertura
    CHEST_OPEN(500),   // tapa se abre, partículas de luz salen
    CARD_REVEAL(600),  // la insignia emerge y hace flip 3D hasta su cara frontal
    SETTLE(400),        // spring final, texto de XP/insignia aparece
}
