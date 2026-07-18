package co.exploracolombia.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.PowerManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Fuente única de ubicación en tiempo real. Se instancia con scope ligado al
 * ciclo de vida de la pantalla que la usa (ver VisitViewModel) — nunca vive en
 * un Service de larga duración. `trackPreciseLocation()` solo debe llamarse
 * mientras la pantalla de captura está en foreground; el llamador es
 * responsable de cancelar el Flow al salir de esa pantalla. Instanciada una
 * sola vez en di/AppContainer.kt con `context.applicationContext`.
 */
class LocationTracker(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /** Ubicación cacheada de bajo costo, para listas/mapas generales. No enciende el GPS. */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        // kotlinx.coroutines.tasks.await es una función de EXTENSIÓN sobre
        // Task<T> (`task.await()`), no una función libre `await(task)` —
        // verificado con un build real que la forma anterior no compilaba.
        val location = fusedClient.lastLocation.await()
        return location?.let { it.latitude to it.longitude }
    }

    /**
     * Stream de ubicación de alta precisión, SOLO para la pantalla de caza de
     * hitos. Degrada automáticamente a precisión balanceada si el dispositivo
     * está en modo ahorro de batería, para no forzar GPS puro innecesariamente.
     */
    @SuppressLint("MissingPermission")
    fun trackPreciseLocation(): Flow<Pair<Double, Double>> = callbackFlow {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val priority = if (powerManager.isPowerSaveMode) {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        } else {
            Priority.PRIORITY_HIGH_ACCURACY
        }

        val request = LocationRequest.Builder(/* intervalMillis = */ 2_000L)
            .setPriority(priority)
            .setMinUpdateDistanceMeters(3f)
            .setWaitForAccurateLocation(true)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.latitude to it.longitude) }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            // Se ejecuta al cancelar el Flow (p. ej. desde onPause de VisitScreen).
            fusedClient.removeLocationUpdates(callback)
        }
    }

    /** Ubicación de bajo costo para el mapa general de rutas, atada a ON_START/ON_STOP. */
    @SuppressLint("MissingPermission")
    fun trackApproximateLocation(): Flow<Pair<Double, Double>> = callbackFlow {
        val request = LocationRequest.Builder(/* intervalMillis = */ 30_000L)
            .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMinUpdateDistanceMeters(20f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.latitude to it.longitude) }
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)
        awaitClose { fusedClient.removeLocationUpdates(callback) }
    }
}
