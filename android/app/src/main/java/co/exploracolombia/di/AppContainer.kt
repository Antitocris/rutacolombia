package co.exploracolombia.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.exploracolombia.BuildConfig
import co.exploracolombia.data.camera.CameraCaptureManager
import co.exploracolombia.data.location.LocationTracker
import co.exploracolombia.data.remote.SupabaseVisitApi
import co.exploracolombia.data.repository.VisitRepositoryImpl
import co.exploracolombia.domain.model.HistoricalSite
import co.exploracolombia.domain.repository.VisitRepository
import co.exploracolombia.domain.usecase.ValidateVisitUseCase
import co.exploracolombia.presentation.visit.VisitViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.Locale

private val SUPPORTED_LANGUAGES = setOf("es", "en", "fr", "ru")

/**
 * Raíz de composición manual: construye y retiene como singletons (mientras
 * viva el Application) las pocas dependencias reales de la app. Reemplaza a
 * Hilt para este MVP — ver la nota en android/build.gradle.kts sobre por
 * qué (conflicto de versiones real, verificado con builds, entre Hilt 2.58
 * —la última compatible con AGP 8.x— y las librerías actuales del proyecto).
 * Si el grafo de dependencias crece mucho más allá de esto, vale la pena
 * reconsiderar Hilt (probablemente junto con el salto a AGP 9).
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val httpClient: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private val deviceLanguage: String
        get() = Locale.getDefault().language.takeIf { it in SUPPORTED_LANGUAGES } ?: "es"

    private val supabaseVisitApi: SupabaseVisitApi by lazy {
        SupabaseVisitApi(
            httpClient = httpClient,
            functionsBaseUrl = BuildConfig.SUPABASE_FUNCTIONS_BASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
            // Sustituto temporal de auth real: en debug, el access_token que
            // se puso en local.properties (o que un paso de CI escribió ahí
            // para el APK de prueba). En release siempre viene vacío — habrá
            // que cambiar este lambda por el token de sesión real de
            // Supabase Auth cuando exista pantalla de login.
            getUserJwt = { BuildConfig.LOCAL_TEST_JWT },
            deviceLanguage = deviceLanguage,
        )
    }

    private val visitRepository: VisitRepository by lazy {
        VisitRepositoryImpl(supabaseVisitApi)
    }

    private val locationTracker: LocationTracker by lazy {
        LocationTracker(appContext)
    }

    private val cameraCaptureManager: CameraCaptureManager by lazy {
        CameraCaptureManager(appContext)
    }

    private val validateVisitUseCase: ValidateVisitUseCase by lazy {
        ValidateVisitUseCase(visitRepository)
    }

    /**
     * [targetSite] llega desde MapScreen (el hito que el usuario tocó) — se
     * necesita un factory nuevo por cada sitio porque VisitViewModel ya no
     * asume un único hito fijo (ver domain/model/HistoricalSite.kt).
     */
    fun visitViewModelFactory(targetSite: HistoricalSite): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(VisitViewModel::class.java)) {
                    "AppContainer.visitViewModelFactory() solo sabe crear VisitViewModel, pidieron $modelClass"
                }
                return VisitViewModel(targetSite, locationTracker, cameraCaptureManager, validateVisitUseCase) as T
            }
        }
}
