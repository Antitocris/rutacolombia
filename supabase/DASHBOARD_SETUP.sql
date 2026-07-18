-- ==========================================================================
-- RutaColombia — Setup completo para pegar UNA sola vez en Supabase Studio:
-- Dashboard del proyecto -> SQL Editor -> pegar todo esto -> Run.
-- Es la unión, en orden, de:
--   supabase/migrations/20260717120000_init_schema.sql
--   supabase/migrations/20260717120001_storage_buckets.sql
--   supabase/seed.sql
-- ==========================================================================

-- ============================================================================
-- ExploraColombia / RutaColombia — Esquema PostgreSQL (Supabase)
-- Multilenguaje: es, en, fr, ru como columnas paralelas (evita joins extra
-- en el hot path de lectura móvil; el catálogo de sitios es pequeño y de
-- baja escritura, así que la denormalización i18n es la opción correcta).
-- ============================================================================

create extension if not exists "uuid-ossp";
create extension if not exists postgis; -- para ST_DWithin en geofencing server-side opcional

-- ----------------------------------------------------------------------------
-- 1. PROFILES (extiende auth.users de Supabase)
-- ----------------------------------------------------------------------------
create table public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null default 'Explorador',
  avatar_url text,
  device_language text not null default 'es' check (device_language in ('es','en','fr','ru')),
  xp integer not null default 0 check (xp >= 0),
  level integer not null default 1 check (level >= 1),
  streak_days integer not null default 0,
  last_visit_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Nivel derivado de XP: 500 XP por nivel, calculado en trigger para evitar
-- que el cliente pueda falsificar el nivel enviándolo directamente.
create or replace function public.recalc_level() returns trigger as $$
begin
  new.level := greatest(1, floor(new.xp / 500.0)::int + 1);
  new.updated_at := now();
  return new;
end;
$$ language plpgsql;

create trigger trg_recalc_level
  before update of xp on public.profiles
  for each row execute function public.recalc_level();

-- ----------------------------------------------------------------------------
-- 2. HISTORICAL_SITES
-- ----------------------------------------------------------------------------
create table public.historical_sites (
  id uuid primary key default uuid_generate_v4(),
  code text not null unique, -- slug estable, ej. 'cartagena-murallas-baluarte-san-francisco'
  department text not null,  -- ej. 'Bolívar'
  city text not null,        -- ej. 'Cartagena de Indias'

  lat double precision not null,
  lng double precision not null,
  geog geography(Point, 4326) generated always as (
    ST_SetSRID(ST_MakePoint(lng, lat), 4326)::geography
  ) stored,
  geofence_radius_m integer not null default 40 check (geofence_radius_m between 5 and 500),

  -- Referencia de validación visual: embeddings/labels esperados devueltos
  -- por el proveedor de visión, usados para comparar contra la foto enviada.
  vision_reference jsonb not null default '{}'::jsonb,
  -- forma esperada: { "expected_labels": ["fortress","stone wall","colonial architecture"],
  --                    "reference_image_url": "...", "min_confidence": 0.62 }

  title_es text not null,
  title_en text not null,
  title_fr text not null,
  title_ru text not null,

  narrative_es text not null,
  narrative_en text not null,
  narrative_fr text not null,
  narrative_ru text not null,

  cover_image_url text,
  xp_reward integer not null default 100 check (xp_reward > 0),
  -- Sin "references public.badges(id)" aquí a propósito: badges se crea MÁS
  -- ABAJO (sección 3), así que una referencia inline en este create table
  -- fallaría con "relation public.badges does not exist". La FK real se
  -- agrega después con el alter table de la sección 3, una vez que badges
  -- ya existe.
  badge_id uuid,

  is_active boolean not null default true,
  created_at timestamptz not null default now()
);

create index idx_sites_geog on public.historical_sites using gist (geog);
create index idx_sites_department on public.historical_sites (department);

-- ----------------------------------------------------------------------------
-- 3. BADGES (insignias coleccionables)
-- ----------------------------------------------------------------------------
create table public.badges (
  id uuid primary key default uuid_generate_v4(),
  code text not null unique, -- ej. 'fantasma-candelaria'
  icon_url text not null,
  rarity text not null default 'common' check (rarity in ('common','rare','epic','legendary')),

  name_es text not null,
  name_en text not null,
  name_fr text not null,
  name_ru text not null,

  description_es text not null,
  description_en text not null,
  description_fr text not null,
  description_ru text not null
);

-- FK diferida: historical_sites.badge_id se declaró antes de crear badges,
-- así que se añade aquí para respetar el orden de creación de tablas.
alter table public.historical_sites
  add constraint fk_sites_badge foreign key (badge_id) references public.badges(id);

-- ----------------------------------------------------------------------------
-- 4. ROUTES + ROUTE_SITES (orden de una ruta temática)
-- ----------------------------------------------------------------------------
create table public.routes (
  id uuid primary key default uuid_generate_v4(),
  code text not null unique, -- ej. 'fantasmas-la-candelaria'
  department text not null,
  city text not null,
  difficulty text not null default 'easy' check (difficulty in ('easy','medium','hard')),

  name_es text not null,
  name_en text not null,
  name_fr text not null,
  name_ru text not null,

  description_es text not null,
  description_en text not null,
  description_fr text not null,
  description_ru text not null,

  cover_image_url text,
  completion_xp_bonus integer not null default 250,
  completion_badge_id uuid references public.badges(id),
  is_active boolean not null default true,
  created_at timestamptz not null default now()
);

create table public.route_sites (
  route_id uuid not null references public.routes(id) on delete cascade,
  site_id uuid not null references public.historical_sites(id) on delete cascade,
  order_index integer not null,
  primary key (route_id, site_id),
  unique (route_id, order_index)
);

-- ----------------------------------------------------------------------------
-- 5. USER_VISITS (registro de logro por hito)
-- ----------------------------------------------------------------------------
create table public.user_visits (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  site_id uuid not null references public.historical_sites(id) on delete cascade,

  captured_lat double precision not null,
  captured_lng double precision not null,
  distance_m numeric not null,
  photo_url text not null,
  vision_confidence numeric not null,
  vision_labels jsonb not null default '[]'::jsonb,
  validation_passed boolean not null,

  xp_awarded integer not null default 0,
  visited_at timestamptz not null default now(),

  unique (user_id, site_id) -- un hito solo otorga XP una vez por usuario
);

create index idx_visits_user on public.user_visits (user_id);

-- ----------------------------------------------------------------------------
-- 6. USER_BADGES (inventario de insignias del usuario)
-- ----------------------------------------------------------------------------
create table public.user_badges (
  user_id uuid not null references public.profiles(id) on delete cascade,
  badge_id uuid not null references public.badges(id) on delete cascade,
  earned_at timestamptz not null default now(),
  source_site_id uuid references public.historical_sites(id),
  source_route_id uuid references public.routes(id),
  primary key (user_id, badge_id)
);

-- ----------------------------------------------------------------------------
-- 7. USER_ROUTE_PROGRESS (progreso agregado por ruta, para UI de álbum)
-- ----------------------------------------------------------------------------
create table public.user_route_progress (
  user_id uuid not null references public.profiles(id) on delete cascade,
  route_id uuid not null references public.routes(id) on delete cascade,
  sites_completed integer not null default 0,
  sites_total integer not null,
  completed_at timestamptz,
  primary key (user_id, route_id)
);

-- ============================================================================
-- ROW LEVEL SECURITY
-- ============================================================================
alter table public.profiles enable row level security;
alter table public.user_visits enable row level security;
alter table public.user_badges enable row level security;
alter table public.user_route_progress enable row level security;
alter table public.historical_sites enable row level security;
alter table public.routes enable row level security;
alter table public.route_sites enable row level security;
alter table public.badges enable row level security;

-- Catálogo: lectura pública (contenido del juego), sin escritura desde el cliente.
create policy "sites_public_read" on public.historical_sites for select using (is_active);
create policy "routes_public_read" on public.routes for select using (is_active);
create policy "route_sites_public_read" on public.route_sites for select using (true);
create policy "badges_public_read" on public.badges for select using (true);

-- Datos del usuario: cada quien solo ve y edita lo suyo.
create policy "profiles_self_select" on public.profiles for select using (auth.uid() = id);
create policy "profiles_self_update" on public.profiles for update using (auth.uid() = id);
create policy "profiles_self_insert" on public.profiles for insert with check (auth.uid() = id);

create policy "visits_self_select" on public.user_visits for select using (auth.uid() = user_id);
-- Las inserciones en user_visits NO se permiten directo desde el cliente:
-- solo la Edge Function con la service_role key puede escribir, para que
-- el XP no pueda falsificarse llamando a la API REST directamente.

create policy "badges_self_select" on public.user_badges for select using (auth.uid() = user_id);
create policy "progress_self_select" on public.user_route_progress for select using (auth.uid() = user_id);

-- ============================================================================
-- (contenido de 20260717120001_storage_buckets.sql)
-- ============================================================================
-- ============================================================================
-- Bucket de Storage para las fotos de visita.
--
-- Se crea por migración SQL (insert directo en storage.buckets) y NO por el
-- bloque [storage.buckets] de config.toml, porque ese bloque es solo para
-- desarrollo local: `supabase db push` no sincroniza buckets declarados ahí
-- hacia el proyecto remoto. Una migración, en cambio, corre igual con
-- `supabase db reset` (local) y con `supabase db push` (producción) — una
-- sola fuente de verdad para ambos entornos.
-- ============================================================================

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('visit-photos', 'visit-photos', true, 10485760, array['image/jpeg'])
on conflict (id) do nothing;

-- No se agregan políticas RLS sobre storage.objects: todas las subidas pasan
-- por supabase/functions/validate-visit/index.ts usando el cliente admin
-- (SUPABASE_SERVICE_ROLE_KEY), que ignora RLS por diseño. La lectura pública
-- funciona porque el bucket tiene public = true, no por una policy — el
-- endpoint de descarga de Storage revisa esa columna antes de evaluar RLS.

-- ============================================================================
-- (contenido de seed.sql — datos de prueba: Baluarte de San Francisco)
-- ============================================================================
-- supabase/seed.sql
-- Se ejecuta automáticamente después de las migraciones en cada
-- `supabase start` / `supabase db reset` (ver [db.seed] en config.toml).
-- Datos fijos (UUID hardcodeado) a propósito: así docs/LOCAL_DEV.md puede
-- referenciar el mismo site_id en cada curl sin tener que hacer un SELECT
-- previo. Esto es solo para el entorno local; nunca se aplica en producción.

insert into public.badges (id, code, icon_url, rarity, name_es, name_en, name_fr, name_ru, description_es, description_en, description_fr, description_ru)
values (
  '22222222-2222-2222-2222-222222222222',
  'murallas-cartagena',
  'https://picsum.photos/seed/murallas-cartagena/200',
  'rare',
  'Guardián de las Murallas', 'Guardian of the Walls', 'Gardien des Murailles', 'Страж Стен',
  'Recorriste las fortificaciones que protegieron Cartagena de los ataques piratas durante la Colonia.',
  'You walked the fortifications that protected Cartagena from pirate attacks during the colonial era.',
  'Vous avez parcouru les fortifications qui protégeaient Carthagène des attaques de pirates pendant la période coloniale.',
  'Вы прошли по укреплениям, защищавшим Картахену от пиратских набегов в колониальную эпоху.'
);

-- vision_reference = '{}' a propósito: en supabase/functions/validate-visit/index.ts,
-- scoreVisionMatch() cae al camino "sin referencia configurada" cuando
-- expected_labels está vacío, y acepta la foto si Vision detecta CUALQUIER
-- etiqueta con score >= 0.5. Para el smoke test local eso es justo lo que
-- se necesita: probar geofencing + i18n con una foto real cualquiera, sin
-- depender de que la IA reconozca específicamente un fuerte colonial.
insert into public.historical_sites (
  id, code, department, city, lat, lng, geofence_radius_m, vision_reference,
  title_es, title_en, title_fr, title_ru,
  narrative_es, narrative_en, narrative_fr, narrative_ru,
  xp_reward, badge_id
)
values (
  '11111111-1111-1111-1111-111111111111',
  'cartagena-murallas-baluarte-san-francisco',
  'Bolívar', 'Cartagena de Indias',
  10.42360000, -75.54780000,
  40,
  '{}'::jsonb,
  'Baluarte de San Francisco', 'San Francisco Bastion', 'Bastion de San Francisco', 'Бастион Сан-Франсиско',
  'Estás frente a uno de los baluartes que formaron el sistema defensivo más grande construido por España en América. Desde aquí, los vigías avistaban las velas enemigas mucho antes de que llegaran a la bahía.',
  'You are standing before one of the bastions that formed the largest defensive system Spain ever built in the Americas. From here, watchmen spotted enemy sails long before they reached the bay.',
  'Vous êtes devant l''un des bastions qui formaient le plus grand système défensif construit par l''Espagne en Amérique. D''ici, les guetteurs repéraient les voiles ennemies bien avant qu''elles n''atteignent la baie.',
  'Вы стоите перед одним из бастионов, составлявших крупнейшую оборонительную систему, построенную Испанией в Америке. Отсюда часовые замечали вражеские паруса задолго до того, как они достигали залива.',
  100,
  '22222222-2222-2222-2222-222222222222'
);

insert into public.routes (id, code, department, city, difficulty, name_es, name_en, name_fr, name_ru, description_es, description_en, description_fr, description_ru, completion_xp_bonus, completion_badge_id)
values (
  '33333333-3333-3333-3333-333333333333',
  'murallas-cartagena',
  'Bolívar', 'Cartagena de Indias',
  'easy',
  'Murallas de Cartagena', 'Walls of Cartagena', 'Murailles de Carthagène', 'Стены Картахены',
  'Un recorrido corto por el sistema defensivo colonial del centro histórico.',
  'A short walk through the colonial defensive system of the historic center.',
  'Une courte promenade à travers le système défensif colonial du centre historique.',
  'Короткая прогулка по колониальной оборонительной системе исторического центра.',
  250,
  null
);

insert into public.route_sites (route_id, site_id, order_index)
values ('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', 1);
