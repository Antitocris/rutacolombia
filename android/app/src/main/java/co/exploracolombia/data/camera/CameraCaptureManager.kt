package co.exploracolombia.data.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Encapsula CameraX. Se inicializa únicamente al entrar a la pantalla de
 * captura y se libera en onPause/onDispose — nunca queda un preview activo
 * en segundo plano (consumo de batería y, más importante, privacidad).
 * Instanciada una sola vez en di/AppContainer.kt — pasa `context.applicationContext`,
 * nunca un Context de Activity, para no filtrar la Activity si algún día
 * este objeto vive más que ella.
 */
class CameraCaptureManager(private val context: Context) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null

    suspend fun bind(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
    ) {
        val provider = awaitCameraProvider()
        cameraProvider = provider

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
        imageCapture = capture

        provider.unbindAll()
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            capture,
        )
    }

    fun unbind() {
        cameraProvider?.unbindAll()
        cameraProvider = null
        imageCapture = null
    }

    /**
     * `ProcessCameraProvider.getInstance(context)` devuelve un
     * `ListenableFuture`; llamar `.get()` directo bloquea el hilo que lo
     * invoque aunque estemos dentro de un `suspend fun` (bloquear ≠ suspender).
     * Se envuelve con `addListener` + `suspendCancellableCoroutine` para que
     * de verdad ceda el hilo (típicamente el principal, porque `bind()` se
     * llama desde un `LaunchedEffect` de Compose) mientras CameraX inicializa.
     */
    private suspend fun awaitCameraProvider(): ProcessCameraProvider =
        suspendCancellableCoroutine { continuation ->
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener(
                { continuation.resume(future.get()) },
                ContextCompat.getMainExecutor(context),
            )
        }

    /** Captura un frame y lo devuelve como JPEG comprimido en memoria (sin tocar disco). */
    suspend fun capturePhotoBytes(): ByteArray = suspendCancellableCoroutine { continuation ->
        val capture = imageCapture ?: run {
            continuation.resumeWithException(IllegalStateException("Cámara no inicializada"))
            return@suspendCancellableCoroutine
        }

        capture.takePicture(
            ContextCompatExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                    val output = ByteArrayOutputStream()
                    val planeBuffer = image.planes[0].buffer
                    val bytes = ByteArray(planeBuffer.remaining())
                    planeBuffer.get(bytes)
                    output.write(bytes)
                    image.close()
                    continuation.resume(output.toByteArray())
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resumeWithException(exception)
                }
            },
        )
    }
}

private val ContextCompatExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()
