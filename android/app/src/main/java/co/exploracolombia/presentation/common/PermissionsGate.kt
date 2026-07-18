package co.exploracolombia.presentation.common

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private fun hasAllPermissions(context: android.content.Context, permissions: Array<String>): Boolean =
    permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }

/**
 * Pide un set de permisos runtime antes de mostrar `content`. Compartido
 * entre MapScreen (solo ubicación) y VisitScreen/ScanScreen (cámara +
 * ubicación) — ver los wrappers específicos más abajo en este archivo.
 * Si el usuario rechaza, muestra una explicación con botón de reintento; si
 * los rechazó de forma permanente ("no volver a preguntar"), el mismo botón
 * lleva a Ajustes de la app en vez de volver a lanzar el diálogo del
 * sistema (relanzarlo ahí no hace nada — Android lo ignora silenciosamente).
 */
@Composable
fun PermissionsGate(
    permissions: Array<String>,
    rationaleTitle: String,
    rationaleBody: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasAllPermissions(context, permissions)) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        granted = results.values.all { it }
        if (!granted) {
            val activity = context as? Activity
            permanentlyDenied = activity != null && permissions.none { permission ->
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!granted) launcher.launch(permissions)
    }

    if (granted) {
        content()
    } else {
        PermissionRationale(
            title = rationaleTitle,
            body = rationaleBody,
            permanentlyDenied = permanentlyDenied,
            onRequestAgain = { launcher.launch(permissions) },
            onOpenSettings = {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null),
                    ),
                )
            },
        )
    }
}

@Composable
private fun PermissionRationale(
    title: String,
    body: String,
    permanentlyDenied: Boolean,
    onRequestAgain: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Button(
            onClick = if (permanentlyDenied) onOpenSettings else onRequestAgain,
            modifier = Modifier.padding(top = 24.dp),
        ) {
            Text(if (permanentlyDenied) "Abrir ajustes de la app" else "Conceder permisos")
        }
    }
}
