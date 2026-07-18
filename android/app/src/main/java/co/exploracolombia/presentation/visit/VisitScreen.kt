package co.exploracolombia.presentation.visit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.VisitResult
import kotlinx.coroutines.launch

@Composable
fun VisitScreen(viewModel: VisitViewModel) {
    CameraLocationPermissionsGate {
        VisitContent(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitContent(viewModel: VisitViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val location by viewModel.location.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // El GPS de alta precisión y la cámara solo viven entre ON_RESUME y
    // ON_PAUSE de esta pantalla — misma disciplina de batería que el resto
    // de la app (ver android/ARCHITECTURE.md).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startLocationUpdates()
                Lifecycle.Event.ON_PAUSE -> viewModel.stopLocationUpdates()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopLocationUpdates()
            viewModel.unbindCamera()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            CameraPreview(
                onBind = { owner, previewView -> viewModel.bindCamera(owner, previewView) },
                modifier = Modifier.fillMaxWidth().weight(1f),
            )

            VisitBottomPanel(
                siteTitle = viewModel.targetSite.title,
                location = location,
                isLoading = uiState is VisitUiState.Loading,
                onValidate = viewModel::validateVisit,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }

        val errorState = uiState as? VisitUiState.Error
        AnimatedVisibility(
            visible = errorState != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
        ) {
            if (errorState != null) {
                ErrorBanner(message = errorState.message, onDismiss = viewModel::dismissError)
            }
        }
    }

    val successState = uiState as? VisitUiState.Success
    if (successState != null) {
        VisitSuccessSheet(result = successState.result, onDismiss = viewModel::dismissSuccess)
    }
}

@Composable
private fun VisitBottomPanel(
    siteTitle: String,
    location: VisitLocation?,
    isLoading: Boolean,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = siteTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LocationStatusRow(location = location)
        }

        Button(
            onClick = onValidate,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Validando…")
            } else {
                Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validar Hito Histórico", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun LocationStatusRow(location: VisitLocation?) {
    val color = when {
        location == null -> MaterialTheme.colorScheme.onSurfaceVariant
        location.withinRange -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val text = when {
        location == null -> "Buscando señal GPS…"
        location.withinRange -> "Estás dentro del rango (${location.distanceMeters} m)"
        else -> "A ${location.distanceMeters} m del hito"
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(horizontal = 12.dp).weight(1f),
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitSuccessSheet(result: VisitResult, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "¡Hito desbloqueado!",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = result.siteTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
            )

            result.badge?.let { badge ->
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text("${badge.name} · ${rarityLabel(badge.rarity)}") },
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            Text(
                text = "+${result.xpAwarded} XP  ·  Total: ${result.totalXp} XP",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = result.narrative,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
            )

            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            ) {
                Text("Continuar explorando")
            }
        }
    }
}

private fun rarityLabel(rarity: BadgeRarity): String = when (rarity) {
    BadgeRarity.COMMON -> "Común"
    BadgeRarity.RARE -> "Rara"
    BadgeRarity.EPIC -> "Épica"
    BadgeRarity.LEGENDARY -> "Legendaria"
}
