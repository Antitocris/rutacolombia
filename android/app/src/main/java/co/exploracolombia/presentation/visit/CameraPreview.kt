package co.exploracolombia.presentation.visit

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner

/** Preview en vivo de CameraX. Se enlaza una sola vez por instancia de PreviewView. */
@Composable
fun CameraPreview(
    onBind: suspend (LifecycleOwner, PreviewView) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    LaunchedEffect(previewView) {
        onBind(lifecycleOwner, previewView)
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}
