package co.exploracolombia.data.repository

import co.exploracolombia.data.remote.SupabaseVisitApi
import co.exploracolombia.domain.model.Badge
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.VisitFailureReason
import co.exploracolombia.domain.model.VisitResult
import co.exploracolombia.domain.repository.VisitRepository
import io.ktor.client.plugins.ResponseException

class VisitRepositoryImpl(private val api: SupabaseVisitApi) : VisitRepository {

    override suspend fun submitVisit(
        siteId: String,
        lat: Double,
        lng: Double,
        photoJpegBytes: ByteArray,
    ): Result<VisitResult> = try {
        val dto = api.validateVisit(siteId, lat, lng, photoJpegBytes)

        if (dto.success && dto.site != null) {
            Result.success(
                VisitResult(
                    success = true,
                    siteTitle = dto.site.title,
                    narrative = dto.site.narrative,
                    xpAwarded = dto.xp_awarded,
                    totalXp = dto.total_xp,
                    photoUrl = dto.photo_url.orEmpty(),
                    badge = dto.badge?.let {
                        Badge(
                            id = it.id,
                            code = it.code,
                            iconUrl = it.icon_url,
                            rarity = BadgeRarity.valueOf(it.rarity.uppercase()),
                            name = it.name,
                            description = it.description,
                        )
                    },
                ),
            )
        } else {
            Result.success(
                VisitResult(
                    success = false,
                    siteTitle = "",
                    narrative = "",
                    xpAwarded = 0,
                    totalXp = 0,
                    photoUrl = "",
                    badge = null,
                    failureReason = mapFailureCode(dto.code),
                    distanceMeters = dto.distance_m,
                    message = dto.error,
                ),
            )
        }
    } catch (e: ResponseException) {
        Result.success(
            VisitResult(
                success = false, siteTitle = "", narrative = "", xpAwarded = 0,
                totalXp = 0, photoUrl = "", badge = null,
                failureReason = VisitFailureReason.NETWORK_ERROR,
                message = "No se pudo conectar con el servidor (${e.response.status.value}). Revisa tu conexión e intenta de nuevo.",
            ),
        )
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun mapFailureCode(code: String?): VisitFailureReason = when (code) {
        "OUT_OF_RANGE" -> VisitFailureReason.OUT_OF_RANGE
        "PHOTO_MISMATCH" -> VisitFailureReason.PHOTO_MISMATCH
        "ALREADY_VISITED" -> VisitFailureReason.ALREADY_VISITED
        "VISION_UNAVAILABLE" -> VisitFailureReason.VISION_UNAVAILABLE
        else -> VisitFailureReason.NETWORK_ERROR
    }
}
