-- ============================================================================
-- Reseñas de la comunidad (estrellas + texto corto) por hito.
-- Público de lectura (es contenido tipo red social: cualquiera ve las
-- reseñas de todos), pero cada usuario solo puede escribir/editar la SUYA.
-- `display_name` se denormaliza acá vía trigger `security definer` en vez de
-- hacer un embed de PostgREST a `profiles` — `profiles` tiene RLS que solo
-- deja leer el propio perfil (ver 20260717120000_init_schema.sql), así que
-- un embed normal devolvería null para las reseñas de otros usuarios. El
-- trigger lee el nombre una sola vez, al guardar, con privilegios elevados
-- controlados — el cliente nunca puede mandar un display_name falso.
-- ============================================================================

create table public.site_reviews (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid not null default auth.uid() references public.profiles(id) on delete cascade,
  site_id uuid not null references public.historical_sites(id) on delete cascade,
  rating smallint not null check (rating between 1 and 5),
  review_text text check (char_length(review_text) <= 500),
  display_name text not null default 'Explorador',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),

  -- Una reseña por usuario por hito; volver a escribir actualiza la
  -- existente (ver Prefer: resolution=merge-duplicates en el cliente).
  unique (user_id, site_id)
);

create index idx_site_reviews_site on public.site_reviews (site_id);

alter table public.site_reviews enable row level security;

create policy "site_reviews_public_read" on public.site_reviews for select using (true);
create policy "site_reviews_self_insert" on public.site_reviews for insert with check (auth.uid() = user_id);
create policy "site_reviews_self_update" on public.site_reviews for update using (auth.uid() = user_id);

create or replace function public.site_reviews_set_display_name() returns trigger as $$
begin
  select p.display_name into new.display_name from public.profiles p where p.id = new.user_id;
  new.updated_at := now();
  return new;
end;
$$ language plpgsql security definer set search_path = public;

create trigger trg_site_reviews_display_name
  before insert or update on public.site_reviews
  for each row execute function public.site_reviews_set_display_name();
