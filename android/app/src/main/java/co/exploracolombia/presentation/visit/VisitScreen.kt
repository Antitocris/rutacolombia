package co.exploracolombia.presentation.visit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import co.exploracolombia.data.local.SiteCatalog
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.VisitResult
import co.exploracolombia.presentation.theme.RutaColors
import kotlinx.coroutines.launch

/**
 * Paso final del flujo de colección: cámara a pantalla completa con visor
 * de aventura. Ya no es la pantalla de entrada de la app (ver MapScreen) —
 * se llega aquí solo desde el botón "Escanear" de la tarjeta de un hito.
 */
@Composable
fun VisitScreen(
    viewModel: VisitViewModel,
    badgeRarity: BadgeRarity,
    onExit: () -> Unit,
    onVisitSuccess: (VisitResult) -> Unit,
) {
    CameraLocationPermissionsGate {
        VisitContent(viewModel, badgeRarity, onExit, onVisitSuccess)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VisitContent(
    viewModel: VisitViewModel,
    badgeRarity: BadgeRarity,
    onExit: () -> Unit,
    onVisitSuccess: (VisitResult) -> Unit,
) {
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

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraPreview(
            onBind = { owner, previewView -> viewModel.bindCamera(owner, previewView) },
            modifier = Modifier.fillMaxSize(),
        )

        ScanOverlay(rarity = badgeRarity, modifier = Modifier.fillMaxSize())

        // Scrim superior: el título y el botón de volver SIEMPRE quedan
        // sobre un degradado oscuro propio, nunca directo sobre lo que
        // esté filmando la cámara en ese momento — así el contraste no
        // depende de qué tan clara u oscura sea la escena real.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent))),
        ) {
            TopBar(siteTitle = viewModel.targetSite.title, onExit = onExit)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)))),
        ) {
            BottomActionBar(
                location = location,
                isLoading = uiState is VisitUiState.Loading,
                onValidate = viewModel::validateVisit,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        val errorState = uiState as? VisitUiState.Error
        AnimatedVisibility(
            visible = errorState != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 64.dp, start = 16.dp, end = 16.dp),
        ) {
            if (errorState != null) {
                ErrorBanner(message = errorState.message, onDismiss = viewModel::dismissError)
            }
        }
    }

    val successState = uiState as? VisitUiState.Success
    if (successState != null) {
        VisitSuccessSheet(
            result = successState.result,
            onDismiss = {
                viewModel.dismissSuccess()
                onVisitSuccess(successState.result)
            },
        )
    }
}

@Composable
private fun TopBar(siteTitle: String, onExit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onExit) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver al mapa", tint = Color.White)
        }
        Text(
            text = siteTitle,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun BottomActionBar(
    location: VisitLocation?,
    isLoading: Boolean,
    onValidate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp, top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LocationStatusChip(location = location)
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onValidate,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = RutaColors.Gold, contentColor = RutaColors.GoldInk),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = RutaColors.GoldInk,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Validando…", fontWeight = FontWeight.Bold)
            } else {
                Icon(imageVector = Icons.Filled.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validar Hito Histórico", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LocationStatusChip(location: VisitLocation?) {
    val text = when {
        location == null -> "Buscando señal GPS…"
        location.withinRange -> "Estás dentro del rango (${location.distanceMeters} m)"
        else -> "A ${location.distanceMeters} m del hito"
    }
    val dotColor = if (location?.withinRange == true) RutaColors.Gold else Color.White.copy(alpha = 0.6f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = dotColor,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White)
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

    val laminaNumber = result.badge?.let { badge -> SiteCatalog.all.find { it.badge.code == badge.code }?.laminaNumber }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RutaColors.Parchment,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = if (laminaNumber != null) "¡Has conseguido la Lámina #$laminaNumber!" else "¡Hito desbloqueado!",
                style = MaterialTheme.typography.labelLarge,
                color = RutaColors.JungleGreen,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = result.siteTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = RutaColors.JungleGreenDark,
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
                color = RutaColors.GoldInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
            )

            Text(
                text = result.narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = RutaColors.JungleGreenDark,
                modifier = Modifier.padding(top = 16.dp),
            )

            if (laminaNumber != null) {
                Text(
                    text = "Ve al Álbum y toca la lámina #$laminaNumber para pegarla.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RutaColors.StoneGrey,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

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
