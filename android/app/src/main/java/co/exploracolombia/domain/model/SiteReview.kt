package co.exploracolombia.domain.model

data class SiteReview(
    val id: String,
    val rating: Int,
    val reviewText: String?,
    val authorName: String,
)
