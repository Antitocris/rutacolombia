package co.exploracolombia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import co.exploracolombia.presentation.visit.VisitScreen
import co.exploracolombia.presentation.visit.VisitViewModel

/**
 * Punto de entrada de la app. La navegación real (lista de rutas -> captura
 * de hito -> álbum) se resuelve en AppNavGraph.kt cuando exista más de una
 * pantalla (ver android/ARCHITECTURE.md); por ahora VisitScreen es la única
 * pantalla y se hospeda directo aquí.
 */
class MainActivity : ComponentActivity() {

    private val visitViewModel: VisitViewModel by viewModels {
        (application as RutaColombiaApplication).container.visitViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VisitScreen(viewModel = visitViewModel)
                }
            }
        }
    }
}
