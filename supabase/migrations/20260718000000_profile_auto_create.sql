-- ============================================================================
-- Crea automáticamente una fila en public.profiles cada vez que se registra
-- un usuario nuevo en auth.users.
--
-- Bug real encontrado probando contra un proyecto de producción real: nada
-- en el esquema original creaba esta fila, así que cualquier insert en
-- user_visits fallaba con violación de foreign key ("no se pudo registrar
-- la visita") para todo usuario nuevo — no era un caso límite, era el 100%
-- de los usuarios reales, porque ninguno tiene fila en profiles al
-- registrarse. Sin este trigger, la app nunca podría otorgar XP a nadie.
-- ============================================================================

create or replace function public.handle_new_user()
returns trigger as $$
begin
  insert into public.profiles (id, device_language)
  values (new.id, coalesce(new.raw_user_meta_data->>'device_language', 'es'))
  on conflict (id) do nothing;
  return new;
end;
$$ language plpgsql security definer set search_path = public;

create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- Backfill: crea la fila de perfil para usuarios que ya se registraron
-- ANTES de que existiera este trigger (ej. el usuario de prueba de QA).
insert into public.profiles (id)
select id from auth.users
where id not in (select id from public.profiles)
on conflict (id) do nothing;
