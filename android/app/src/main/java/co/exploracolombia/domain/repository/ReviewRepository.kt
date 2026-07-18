package co.exploracolombia.domain.repository

import co.exploracolombia.domain.model.SiteReview

interface ReviewRepository {
    suspend fun fetchReviews(siteId: String): Result<List<SiteReview>>
    suspend fun submitReview(siteId: String, rating: Int, reviewText: String?): Result<Unit>
}
