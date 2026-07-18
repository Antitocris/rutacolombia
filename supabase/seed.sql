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
