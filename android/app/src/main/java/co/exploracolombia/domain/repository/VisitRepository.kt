package co.exploracolombia.domain.repository

import co.exploracolombia.domain.model.HistoricalSite
import co.exploracolombia.domain.model.VisitResult

interface SiteRepository {
    suspend fun getNearbySites(lat: Double, lng: Double, radiusMeters: Int): Result<List<HistoricalSite>>
    suspend fun getSiteById(siteId: String): Result<HistoricalSite>
}

interface VisitRepository {
    suspend fun submitVisit(
        siteId: String,
        lat: Double,
        lng: Double,
        photoJpegBytes: ByteArray,
    ): Result<VisitResult>
}
