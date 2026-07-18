package co.exploracolombia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.exploracolombia.presentation.AppRoot

/**
 * Punto de entrada de la app. Toda la navegación (mapa <-> escaneo) vive en
 * AppRoot.kt; este Activity solo hospeda el árbol de Compose y expone el
 * AppContainer (ver RutaColombiaApplication.kt) hacia abajo.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as RutaColombiaApplication).container
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(appContainer = container)
                }
            }
        }
    }
}
