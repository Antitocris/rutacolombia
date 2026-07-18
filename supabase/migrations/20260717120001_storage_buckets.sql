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
