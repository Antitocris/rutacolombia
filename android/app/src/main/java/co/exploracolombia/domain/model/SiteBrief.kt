package co.exploracolombia.domain.model

/**
 * Ficha de un hito para el mapa, la tarjeta de detalle y el Álbum, ANTES de
 * visitarlo — a diferencia de [HistoricalSite]/[VisitResult], que traen
 * texto ya resuelto a un solo idioma (el del dispositivo) porque vienen de
 * una respuesta del backend. Esta ficha vive en el cliente (ver
 * `data/local/SiteCatalog.kt`) porque todavía no existe un endpoint
 * "listar sitios cercanos" — es el mismo contenido de `supabase/seed.sql`,
 * a propósito, para que el mapa y el backend nunca cuenten historias
 * distintas del mismo hito.
 */
data class SiteBrief(
    val id: String,
    val code: String,
    val department: String,
    val city: String,
    val lat: Double,
    val lng: Double,
    val geofenceRadiusMeters: Int,
    val titleEs: String,
    val titleEn: String,
    val narrativeEs: String,
    val narrativeEn: String,
    val coverImageUrl: String?,
    val xpReward: Int,
    val badge: BadgeBrief,
    /** Número de cromo en el Álbum — el mismo que se muestra en el mapa y en la lámina. */
    val laminaNumber: Int,
    val albumPage: AlbumPage,
    /** Dato curioso, corto y con humor, para el reverso de la lámina — distinto del narrativeEs/En "serio" de la tarjeta del mapa. */
    val funFactEs: String,
    val funFactEn: String,
)

data class BadgeBrief(
    val code: String,
    val nameEs: String,
    val nameEn: String,
    val rarity: BadgeRarity,
)

/** Las páginas temáticas del Álbum — el orden de esta lista es el orden en que aparecen las pestañas. */
enum class AlbumPage(val titleEs: String, val titleEn: String) {
    BOGOTA_COLONIAL("Bogotá Colonial", "Colonial Bogotá"),
    MUSEOS_E_HISTORIA("Museos e Historia", "Museums & History"),
    MURALLAS_DE_CARTAGENA("Murallas de Cartagena", "Walls of Cartagena"),
}

fun SiteBrief.toHistoricalSite(isUnlocked: Boolean): HistoricalSite = HistoricalSite(
    id = id,
    code = code,
    department = department,
    city = city,
    lat = lat,
    lng = lng,
    geofenceRadiusMeters = geofenceRadiusMeters,
    title = titleEs,
    narrative = narrativeEs,
    coverImageUrl = coverImageUrl,
    xpReward = xpReward,
    isUnlocked = isUnlocked,
)
