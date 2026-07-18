# Reglas de ofuscación/reducción para el build de release.

# kotlinx.serialization: conserva los serializadores generados por el compilador.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class co.exploracolombia.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor / OkHttp: evita advertencias de clases opcionales de plataformas no-Android.
-dontwarn org.slf4j.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**

# Hilt genera sus propios componentes; no se deben ofuscar sus anotaciones.
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager
