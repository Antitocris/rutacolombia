package co.exploracolombia

import android.app.Application
import co.exploracolombia.di.AppContainer

class RutaColombiaApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
