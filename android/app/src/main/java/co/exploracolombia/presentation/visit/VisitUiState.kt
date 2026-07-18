package co.exploracolombia.presentation.visit

import co.exploracolombia.domain.model.VisitFailureReason
import co.exploracolombia.domain.model.VisitResult

sealed interface VisitUiState {
    data object Idle : VisitUiState
    data object Loading : VisitUiState
    data class Success(val result: VisitResult) : VisitUiState
    data class Error(val message: String, val reason: VisitFailureReason?) : VisitUiState
}

/** Ubicación en vivo relativa al hito objetivo, para el indicador de distancia en pantalla. */
data class VisitLocation(
    val lat: Double,
    val lng: Double,
    val distanceMeters: Int,
    val withinRange: Boolean,
)
