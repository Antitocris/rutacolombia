# Guía de ejecución local: Supabase + Edge Function

Flujo completo para levantar el backend de RutaColombia en tu máquina y
verificar, antes de tocar producción, que el geofencing (Haversine sobre
PostGIS/`geography`) y la narrativa multilingüe responden bien.

Comandos probados para **Git Bash / WSL / macOS / Linux** (`curl` real). Si
corres esto en **Windows PowerShell nativo**, antepone `.exe` — `curl` a
secas en PowerShell 5.1 es un alias de `Invoke-WebRequest` con una sintaxis
de flags totalmente distinta y estos comandos fallarán tal cual están
escritos:
```powershell
curl.exe -sS -X POST "http://127.0.0.1:54321/..." ...
```

## Prerrequisitos

| Herramienta | Por qué | Verificar |
|---|---|---|
| Docker Desktop, corriendo | `supabase start` levanta Postgres, GoTrue, Storage, etc. como contenedores | `docker ps` no debe dar error de conexión |
| Supabase CLI | orquesta todo lo anterior | `supabase --version` |
| Nada de Google Cloud todavía | ambas pruebas de esta guía usan `GOOGLE_VISION_API_KEY=mock-local-testing` (ver paso 4) — una key real de Vision solo hace falta cuando quieras probar contra la API de verdad | ver `supabase/functions/validate-visit/.env.example` |

Instalar el CLI en Windows (npm global fue removido oficialmente por
Supabase; usa Scoop):
```powershell
irm get.scoop.sh | iex
scoop bucket add supabase https://github.com/supabase/scoop-bucket.git
scoop install supabase
```
En macOS: `brew install supabase/tap/supabase`. Alternativa multiplataforma
sin instalar nada: `npx supabase <comando>` (requiere Node ≥ 20) en lugar de
`supabase <comando>` en todo lo que sigue.

Todos los comandos de esta guía asumen que tu terminal está parada en la
raíz del repo (`E:\RutaColombia`), no dentro de `supabase/`.

---

## 1. Levantar Supabase local

```bash
supabase start
```

La primera vez, esto descarga las imágenes de Docker (puede tardar varios
minutos) y luego:
1. Levanta Postgres 17, PostgREST, GoTrue (Auth), Storage, Realtime, Studio,
   Inbucket y el runtime de Edge Functions.
2. Aplica **todas** las migraciones en `supabase/migrations/` en orden:
   `20260717120000_init_schema.sql` (trae `create extension if not exists
   postgis;` y crea todas las tablas) y luego
   `20260717120001_storage_buckets.sql` (crea el bucket `visit-photos`
   insertando directamente en `storage.buckets` — no por el bloque
   `[storage.buckets]` de `config.toml`, que es solo para uso local; ver la
   nota en `config.toml` sobre por qué).
3. Corre `supabase/seed.sql` (declarado en `config.toml` bajo `[db.seed]`),
   que inserta un sitio histórico, una insignia y una ruta de prueba con
   IDs fijos — son los que usan los `curl` de las Pruebas 1 y 2 más abajo.

Al terminar imprime una tabla con las URLs y claves locales. Si la cerraste
o necesitas verla de nuevo:
```bash
supabase status
```

## 2. Re-aplicar migraciones a mano (cuando cambies el esquema)

`supabase start` solo corre las migraciones la primera vez que crea el
volumen de Postgres. Cada vez que edites algo en `supabase/migrations/` o en
`seed.sql` y quieras que la base local refleje el cambio desde cero:

```bash
supabase db reset
```

Esto recrea el contenedor de Postgres, vuelve a aplicar **todas** las
migraciones en orden y vuelve a correr `seed.sql`. Cualquier dato que hayas
creado a mano en el Studio local se pierde — es exactamente lo que quieres
antes de repetir una prueba desde un estado limpio.

## 3. Obtener las claves locales

```bash
supabase status -o env
```

Esto imprime (entre otras) tres variables que necesitas para los pasos
siguientes:
```
API_URL="http://127.0.0.1:54321"
ANON_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
SERVICE_ROLE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```
Guárdalas en variables de shell para no copiarlas a mano en cada comando:
```bash
export API_URL="http://127.0.0.1:54321"
export ANON_KEY="<pega aquí el valor de ANON_KEY>"
```
(La `SERVICE_ROLE_KEY` **no** la necesitas para nada de esta guía: el
cliente/curl solo usa el JWT del usuario de prueba, igual que la app
Android real — la service role vive únicamente dentro de la función.)

## 4. Configurar y servir la Edge Function

```bash
cp supabase/functions/validate-visit/.env.example supabase/functions/validate-visit/.env
```

Edita `supabase/functions/validate-visit/.env`: para el flujo de esta guía
(y para Tarea 2 más abajo) **no necesitas una clave real de Google** — deja
`GOOGLE_VISION_API_KEY=mock-local-testing`. `index.ts` reconoce ese string
exacto y devuelve un set fijo de etiquetas de alta confianza en vez de
llamar a Google, para que las pruebas sean 100% deterministas. El día que
quieras probar contra la API real, reemplázalo por una clave real generada
en https://console.cloud.google.com/apis/credentials (API "Cloud Vision API"
habilitada).

```bash
supabase functions serve validate-visit \
  --env-file ./supabase/functions/validate-visit/.env
```

Déjalo corriendo en esta terminal (recarga el código automáticamente en
cada request gracias a `policy = "oneshot"` en `config.toml` — no hace falta
reiniciar el proceso al editar `index.ts`). Abre una segunda terminal para
los pasos de prueba.

La función queda expuesta en:
```
http://127.0.0.1:54321/functions/v1/validate-visit
```

## 5. Crear un usuario de prueba y obtener su JWT

La función exige `Authorization: Bearer <jwt>` de un usuario real (línea
`userClient.auth.getUser()` en `index.ts`). Como `enable_confirmations =
false` en `config.toml`, el signup devuelve el `access_token` de una vez, sin
pasar por Inbucket:

```bash
curl -sS -X POST "$API_URL/auth/v1/signup" \
  -H "apikey: $ANON_KEY" \
  -H "Content-Type: application/json" \
  -d '{"email":"explorador@test.rutacolombia.local","password":"prueba12345"}'
```

Copia el valor del campo `"access_token"` de la respuesta:
```bash
export USER_JWT="<pega aquí el access_token>"
```

## 6. Prueba 1 — Geofencing: usuario fuera de rango

El sitio sembrado (`11111111-1111-1111-1111-111111111111`) está en
`lat 10.4236, lng -75.5478` con `geofence_radius_m = 40`. Mandamos al
usuario a `lat 10.4336` (mismo `lng`, +0.01° de latitud) — un desplazamiento
puramente en latitud, así el resultado esperado se puede calcular a mano con
la misma fórmula esférica que usa `index.ts`
(`distance = EARTH_RADIUS_M * dLat_en_radianes`, porque `dLng = 0` colapsa el
término de longitud del Haversine):

```
distance = 6 371 000 m × (0.01° × π/180) ≈ 1112 m
```

```bash
curl -sS -X POST "$API_URL/functions/v1/validate-visit" \
  -H "Authorization: Bearer $USER_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "site_id": "11111111-1111-1111-1111-111111111111",
    "lat": 10.4336,
    "lng": -75.5478,
    "photo_base64": "'"$(printf 'A%.0s' {1..200})"'",
    "lang": "es"
  }'
```

Respuesta esperada — HTTP 422, y `distance_m` debe caer cerca del cálculo
de arriba (la función usa la fórmula esférica completa, no la aproximación
lineal, así que un par de metros de diferencia es normal):
```json
{
  "error": "Estás a 1112m del hito. Acércate a menos de 40m.",
  "code": "OUT_OF_RANGE",
  "distance_m": 1112
}
```
Si ves esto, el cálculo de distancia está correcto y — tan importante como
eso — confirmaste que el geofencing rechaza *antes* de gastar una llamada a
Google Vision (el `photo_base64` de arriba es basura de relleno a propósito;
si la función hubiera intentado analizarlo con Vision habría fallado por
otra razón, no por distancia).

## 7. Prueba 2 — Visita válida + narrativa en 3 idiomas

Con `GOOGLE_VISION_API_KEY=mock-local-testing` (paso 4), `callVisionApi()`
nunca sale a internet: devuelve directamente
`[{description:"fortress",score:0.93}, {description:"wall",score:0.87}, ...]`.
El sitio sembrado tiene `vision_reference = '{}'`, así que
`scoreVisionMatch()` cae al camino "sin referencia configurada" y acepta la
mejor etiqueta detectada (`0.93 >= 0.5`) — no hace falta ninguna foto real
ni gastar cuota de Google. Reutilizamos el mismo `photo_base64` de relleno
de la Prueba 1 (el mock no lo inspecciona; solo se sube tal cual a Storage).

Pide la narrativa en inglés (`"lang": "en"`) para probar el i18n en el mismo
paso — cambia a `"es"`, `"fr"` o `"ru"` y compara la respuesta:

```bash
curl -sS -X POST "$API_URL/functions/v1/validate-visit" \
  -H "Authorization: Bearer $USER_JWT" \
  -H "Content-Type: application/json" \
  -d '{
    "site_id": "11111111-1111-1111-1111-111111111111",
    "lat": 10.4236,
    "lng": -75.5478,
    "photo_base64": "'"$(printf 'A%.0s' {1..200})"'",
    "lang": "en"
  }'
```

Respuesta esperada — HTTP 200:
```json
{
  "success": true,
  "site": {
    "id": "11111111-1111-1111-1111-111111111111",
    "title": "San Francisco Bastion",
    "narrative": "You are standing before one of the bastions that formed the largest defensive system Spain ever built in the Americas..."
  },
  "xp_awarded": 100,
  "total_xp": 100,
  "photo_url": "http://127.0.0.1:54321/storage/v1/object/public/visit-photos/...",
  "badge": {
    "code": "murallas-cartagena",
    "rarity": "rare",
    "name": "Guardian of the Walls",
    "description": "You walked the fortifications..."
  }
}
```
`title`/`narrative`/`badge.name`/`badge.description` deben cambiar de idioma
según el `lang` que mandes — así verificas el i18n end-to-end, no solo que
el campo exista.

Si repites exactamente este mismo `curl` una segunda vez, ahora debe dar
**409** con `"code": "ALREADY_VISITED"` — es el `unique(user_id, site_id)`
de `user_visits` haciendo su trabajo (ver `schema.sql`/migración): confirma
que no se puede farmear XP repitiendo la misma visita.

## Troubleshooting

| Síntoma | Causa probable | Arreglo |
|---|---|---|
| `supabase start` se cuelga en "Starting containers..." | Docker Desktop no está corriendo | Ábrelo y espera a que el ícono de la ballena esté estable antes de reintentar |
| `relation "historical_sites" does not exist` | La migración no se aplicó (proyecto ya existía de antes de mover el archivo a `migrations/`) | `supabase db reset` |
| El signup del paso 5 devuelve `"email_confirmed_at": null` sin `access_token` | Editaste `config.toml` y quitaste `enable_confirmations = false`, o no reiniciaste tras cambiarlo | `supabase stop && supabase start` después de tocar `config.toml` — los cambios ahí NO se aplican en caliente |
| `curl` responde `Connection refused` en el puerto 54321 pero `supabase status` se ve bien | Terminaste (Ctrl+C) el proceso de `functions serve` del paso 4 sin darte cuenta | Vuelve a correr `supabase functions serve validate-visit --env-file ...` |
| `"code": "VISION_UNAVAILABLE"` (502) | `GOOGLE_VISION_API_KEY` vacía/inválida, o la API "Cloud Vision API" no está habilitada en el proyecto de GCP | Revisa `supabase/functions/validate-visit/.env` y el paso 2 de `.env.example` |
| Cambiaste algo en `config.toml` y no pasa nada | El CLI solo lee `config.toml` al arrancar los contenedores | `supabase stop` y luego `supabase start` de nuevo |
