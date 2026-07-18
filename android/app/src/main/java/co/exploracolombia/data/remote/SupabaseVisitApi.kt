package co.exploracolombia.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import android.util.Base64
import kotlinx.serialization.Serializable

@Serializable
data class ValidateVisitRequestDto(
    val site_id: String,
    val lat: Double,
    val lng: Double,
    val photo_base64: String,
    val lang: String,
)

@Serializable
data class BadgeDto(
    val id: String,
    val code: String,
    val icon_url: String,
    val rarity: String,
    val name: String,
    val description: String,
)

@Serializable
data class SiteDto(val id: String, val title: String, val narrative: String)

@Serializable
data class ValidateVisitResponseDto(
    val success: Boolean = false,
    val site: SiteDto? = null,
    val xp_awarded: Int = 0,
    val total_xp: Int = 0,
    val photo_url: String? = null,
    val badge: BadgeDto? = null,
    val error: String? = null,
    val code: String? = null,
    val distance_m: Int? = null,
)

/** Cliente delgado sobre la Edge Function; no contiene lógica de negocio. */
class SupabaseVisitApi(
    private val httpClient: HttpClient,
    private val functionsBaseUrl: String, // ej. https://<project>.supabase.co/functions/v1
    // El gateway de Supabase en producción (Kong) exige el header `apikey`
    // en TODA petición, además del `Authorization: Bearer <jwt>` del usuario
    // — sin él responde 401 antes de que el código de la función corra. Es
    // la anon/publishable key del proyecto: pública por diseño, segura de
    // embeber en la app (no reemplaza el JWT, que sigue siendo obligatorio).
    private val anonKey: String,
    private val getUserJwt: suspend () -> String,
    private val deviceLanguage: String,
) {
    suspend fun validateVisit(
        siteId: String,
        lat: Double,
        lng: Double,
        photoJpegBytes: ByteArray,
    ): ValidateVisitResponseDto {
        val jwt = getUserJwt()
        val photoBase64 = Base64.encodeToString(photoJpegBytes, Base64.NO_WRAP)

        val response = httpClient.post("$functionsBaseUrl/validate-visit") {
            contentType(ContentType.Application.Json)
            header("apikey", anonKey)
            header("Authorization", "Bearer $jwt")
            setBody(
                ValidateVisitRequestDto(
                    site_id = siteId,
                    lat = lat,
                    lng = lng,
                    photo_base64 = photoBase64,
                    lang = deviceLanguage,
                ),
            )
        }
        return response.body()
    }
}
