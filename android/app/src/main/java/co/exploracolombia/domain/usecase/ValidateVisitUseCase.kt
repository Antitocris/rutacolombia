package co.exploracolombia.domain.usecase

import co.exploracolombia.domain.model.VisitResult
import co.exploracolombia.domain.repository.VisitRepository

/**
 * Orquesta el envío de una visita. Vive en domain para que la decisión de
 * "qué constituye una visita válida" no dependa de detalles de red/cámara.
 */
class ValidateVisitUseCase(private val visitRepository: VisitRepository) {
    suspend operator fun invoke(
        siteId: String,
        lat: Double,
        lng: Double,
        photoJpegBytes: ByteArray,
    ): Result<VisitResult> {
        require(photoJpegBytes.isNotEmpty()) { "La foto capturada está vacía" }
        require(lat in -90.0..90.0 && lng in -180.0..180.0) { "Coordenadas GPS inválidas" }
        return visitRepository.submitVisit(siteId, lat, lng, photoJpegBytes)
    }
}
