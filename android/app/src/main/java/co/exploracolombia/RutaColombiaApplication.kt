package co.exploracolombia

import android.app.Application
import co.exploracolombia.di.AppContainer
import org.osmdroid.config.Configuration

class RutaColombiaApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        // osmdroid exige un user agent identificable — su política de uso del
        // tile server público bloquea el user agent por defecto (a secas
        // "Java", el de OkHttp) para evitar abuso anónimo. Se configura acá,
        // una sola vez, antes de que cualquier MapView intente pedir tiles.
        Configuration.getInstance().apply {
            load(applicationContext, getSharedPreferences("osmdroid_prefs", MODE_PRIVATE))
            userAgentValue = packageName
        }
    }
}
