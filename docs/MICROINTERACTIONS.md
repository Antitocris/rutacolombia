# Microinteracciones: secuencia de desbloqueo

Implementación: [`BadgeUnlockAnimation.kt`](../android/app/src/main/java/co/exploracolombia/presentation/unlock/BadgeUnlockAnimation.kt),
orquestada por [`UnlockPhase.kt`](../android/app/src/main/java/co/exploracolombia/presentation/unlock/UnlockPhase.kt).

## Por qué una máquina de estados y no una sola animación

El "momento mágico" real son 5 sensaciones distintas encadenadas, cada una
con su propia curva de animación — mezclarlas en un solo `AnimatedVisibility`
se ve plano. `UnlockPhase` enumera las fases con su duración exacta y
`BadgeUnlockAnimation` recorre el enum con un `LaunchedEffect` + `delay`,
mostrando/ocultando cada capa según la fase activa.

| Fase | Duración | Qué pasa | API de Compose |
|---|---|---|---|
| `FLASH` | 180ms | La cámara "se congela" y un flash blanco corta el corte de escena | `AnimatedVisibility` + `fadeIn/fadeOut` |
| `CHEST_SHAKE` | 420ms | Un cofre 2D vibra en X (4 oscilaciones) para generar anticipación | `Animatable<Float>` + `tween(45)` en loop |
| `CHEST_OPEN` | 500ms | La tapa se traslada y rota (`rotationX`) como si girara sobre una bisagra; partículas de luz explotan radialmente | `graphicsLayer { rotationX }`, `Canvas` con partículas polares |
| `CARD_REVEAL` | 600ms | La tarjeta de insignia entra con flip 3D: `rotationY` de 90°→0° con `EaseOutBack` | `graphicsLayer { rotationY }` + `cameraDistance` para perspectiva real |
| `SETTLE` | 400ms | La tarjeta "rebota" hasta su tamaño final y aparece el texto de XP/insignia | `spring(dampingRatio = MediumBouncy, stiffness = Low)` |

## Detalles de física que hacen la diferencia

- **`cameraDistance` en el flip**: sin ajustar la distancia de cámara del
  `graphicsLayer`, una rotación en Y por encima de ~45° se ve distorsionada
  (efecto "lente ancho"). Se fija a `12 * density` para que el flip se vea
  como una tarjeta física girando, no una textura estirándose.
- **`compositingStrategy = Offscreen`** en la tarjeta: necesario para que el
  fondo con gradiente y las sombras se recompongan correctamente durante la
  rotación 3D en vez de recortarse.
- **Partículas por trigonometría, no por librería externa**: cada partícula
  tiene un ángulo aleatorio uniforme (`0..2π`) y una velocidad radial; su
  posición es `centro + vector * progreso`, y el `alpha` decae mientras el
  `progreso` avanza, así que se desvanecen justo cuando llegan más lejos.
  Esto evita añadir una dependencia de motor de partículas para un efecto de
  ~24 puntos.
- **Rareza → color**: `badgeBackgroundBrush()` mapea `BadgeRarity` a un
  gradiente distinto (dorado/legendario, morado/épico, azul/raro,
  verde/común), reforzando la sensación de "figurita rara" del álbum sin
  necesidad de arte adicional por insignia.

## Transición cámara → insignia

**Estado actual:** `VisitScreen.kt` (`presentation/visit/`) todavía maneja el
éxito de la visita con un `ModalBottomSheet` simple (título, narrativa,
insignia, XP) en vez de esta animación — ver la Tarea de UI que lo introdujo.
`BadgeUnlockAnimation` sigue existiendo tal cual en `presentation/unlock/`,
lista para engancharse: el punto de integración natural es el
`when (uiState)` de `VisitContent()` en `VisitScreen.kt`, reemplazando la
rama `VisitUiState.Success` (que hoy abre el bottom sheet) por esta
animación antes de mostrarlo, o en su lugar.

Descripción original de la coreografía pensada para esa integración: al
llegar `VisitUiState.Success`, se reemplazaría el preview de CameraX por
`BadgeUnlockAnimation` en el mismo contenedor. Como `unbindCamera()` ya se
llama antes de mostrar el resultado, no hay una superficie de cámara
compitiendo por recursos mientras corre la animación — la GPU queda libre
para las capas de `graphicsLayer` y el `Canvas` de partículas, evitando jank
en dispositivos de gama media (la mayoría del público objetivo en Colombia).
