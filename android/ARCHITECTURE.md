# Arquitectura Android — ExploraColombia

Kotlin + Jetpack Compose, Clean Architecture con 3 capas y flujo de dependencia
estrictamente hacia adentro (`presentation → domain ← data`). `domain` no
importa nada de Android SDK ni de Compose: son interfaces y modelos puros,
lo que permite testear la lógica de negocio sin emulador.

```
app/src/main/java/co/exploracolombia/
├── RutaColombiaApplication.kt            # Application, Hilt entrypoint
├── MainActivity.kt                       # hospeda VisitScreen bajo Hilt
├── di/
│   ├── NetworkModule.kt                  # HttpClient (Ktor), SupabaseVisitApi
│   └── RepositoryModule.kt               # Bind impl -> interface
│
├── domain/                               # Sin dependencias de Android
│   ├── model/HistoricalSite.kt           # HistoricalSite, Badge, VisitResult
│   ├── repository/VisitRepository.kt     # interface
│   ├── usecase/ValidateVisitUseCase.kt
│   └── util/GeoMath.kt                   # Haversine, compartido cliente/servidor
│
├── data/
│   ├── location/LocationTracker.kt       # wrapper de FusedLocationProviderClient
│   ├── camera/CameraCaptureManager.kt    # CameraX ImageCapture
│   ├── remote/SupabaseVisitApi.kt        # llamada a la Edge Function (Ktor)
│   └── repository/VisitRepositoryImpl.kt
│
└── presentation/
    ├── visit/                            # única pantalla del MVP: cámara + GPS + validar
    │   ├── VisitViewModel.kt
    │   ├── VisitScreen.kt
    │   ├── VisitUiState.kt
    │   ├── CameraPreview.kt
    │   └── CameraLocationPermissionsGate.kt
    └── unlock/                           # animación de desbloqueo (aún no enganchada a VisitScreen)
        ├── BadgeUnlockAnimation.kt
        └── UnlockPhase.kt
```

## Estrategia de batería: GPS y cámara bajo demanda

El error más común en apps de geolocalización es dejar `LocationRequest` de
alta precisión corriendo en segundo plano. La regla de esta app es:

**El GPS de alta precisión solo vive mientras la pantalla `Visit` está en
primer plano.** El resto de la app (mapa de rutas, álbum, perfil) usa como
mucho la última ubicación cacheada de Play Services (`getLastLocation()`,
costo de batería ~0).

| Contexto | Prioridad | Intervalo | Duración |
|---|---|---|---|
| Lista de rutas / álbum / perfil | Ninguna (solo `lastLocation`) | — | — |
| Mapa general (¿qué hay cerca?) | `PRIORITY_BALANCED_POWER_ACCURACY` | 30s | Solo mientras la pantalla está visible (`ON_START`/`ON_STOP`) |
| VisitScreen activa (buscando un hito específico) | `PRIORITY_HIGH_ACCURACY` | 2s, con `setMinUpdateDistanceMeters(3f)` | Solo `ON_RESUME`→`ON_PAUSE` de esa pantalla, nunca en background |
| App en background | Sin actualizaciones activas | — | Se usa un **Geofence** de Play Services (`GeofencingClient`) por sitio cercano, que despierta la app vía `PendingIntent` sin polling continuo |

Puntos clave de implementación:

1. **`LocationTracker` se ata al `lifecycleScope` de la pantalla `Visit`**, no
   al `Application` ni a un `Service` de larga duración. Al salir de la
   pantalla (`onPause`), se llama `removeLocationUpdates()` inmediatamente.
2. **Geofencing pasivo en vez de polling en background.** Cuando el usuario
   inicia una ruta, se registran geofences (radio del sitio + margen) para
   los próximos 3-5 hitos de la ruta usando `GeofencingClient.addGeofences()`.
   El sistema operativo notifica vía broadcast solo al entrar/salir del área,
   sin que la app consuma CPU/GPS mientras tanto.
3. **La cámara (CameraX) solo se inicializa al entrar a la pantalla de
   captura**, y `cameraProvider.unbindAll()` se llama en `onPause`. No hay
   preview de cámara corriendo en ninguna otra pantalla.
4. **`ACCESS_BACKGROUND_LOCATION` no se solicita en el MVP.** Todo el
   geofencing usa el permiso `ACCESS_FINE_LOCATION` en foreground/mientras-en-uso;
   los geofences de Play Services funcionan igual sin permiso de background
   porque el sistema, no la app, hace el monitoreo de bajo consumo.
5. **Throttling de precisión por batería del dispositivo:** si
   `BatteryManager.isPowerSaveMode` es `true`, `LocationTracker` degrada
   automáticamente a `PRIORITY_BALANCED_POWER_ACCURACY` incluso dentro de
   `Visit`, evitando forzar GPS puro en modo ahorro de energía.
