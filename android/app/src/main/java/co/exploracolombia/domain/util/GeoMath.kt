package co.exploracolombia.domain.util

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val EARTH_RADIUS_M = 6_371_000.0

/**
 * Distancia Haversine en metros entre dos coordenadas. Misma fórmula que usa
 * el backend en supabase/functions/validate-visit/index.ts — se calcula
 * también en el cliente solo para mostrar feedback de "qué tan cerca estás"
 * en tiempo real; la validación real de geofencing siempre la hace el
 * servidor con esta misma fórmula sobre datos que el cliente no controla.
 */
fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val toRad = { deg: Double -> deg * Math.PI / 180.0 }
    val dLat = toRad(lat2 - lat1)
    val dLng = toRad(lng2 - lng1)
    val a = sin(dLat / 2).let { it * it } +
        cos(toRad(lat1)) * cos(toRad(lat2)) * sin(dLng / 2).let { it * it }
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_M * c
}
