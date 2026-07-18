package co.exploracolombia.presentation.map

import android.Manifest
import androidx.compose.runtime.Composable
import co.exploracolombia.presentation.common.PermissionsGate

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

/** MapScreen solo necesita ubicación (no cámara) para centrarse y calcular hitos alcanzables. */
@Composable
fun LocationPermissionGate(content: @Composable () -> Unit) {
    PermissionsGate(
        permissions = REQUIRED_PERMISSIONS,
        rationaleTitle = "Necesitamos tu ubicación",
        rationaleBody = "RutaColombia centra el mapa en tu posición real y te muestra qué hitos " +
            "históricos tienes cerca. Sin este permiso no podemos ubicarte en el mapa.",
        content = content,
    )
}
