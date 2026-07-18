package co.exploracolombia.domain.model

data class HistoricalSite(
    val id: String,
    val code: String,
    val department: String,
    val city: String,
    val lat: Double,
    val lng: Double,
    val geofenceRadiusMeters: Int,
    val title: String,       // ya resuelto al idioma del dispositivo por el repositorio
    val narrative: String,
    val coverImageUrl: String?,
    val xpReward: Int,
    val isUnlocked: Boolean,
)

data class Badge(
    val id: String,
    val code: String,
    val iconUrl: String,
    val rarity: BadgeRarity,
    val name: String,
    val description: String,
)

enum class BadgeRarity { COMMON, RARE, EPIC, LEGENDARY }

data class VisitResult(
    val success: Boolean,
    val siteTitle: String,
    val narrative: String,
    val xpAwarded: Int,
    val totalXp: Int,
    val photoUrl: String,
    val badge: Badge?,
    val failureReason: VisitFailureReason? = null,
    val distanceMeters: Int? = null,
    // Texto de error tal cual lo redactó el backend (ej. "Estás a 1112m del
    // hito. Acércate a menos de 40m."), para mostrarlo directo en la UI sin
    // reconstruirlo del lado del cliente. Null en visitas exitosas.
    val message: String? = null,
)

enum class VisitFailureReason {
    OUT_OF_RANGE,
    PHOTO_MISMATCH,
    ALREADY_VISITED,
    VISION_UNAVAILABLE,
    NETWORK_ERROR,
}
