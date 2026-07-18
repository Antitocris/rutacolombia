package co.exploracolombia.data.repository

import co.exploracolombia.data.remote.SupabaseReviewsApi
import co.exploracolombia.domain.model.SiteReview
import co.exploracolombia.domain.repository.ReviewRepository

class ReviewRepositoryImpl(private val api: SupabaseReviewsApi) : ReviewRepository {

    override suspend fun fetchReviews(siteId: String): Result<List<SiteReview>> = runCatching {
        api.fetchReviews(siteId).map { dto ->
            SiteReview(
                id = dto.id,
                rating = dto.rating,
                reviewText = dto.review_text,
                authorName = dto.display_name,
            )
        }
    }

    override suspend fun submitReview(siteId: String, rating: Int, reviewText: String?): Result<Unit> = runCatching {
        api.submitReview(siteId, rating, reviewText)
    }
}
