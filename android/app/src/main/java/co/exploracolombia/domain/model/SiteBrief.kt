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
    /**
     * Nombre de "misión de misterio", evocador — el que de verdad se
     * muestra en el pin del mapa, la lámina y el encabezado de la tarjeta
     * de detalle. `titleEs`/`titleEn` (el nombre oficial/técnico) queda
     * como dato secundario, no como lo primero que el jugador ve — ese es
     * justo el cambio que se pidió: dejar de sentirse como Google Maps.
     */
    val missionTitleEs: String,
    val missionTitleEn: String,
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
    /**
     * Foto histórica/antigua REAL (Wikimedia Commons, licencia libre) para el
     * Feed de la Historia "antes/después" — null cuando no se encontró una
     * fotografía de época genuina para este sitio (no se rellena con una
     * foto moderna disfrazada). Ver `data/local/SiteCatalog.kt` para qué
     * sitios sí tienen una.
     */
    val historicalPhotoUrl: String? = null,
    val historicalPhotoYear: String? = null,
)

data class BadgeBrief(
    val code: String,
    val nameEs: String,
    val nameEn: String,
    val rarity: BadgeRarity,
)

/**
 * Las páginas temáticas del Álbum — el orden de esta lista es el orden en
 * que aparecen las pestañas. `rewardTitleEs/En` es el título de perfil real
 * que se desbloquea al completar TODA la página (ver GamificationState /
 * MapViewModel.pasteLamina) — la recompensa real que se pidió, no solo un
 * contador que sube.
 */
enum class AlbumPage(val titleEs: String, val titleEn: String, val rewardTitleEs: String, val rewardTitleEn: String) {
    BOGOTA_COLONIAL("Bogotá Colonial", "Colonial Bogotá", "Cronista de la Candelaria", "Chronicler of La Candelaria"),
    MUSEOS_E_HISTORIA("Museos e Historia", "Museums & History", "Guardián del Dorado", "Guardian of El Dorado"),
    MURALLAS_DE_CARTAGENA("Murallas de Cartagena", "Walls of Cartagena", "Corsario de las Murallas", "Corsair of the Walls"),
    MIRADORES_Y_NATURALEZA("Miradores y Naturaleza", "Viewpoints & Nature", "Señor de las Alturas", "Lord of the Heights"),
    BOGOTA_MODERNA("Bogotá Moderna", "Modern Bogotá", "Arquitecto Errante", "Wandering Architect"),
    BOGOTA_OCULTA("Bogotá Oculta y Secreta", "Hidden & Secret Bogotá", "Cazador de Leyendas", "Legend Hunter"),
}

fun SiteBrief.toHistoricalSite(isUnlocked: Boolean): HistoricalSite = HistoricalSite(
    id = id,
    code = code,
    department = department,
    city = city,
    lat = lat,
    lng = lng,
    geofenceRadiusMeters = geofenceRadiusMeters,
    title = missionTitleEs,
    narrative = narrativeEs,
    coverImageUrl = coverImageUrl,
    xpReward = xpReward,
    isUnlocked = isUnlocked,
)
