// app/build.gradle.kts (módulo de la aplicación)

import java.io.FileInputStream
import java.util.Properties

// local.properties NO se carga automáticamente en `project.findProperty(...)`
// como sí pasa con gradle.properties — hay que leerlo a mano. Aquí vive
// LOCAL_TEST_JWT, el access_token de un usuario de prueba (ver
// docs/LOCAL_DEV.md) para poder correr la app contra el backend local sin
// tener todavía una pantalla de login real. El archivo está en .gitignore.
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    // El código fuente existente declara `package co.exploracolombia.*` en
    // cada archivo. `namespace` debe coincidir con esa raíz de paquete para
    // que R/BuildConfig se resuelvan sin imports completos. `applicationId`
    // (el identificador de Play Store) es independiente y sí usa el
    // solicitado "com.rutacolombia.app".
    namespace = "co.exploracolombia"
    // 36, no 35: verificado con un build real que activity-compose 1.9.x+
    // (y otras libs AndroidX recientes) ya exigen compileSdk 36. AGP 8.13
    // soporta 36 sin problema (37 sí requeriría AGP 9). targetSdk se deja
    // en 35 tal como se pidió — compileSdk y targetSdk son independientes.
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rutacolombia.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // debug.keystore está commiteado a propósito (ver .gitignore) para que
    // TODO build debug — el tuyo local, o el que corre en un runner efímero
    // de GitHub Actions — firme con la misma clave. Sin esto, cada APK de
    // CI tendría una firma distinta y Android rechazaría instalar una
    // versión nueva encima de la anterior ("app not installed").
    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // Se lee la propiedad pero NO se valida aquí (ver el bloque
            // `tasks.matching` más abajo, fuera de `android {}`): esta
            // lambda `release { }` se evalúa en la fase de CONFIGURACIÓN de
            // Gradle para cualquier comando, incluido `assembleDebug` o un
            // simple sync en Android Studio — si el `error(...)` estuviera
            // aquí, fallaría el sync de cualquiera que solo quiera compilar
            // debug, que es justo lo que se quiere evitar.
            val prodFunctionsUrl = project.findProperty("SUPABASE_FUNCTIONS_BASE_URL") as String?
            buildConfigField("String", "SUPABASE_FUNCTIONS_BASE_URL", "\"${prodFunctionsUrl.orEmpty()}\"")
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"${(project.findProperty("SUPABASE_ANON_KEY") as String?).orEmpty()}\"")
            buildConfigField("String", "LOCAL_TEST_JWT", "\"\"")
            // false a propósito: un release real jamás debe poder fingir que
            // el usuario está parado en el hito. Ver MOCK_LOCATION_ENABLED
            // en `debug` para el porqué existe este interruptor.
            buildConfigField("boolean", "MOCK_LOCATION_ENABLED", "false")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"

            // Los 3 valores salen de local.properties (nunca se commitea).
            // En una máquina de desarrollo normal, sin tocar nada, cae en el
            // valor por defecto de abajo: 10.0.2.2 es la IP fija con la que
            // el EMULADOR de Android Studio (AVD, motor QEMU) alcanza el
            // localhost de la máquina anfitriona — no aplica a un celular
            // físico. Para el APK de prueba del Director, un paso de CI
            // escribe estos mismos 3 valores en local.properties apuntando
            // al proyecto real de Supabase antes de compilar (ver
            // .github/workflows/build-qa-apk.yml) — mismo mecanismo, sin
            // tocar este archivo.
            buildConfigField(
                "String", "SUPABASE_FUNCTIONS_BASE_URL",
                "\"${localProperties.getProperty("SUPABASE_FUNCTIONS_BASE_URL", "http://10.0.2.2:54321/functions/v1")}\"",
            )
            buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\"")
            buildConfigField("String", "LOCAL_TEST_JWT", "\"${localProperties.getProperty("LOCAL_TEST_JWT", "")}\"")
            // true a propósito: este build type es el que usa tanto el
            // emulador local como el APK de prueba del Director. En vez de
            // esperar un GPS real, VisitViewModel reporta directo las
            // coordenadas del Baluarte de San Francisco — así se puede
            // probar el flujo completo (incluida la validación real del
            // backend) sin viajar a Cartagena. Nunca está en `release`.
            buildConfigField("boolean", "MOCK_LOCATION_ENABLED", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Extensión de nivel de módulo (no va dentro de `android {}`): fija el JDK
// objetivo tanto para la compilación de Kotlin como para toolchains locales.
kotlin {
    jvmToolchain(17)
}

// Validación diferida a EJECUCIÓN (doFirst), no a configuración: solo revienta
// cuando de verdad corres `assembleRelease`/`bundleRelease`, nunca en un sync
// normal ni al compilar debug para el emulador.
tasks.matching { it.name == "assembleRelease" || it.name == "bundleRelease" }.configureEach {
    doFirst {
        check(!project.findProperty("SUPABASE_FUNCTIONS_BASE_URL").toString().isNullOrBlank()) {
            "Falta SUPABASE_FUNCTIONS_BASE_URL para el build de release. " +
                "Agrega SUPABASE_FUNCTIONS_BASE_URL=https://<tu-project-ref>.supabase.co/functions/v1 " +
                "a android/gradle.properties (el project-ref lo da `supabase status` tras `supabase link`)."
        }
    }
}

dependencies {
    // --- Mapa real (OpenStreetMap vía osmdroid): gratis, sin API key ni
    //     cuenta de Google Cloud/facturación — a diferencia del Maps SDK de
    //     Google. Descarga tiles del servidor público de OSM.
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // --- Jetpack Compose (UI, Material 3, Foundation, Animation) ---
    implementation(platform("androidx.compose:compose-bom:2026.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // material3 NO trae los íconos por sí solo — VisitScreen.kt usa
    // Icons.Filled.PhotoCamera/LocationOn/Warning/Close, y no todos viven en
    // el set "core" (PhotoCamera es del set extendido), así que se agregan
    // ambos para no arriesgar un "unresolved reference" según qué ícono se use.
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Versiones bajadas de lo que se había fijado antes (1.19.0 / 1.13.0):
    // verificado con un build real que esas exigían compileSdk 37 y/o AGP
    // 9.1+. Estas sí compilan contra compileSdk 36 con AGP 8.13.
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Tema del sistema (Theme.Material3.DayNight.NoActionBar en themes.xml)
    // antes de que Compose tome el control de la ventana.
    implementation("com.google.android.material:material:1.13.0")

    // --- Lifecycle: ViewModel + Runtime en Compose (clave para el ciclo de
    //     vida que apaga el GPS/cámara al salir de VisitScreen) ---
    // 2.8.7, no 2.11.0: la 2.11.0 exige compileSdk 37 + AGP 9.1+ (verificado
    // con build real), igual que core-ktx/activity-compose más arriba.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // --- CameraX (captura del hito) ---
    implementation("androidx.camera:camera-core:1.5.1")
    implementation("androidx.camera:camera-camera2:1.5.1")
    implementation("androidx.camera:camera-lifecycle:1.5.1")
    implementation("androidx.camera:camera-view:1.5.1")

    // --- Ubicación (GPS de alta precisión bajo demanda) ---
    implementation("com.google.android.gms:play-services-location:21.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.11.0")

    // --- Red: Retrofit/OkHttp para llamadas REST estándar ---
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Ktor client: requerido por SupabaseVisitApi.kt (data/remote), que
    //     ya está escrito contra la API de HttpClient de Ktor, no Retrofit.
    //     Se mantiene junto a Retrofit para no romper el código existente;
    //     ver nota en la respuesta sobre consolidar a un solo cliente HTTP. ---
    implementation("io.ktor:ktor-client-android:3.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.3.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // --- Carga de imágenes (insignias, portadas de rutas) ---
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-ktor3:3.5.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
