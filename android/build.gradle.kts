// build.gradle.kts (raíz del proyecto)
//
// Este archivo SOLO declara qué plugins están disponibles para los módulos
// (con `apply false`) y fija sus versiones en un único lugar. La aplicación
// real de cada plugin ocurre en app/build.gradle.kts. Esto evita el clásico
// error de sincronización por versiones de plugin distintas entre módulos.
//
// Nota de compatibilidad: se añade el plugin de Compose compiler
// (org.jetbrains.kotlin.plugin.compose). Desde Kotlin 2.0, el compilador de
// Compose se desacopló de AGP y viajó al repositorio de Kotlin — sin este
// plugin, `buildFeatures.compose = true` falla al sincronizar con Kotlin 2.x.
//
// SIN Hilt ni KSP, a propósito (verificado con builds reales, no solo
// revisión de código):
//   - Hilt 2.59+ exige AGP 9.0+, y elegimos quedarnos en AGP 8.13 para no
//     arrastrar los cambios grandes de AGP 9 (ver android/ARCHITECTURE.md).
//   - Hilt 2.58 (la última compatible con AGP 8.x) trae un kotlin-metadata-jvm
//     que solo lee metadata hasta formato 2.3.0 — pero TODAS las librerías
//     actuales que este proyecto necesita (Ktor 3.x, kotlinx-coroutines
//     1.11.x, kotlinx-serialization 1.9.x, Coil3, play-services-location
//     21.x) ya se publican compiladas con metadata 2.2.0-2.4.0. No hay
//     combinación de versión de Kotlin que satisfaga a la vez "Hilt 2.58 lo
//     puede leer" y "estas librerías lo aceptan a él".
// Con solo 5 clases inyectadas en toda la app (ver
// app/src/main/java/co/exploracolombia/di/AppContainer.kt), cablear las
// dependencias a mano es más simple que perseguir esa combinación de
// versiones, y no le cierra la puerta a volver a Hilt más adelante si el
// grafo de dependencias crece.

plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.4.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.0" apply false
}
