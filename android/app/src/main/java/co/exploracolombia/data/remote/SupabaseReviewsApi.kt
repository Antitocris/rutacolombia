package co.exploracolombia.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class SiteReviewDto(
    val id: String,
    val site_id: String,
    val rating: Int,
    val review_text: String? = null,
    val display_name: String,
    val created_at: String,
)

@Serializable
private data class NewSiteReviewDto(
    val site_id: String,
    val rating: Int,
    val review_text: String?,
)

/**
 * Cliente delgado sobre la API REST autogenerada de Supabase (PostgREST) para
 * las reseñas de la comunidad. A diferencia de validate-visit, esto NO pasa
 * por una Edge Function propia: no hay lógica de negocio que proteger, la
 * RLS de `site_reviews` (ver la migración 20260719000000) ya garantiza que
 * cada usuario solo puede insertar/editar su propia fila, y el nombre del
 * autor lo resuelve un trigger en el servidor, nunca el cliente.
 */
class SupabaseReviewsApi(
    private val httpClient: HttpClient,
    private val restBaseUrl: String, // ej. https://<project>.supabase.co/rest/v1
    private val anonKey: String,
    private val getUserJwt: suspend () -> String,
) {
    suspend fun fetchReviews(siteId: String): List<SiteReviewDto> {
        val response = httpClient.get("$restBaseUrl/site_reviews") {
            header("apikey", anonKey)
            header("Authorization", "Bearer ${getUserJwt()}")
            parameter("site_id", "eq.$siteId")
            parameter("order", "created_at.desc")
        }
        return response.body()
    }

    /** Un mismo usuario solo puede tener una reseña por hito: volver a enviar actualiza la existente (upsert por el índice único user_id+site_id). */
    suspend fun submitReview(siteId: String, rating: Int, reviewText: String?) {
        httpClient.post("$restBaseUrl/site_reviews") {
            contentType(ContentType.Application.Json)
            header("apikey", anonKey)
            header("Authorization", "Bearer ${getUserJwt()}")
            header("Prefer", "resolution=merge-duplicates,return=minimal")
            parameter("on_conflict", "user_id,site_id")
            setBody(NewSiteReviewDto(site_id = siteId, rating = rating, review_text = reviewText))
        }
    }
}
