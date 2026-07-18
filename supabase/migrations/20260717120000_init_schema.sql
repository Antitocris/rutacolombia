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
