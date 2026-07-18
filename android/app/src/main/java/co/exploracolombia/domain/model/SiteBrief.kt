package co.exploracolombia.domain.model

/**
 * Ficha de un hito para el mapa y la tarjeta de detalle, ANTES de visitarlo
 * — a diferencia de [HistoricalSite]/[VisitResult], que traen texto ya
 * resuelto a un solo idioma (el del dispositivo) porque vienen de una
 * respuesta del backend. Esta ficha vive en el cliente (ver
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
)

data class BadgeBrief(
    val code: String,
    val nameEs: String,
    val nameEn: String,
    val rarity: BadgeRarity,
)

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
