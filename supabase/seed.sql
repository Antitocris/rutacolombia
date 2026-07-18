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

-- ============================================================================
-- 3 hitos adicionales, todos dentro de la ciudad amurallada de Cartagena, a
-- pocos cientos de metros del Baluarte — así el mapa del cliente tiene algo
-- que mostrar además de un solo pin. Coordenadas ILUSTRATIVAS: ajustadas a
-- mano para caer a ~100/300/500m exactos del Baluarte (ver comentario en
-- android/.../data/local/SiteCatalog.kt), no un levantamiento topográfico.
-- ============================================================================

insert into public.badges (id, code, icon_url, rarity, name_es, name_en, name_fr, name_ru, description_es, description_en, description_fr, description_ru)
values
(
  '77777777-7777-7777-7777-777777777777',
  'puerta-del-reloj',
  'https://picsum.photos/seed/puerta-del-reloj/200',
  'common',
  'Guardián del Reloj', 'Keeper of the Clock', 'Gardien de l''Horloge', 'Хранитель часов',
  'Cruzaste la puerta principal de entrada a la Cartagena colonial amurallada.',
  'You crossed the main gateway into colonial walled Cartagena.',
  'Vous avez franchi la porte principale de la Carthagène coloniale fortifiée.',
  'Вы прошли через главные ворота колониальной крепостной Картахены.'
),
(
  '88888888-8888-8888-8888-888888888888',
  'santo-domingo',
  'https://picsum.photos/seed/santo-domingo/200',
  'epic',
  'Alma de Piedra', 'Soul of Stone', 'Âme de Pierre', 'Каменная душа',
  'Visitaste la iglesia más antigua de Cartagena y su famosa torre inclinada.',
  'You visited Cartagena''s oldest church and its famous leaning tower.',
  'Vous avez visité la plus ancienne église de Carthagène et sa célèbre tour inclinée.',
  'Вы посетили старейшую церковь Картахены с её знаменитой наклонной башней.'
),
(
  '99999999-9999-9999-9999-999999999999',
  'las-bovedas',
  'https://picsum.photos/seed/las-bovedas/200',
  'legendary',
  'Centinela de las Bóvedas', 'Sentinel of the Vaults', 'Sentinelle des Voûtes', 'Страж Сводов',
  'Exploraste las bóvedas que alguna vez guardaron secretos militares de la Colonia.',
  'You explored the vaults that once held the colonial military''s secrets.',
  'Vous avez exploré les voûtes qui gardaient autrefois les secrets militaires de la Colonie.',
  'Вы исследовали своды, некогда хранившие военные тайны колониальной эпохи.'
);

insert into public.historical_sites (
  id, code, department, city, lat, lng, geofence_radius_m, vision_reference,
  title_es, title_en, title_fr, title_ru,
  narrative_es, narrative_en, narrative_fr, narrative_ru,
  xp_reward, badge_id
)
values
(
  '44444444-4444-4444-4444-444444444444',
  'cartagena-puerta-del-reloj',
  'Bolívar', 'Cartagena de Indias',
  10.42450000, -75.54780000,
  40,
  '{}'::jsonb,
  'Puerta del Reloj', 'Clock Gate', 'Porte de l''Horloge', 'Часовые ворота',
  'Estás en la puerta principal de acceso a la ciudad amurallada, coronada por su icónica torre del reloj. Durante siglos, todo comerciante y viajero que entraba a Cartagena pasaba bajo este arco.',
  'You''re at the main gateway into the walled city, crowned by its iconic clock tower. For centuries, every merchant and traveler entering Cartagena passed beneath this arch.',
  'Vous êtes à la porte principale d''accès à la ville fortifiée, couronnée par sa tour de l''horloge emblématique. Pendant des siècles, tout marchand et voyageur entrant à Carthagène passait sous cette arche.',
  'Вы находитесь у главных ворот в обнесённый стеной город, увенчанных его знаменитой часовой башней. Веками каждый торговец и путешественник, входивший в Картахену, проходил под этой аркой.',
  75,
  '77777777-7777-7777-7777-777777777777'
),
(
  '55555555-5555-5555-5555-555555555555',
  'cartagena-iglesia-santo-domingo',
  'Bolívar', 'Cartagena de Indias',
  10.42360000, -75.55050000,
  40,
  '{}'::jsonb,
  'Iglesia de Santo Domingo', 'Santo Domingo Church', 'Église de Santo Domingo', 'Церковь Санто-Доминго',
  'La iglesia más antigua de Cartagena, con una torre inclinada que sobrevive desde el siglo XVI. La leyenda cuenta que el mismísimo diablo torció su estructura una noche de tormenta.',
  'Cartagena''s oldest church, with a leaning tower that has stood since the 16th century. Legend says the devil himself twisted its structure during a stormy night.',
  'La plus ancienne église de Carthagène, avec une tour inclinée qui subsiste depuis le XVIe siècle. La légende raconte que le diable lui-même aurait tordu sa structure lors d''une nuit d''orage.',
  'Старейшая церковь Картахены с наклонной башней, стоящей с XVI века. Легенда гласит, что сам дьявол искривил её конструкцию в бурную ночь.',
  150,
  '88888888-8888-8888-8888-888888888888'
),
(
  '66666666-6666-6666-6666-666666666666',
  'cartagena-las-bovedas',
  'Bolívar', 'Cartagena de Indias',
  10.42360000, -75.54320000,
  40,
  '{}'::jsonb,
  'Las Bóvedas', 'The Vaults', 'Les Voûtes', 'Своды',
  'Estas bóvedas de piedra sirvieron como depósito militar y, según la tradición oral, como calabozo. Hoy sus 23 arcos albergan artesanías, pero las paredes todavía guardan ecos de la Cartagena colonial.',
  'These stone vaults once served as a military storehouse and, according to oral tradition, a dungeon. Today their 23 arches house craft shops, but the walls still echo colonial Cartagena.',
  'Ces voûtes de pierre ont servi de dépôt militaire et, selon la tradition orale, de cachot. Aujourd''hui, ses 23 arches abritent des boutiques d''artisanat, mais les murs gardent encore les échos de la Carthagène coloniale.',
  'Эти каменные своды служили военным складом, а по устной традиции — тюрьмой. Сегодня в их 23 арках располагаются ремесленные лавки, но стены всё ещё хранят отголоски колониальной Картахены.',
  200,
  '99999999-9999-9999-9999-999999999999'
);

insert into public.route_sites (route_id, site_id, order_index)
values
  ('33333333-3333-3333-3333-333333333333', '44444444-4444-4444-4444-444444444444', 2),
  ('33333333-3333-3333-3333-333333333333', '55555555-5555-5555-5555-555555555555', 3),
  ('33333333-3333-3333-3333-333333333333', '66666666-6666-6666-6666-666666666666', 4);
