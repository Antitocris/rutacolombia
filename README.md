# ExploraColombia (RutaColombia) — Blueprint técnico del MVP

Turismo gamificado por Colombia: rutas históricas, geofencing + validación de
foto por IA, XP e insignias coleccionables, contenido multilenguaje (es/en/fr/ru)
servido desde la nube.

## Mapa del repositorio

| Paso | Contenido | Ubicación |
|---|---|---|
| 1. Esquema DB multilenguaje | Tablas Postgres/Supabase, i18n por columnas paralelas, RLS | [`supabase/migrations/20260717120000_init_schema.sql`](supabase/migrations/20260717120000_init_schema.sql) |
| 2. Validación de visita (GPS + IA) | Edge Function Deno/TypeScript: geofencing, Google Vision, XP, insignia, narrativa localizada | [`supabase/functions/validate-visit/index.ts`](supabase/functions/validate-visit/index.ts) |
| 3. Arquitectura Android + batería | Clean Architecture Kotlin/Compose, estrategia de GPS/cámara bajo demanda | [`android/ARCHITECTURE.md`](android/ARCHITECTURE.md), [`android/app/src/main/java/co/exploracolombia`](android/app/src/main/java/co/exploracolombia) |
| 4. Microinteracciones de desbloqueo | Animación "cofre abriéndose" → tarjeta de insignia con flip 3D | [`docs/MICROINTERACTIONS.md`](docs/MICROINTERACTIONS.md), [`BadgeUnlockAnimation.kt`](android/app/src/main/java/co/exploracolombia/presentation/unlock/BadgeUnlockAnimation.kt) |
| 5. Entorno local Supabase/Cloudflare | `config.toml`, seed de prueba, guía de `curl` para geofencing + i18n | [`docs/LOCAL_DEV.md`](docs/LOCAL_DEV.md), [`supabase/config.toml`](supabase/config.toml) |

## Flujo end-to-end de una visita

1. El MVP arranca directo en `VisitScreen` con un único sitio fijo (Baluarte
   de San Francisco, Cartagena — coincide con `supabase/seed.sql`); elegir
   sitio desde una ruta real queda para cuando exista navegación entre
   pantallas (ver `android/ARCHITECTURE.md`).
2. `CameraLocationPermissionsGate` pide `CAMERA` y `ACCESS_FINE_LOCATION`
   antes de mostrar nada. Con permisos concedidos, `VisitScreen` activa GPS
   de alta precisión **solo mientras está en foreground**
   ([`LocationTracker.kt`](android/app/src/main/java/co/exploracolombia/data/location/LocationTracker.kt)).
3. Al tocar "Validar Hito Histórico", `CameraCaptureManager` toma la foto en
   memoria (sin escribir a disco).
4. `ValidateVisitUseCase` envía `{site_id, lat, lng, photo_base64}` con el
   JWT del usuario a la Edge Function `validate-visit`.
5. La función valida distancia (Haversine), llama a Google Cloud Vision (o al
   modo mock `GOOGLE_VISION_API_KEY=mock-local-testing` en local — ver
   `docs/LOCAL_DEV.md`), compara etiquetas contra
   `vision_reference.expected_labels`, y si pasa: sube la foto a Storage,
   inserta en `user_visits`, suma XP a `profiles`, otorga `badges` si aplica,
   y devuelve la narrativa en el idioma del usuario.
6. El cliente muestra el resultado en un `ModalBottomSheet` (título del
   hito, narrativa, insignia y XP) o, si falla, en un banner rojo con el
   mensaje exacto del backend (ej. `OUT_OF_RANGE` con la distancia real).
   `BadgeUnlockAnimation` (flash → cofre → flip 3D de la insignia) ya existe
   como componente independiente en `presentation/unlock/` pero todavía no
   está enganchado a este flujo — es la mejora de UX natural del próximo paso.

## Cómo correr el backend en local

Ver la guía completa (arranque de Docker/Supabase, re-aplicar migraciones,
servir la función, y dos pruebas `curl` que verifican geofencing e i18n
antes de tocar producción): [`docs/LOCAL_DEV.md`](docs/LOCAL_DEV.md).

## Cómo desplegar el backend

```bash
supabase link --project-ref <tu-project-ref>
supabase db push                      # aplica todo lo que hay en supabase/migrations/
supabase secrets set GOOGLE_VISION_API_KEY=xxxxx
supabase functions deploy validate-visit
```

El bucket de Storage `visit-photos` (público para lectura, escritura solo
vía `service_role`) se crea con el resto del esquema: `supabase db push`
también aplica `supabase/migrations/20260717120001_storage_buckets.sql`,
que inserta la fila en `storage.buckets` — no hace falta un paso aparte ni
tocar el dashboard.

## Notas de producción

- **Anti-fraude de XP**: la tabla `user_visits` tiene `unique(user_id, site_id)`
  y solo la Edge Function (con `service_role_key`) puede insertar — el cliente
  no tiene permiso de escritura directa sobre `user_visits` ni `profiles.xp`
  (ver políticas RLS en `schema.sql`).
- **i18n dinámico**: agregar un quinto idioma es una migración de columnas
  (`title_pt`, `narrative_pt`, …) más un `lang` adicional en el enum del
  cliente — no requiere cambios de esquema estructural.
- **B2B futuro (cupones a comercios aliados)**: el modelo ya deja el gancho
  natural en `routes`/`historical_sites` por `department`/`city`; una tabla
  `partner_coupons` vinculada a `route_id` se añade sin tocar el flujo de
  validación existente.
