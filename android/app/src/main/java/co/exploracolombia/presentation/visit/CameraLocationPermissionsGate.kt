package co.exploracolombia.presentation.visit

import android.Manifest
import androidx.compose.runtime.Composable
import co.exploracolombia.presentation.common.PermissionsGate

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_FINE_LOCATION,
)

@Composable
fun CameraLocationPermissionsGate(content: @Composable () -> Unit) {
    PermissionsGate(
        permissions = REQUIRED_PERMISSIONS,
        rationaleTitle = "Necesitamos cámara y ubicación",
        rationaleBody = "RutaColombia usa tu cámara para fotografiar el hito y tu ubicación GPS " +
            "para confirmar que estás frente a él. Sin estos dos permisos no podemos validar tu visita.",
        content = content,
    )
}
