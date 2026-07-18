-- ============================================================================
-- Ampliación de Bogotá: 11 hitos nuevos (Monserrate, Quinta de Bolívar,
-- Planetario, Torre Colpatria, Parque de Usaquén, Parque de la 93,
-- Biblioteca Virgilio Barco, Jardín Botánico, Capitolio Nacional, Catedral
-- Primada, Parque Simón Bolívar), en paralelo exacto a los mismos 11
-- SiteBrief agregados en android/.../data/local/SiteCatalog.kt — MISMOS
-- UUIDs de sitio en ambos lados, a propósito, para que cuando exista un
-- endpoint real de "sitios cercanos" el cliente y el backend nunca
-- discrepen. Coordenadas verificadas contra Nominatim, narrativas ES/EN
-- verificadas contra fuentes reales (ver historial de la conversación);
-- FR/RU son traducción directa del mismo contenido ya verificado, no datos
-- nuevos inventados.
--
-- Esta migración también es la que hace posible que las reseñas de la
-- comunidad (20260719000000_site_reviews.sql) funcionen para estos 11
-- sitios nuevos: site_reviews.site_id tiene FK a historical_sites.id, así
-- que sin estas filas el mapa los mostraría pero cualquier intento de
-- reseñarlos fallaría por violación de llave foránea.
-- ============================================================================

insert into public.badges (id, code, icon_url, rarity, name_es, name_en, name_fr, name_ru, description_es, description_en, description_fr, description_ru)
values
(
  'a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1',
  'quinta-bolivar',
  'https://picsum.photos/seed/quinta-bolivar/200',
  'rare',
  'Huésped del Libertador', 'Guest of the Liberator', 'Invité du Libérateur', 'Гость Освободителя',
  'Visitaste la casa de campo donde vivió Simón Bolívar en Bogotá.',
  'You visited the country house where Simón Bolívar lived in Bogotá.',
  'Vous avez visité la maison de campagne où vécut Simón Bolívar à Bogotá.',
  'Вы посетили загородный дом, где жил Симон Боливар в Боготе.'
),
(
  'a2a2a2a2-a2a2-a2a2-a2a2-a2a2a2a2a2a2',
  'capitolio-nacional',
  'https://picsum.photos/seed/capitolio-nacional/200',
  'epic',
  'Testigo de la República', 'Witness to the Republic', 'Témoin de la République', 'Свидетель Республики',
  'Recorriste la sede del Congreso de la República en la Plaza de Bolívar.',
  'You visited the seat of Congress on Plaza de Bolívar.',
  'Vous avez visité le siège du Congrès sur la Plaza de Bolívar.',
  'Вы посетили здание Конгресса на площади Боливара.'
),
(
  'a3a3a3a3-a3a3-a3a3-a3a3-a3a3a3a3a3a3',
  'catedral-primada',
  'https://picsum.photos/seed/catedral-primada/200',
  'epic',
  'Guardián de Jiménez de Quesada', 'Keeper of Jiménez de Quesada', 'Gardien de Jiménez de Quesada', 'Хранитель Хименеса де Кесады',
  'Visitaste la catedral más grande de Colombia, donde descansa el fundador de Bogotá.',
  'You visited Colombia''s largest cathedral, resting place of Bogotá''s founder.',
  'Vous avez visité la plus grande cathédrale de Colombie, où repose le fondateur de Bogotá.',
  'Вы посетили крупнейший собор Колумбии, где покоится основатель Боготы.'
),
(
  'a4a4a4a4-a4a4-a4a4-a4a4-a4a4a4a4a4a4',
  'monserrate',
  'https://picsum.photos/seed/monserrate/200',
  'legendary',
  'Peregrino de las Alturas', 'Pilgrim of the Heights', 'Pèlerin des Hauteurs', 'Паломник высот',
  'Subiste al Cerro de Monserrate y contemplaste Bogotá desde más de 3.100 metros.',
  'You climbed Cerro de Monserrate and took in Bogotá from over 3,100 meters.',
  'Vous avez gravi le Cerro de Monserrate et contemplé Bogotá depuis plus de 3 100 mètres.',
  'Вы поднялись на гору Монсеррате и увидели Боготу с высоты более 3100 метров.'
),
(
  'a5a5a5a5-a5a5-a5a5-a5a5-a5a5a5a5a5a5',
  'torre-colpatria',
  'https://picsum.photos/seed/torre-colpatria/200',
  'rare',
  'Vigía de la Sabana', 'Watcher of the Savanna', 'Vigie de la Savane', 'Страж саванны',
  'Subiste al mirador de uno de los edificios más altos de Bogotá.',
  'You went up to the observation deck of one of Bogotá''s tallest buildings.',
  'Vous êtes monté au belvédère de l''un des plus hauts immeubles de Bogotá.',
  'Вы поднялись на смотровую площадку одного из самых высоких зданий Боготы.'
),
(
  'a6a6a6a6-a6a6-a6a6-a6a6-a6a6a6a6a6a6',
  'jardin-botanico',
  'https://picsum.photos/seed/jardin-botanico/200',
  'rare',
  'Guardián del Páramo', 'Guardian of the Páramo', 'Gardien du Páramo', 'Хранитель парамо',
  'Recorriste las colecciones de flora nativa del Jardín Botánico de Bogotá.',
  'You explored the native flora collections at Bogotá''s Botanical Garden.',
  'Vous avez exploré les collections de flore native du Jardin botanique de Bogotá.',
  'Вы осмотрели коллекции местной флоры в Ботаническом саду Боготы.'
),
(
  'a7a7a7a7-a7a7-a7a7-a7a7-a7a7a7a7a7a7',
  'planetario-bogota',
  'https://picsum.photos/seed/planetario-bogota/200',
  'common',
  'Explorador del Cosmos', 'Explorer of the Cosmos', 'Explorateur du Cosmos', 'Исследователь космоса',
  'Visitaste el Planetario de Bogotá y su icónica cúpula.',
  'You visited the Bogotá Planetarium and its iconic dome.',
  'Vous avez visité le Planétarium de Bogotá et sa coupole emblématique.',
  'Вы посетили Планетарий Боготы с его узнаваемым куполом.'
),
(
  'a8a8a8a8-a8a8-a8a8-a8a8-a8a8a8a8a8a8',
  'biblioteca-virgilio-barco',
  'https://picsum.photos/seed/biblioteca-virgilio-barco/200',
  'rare',
  'Lector de Ladrillo y Agua', 'Reader of Brick and Water', 'Lecteur de Brique et d''Eau', 'Читатель кирпича и воды',
  'Visitaste la biblioteca diseñada por Rogelio Salmona y su parque integrado.',
  'You visited the library designed by Rogelio Salmona and its integrated park.',
  'Vous avez visité la bibliothèque conçue par Rogelio Salmona et son parc intégré.',
  'Вы посетили библиотеку, спроектированную Рохелио Сальмоной, и прилегающий парк.'
),
(
  'a9a9a9a9-a9a9-a9a9-a9a9-a9a9a9a9a9a9',
  'parque-usaquen',
  'https://picsum.photos/seed/parque-usaquen/200',
  'common',
  'Cazador de Pulgas', 'Flea Market Hunter', 'Chasseur de Puces', 'Охотник за барахолкой',
  'Recorriste el mercado de las pulgas del antiguo pueblo de Usaquén.',
  'You explored the flea market in the old town of Usaquén.',
  'Vous avez parcouru le marché aux puces de l''ancien village d''Usaquén.',
  'Вы прошлись по блошиному рынку старого посёлка Усакен.'
),
(
  'b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1',
  'parque-93',
  'https://picsum.photos/seed/parque-93/200',
  'common',
  'Sobremesa de Chapinero', 'Chapinero Regular', 'Habitué de Chapinero', 'Завсегдатай Чапинеро',
  'Visitaste el parque urbano más concurrido de Chapinero.',
  'You visited Chapinero''s busiest urban park.',
  'Vous avez visité le parc urbain le plus fréquenté de Chapinero.',
  'Вы посетили самый оживлённый городской парк Чапинеро.'
),
(
  'b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2',
  'parque-simon-bolivar',
  'https://picsum.photos/seed/parque-simon-bolivar/200',
  'rare',
  'Corredor del Parque Grande', 'Runner of the Big Park', 'Coureur du Grand Parc', 'Бегун большого парка',
  'Recorriste el parque metropolitano más grande de Bogotá.',
  'You explored Bogotá''s largest metropolitan park.',
  'Vous avez parcouru le plus grand parc métropolitain de Bogotá.',
  'Вы прошли по крупнейшему столичному парку Боготы.'
);

insert into public.historical_sites (
  id, code, department, city, lat, lng, geofence_radius_m, vision_reference,
  title_es, title_en, title_fr, title_ru,
  narrative_es, narrative_en, narrative_fr, narrative_ru,
  xp_reward, badge_id
)
values
(
  'dddddddd-dddd-dddd-dddd-dddddddddddd',
  'bogota-quinta-de-bolivar',
  'Cundinamarca', 'Bogotá',
  4.60257340, -74.06285120,
  50,
  '{}'::jsonb,
  'Quinta de Bolívar', 'Quinta de Bolívar', 'Quinta de Bolívar', 'Кинта-де-Боливар',
  'Esta antigua casa de campo colonial, a los pies de Monserrate, fue residencia del Libertador Simón Bolívar. Hoy funciona como museo casa con mobiliario de época, objetos personales y jardines de estilo republicano.',
  'This colonial-era country house at the foot of Monserrate was once home to Simón Bolívar, the Liberator. It now operates as a house museum with period furnishings, personal artifacts, and Republican gardens.',
  'Cette ancienne maison de campagne coloniale, au pied de Monserrate, fut la résidence du Libérateur Simón Bolívar. Elle abrite aujourd''hui un musée avec du mobilier d''époque, des objets personnels et des jardins de style républicain.',
  'Этот старинный колониальный загородный дом у подножия Монсеррате был резиденцией Симона Боливара, Освободителя. Сегодня здесь дом-музей с мебелью той эпохи, личными вещами и садами в республиканском стиле.',
  130,
  'a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1'
),
(
  'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
  'bogota-capitolio-nacional',
  'Cundinamarca', 'Bogotá',
  4.59721220, -74.07662250,
  50,
  '{}'::jsonb,
  'Capitolio Nacional', 'National Capitol', 'Capitole National', 'Национальный Капитолий',
  'Sede del Congreso de la República, este imponente edificio neoclásico ocupa un costado de la Plaza de Bolívar. Su construcción se extendió por varias décadas entre el siglo XIX y el XX.',
  'Home to the Colombian Congress, this imposing neoclassical building occupies one side of Plaza de Bolívar. Its construction stretched across several decades from the 19th into the 20th century.',
  'Siège du Congrès de la République, cet imposant bâtiment néoclassique occupe un côté de la Plaza de Bolívar. Sa construction s''est étalée sur plusieurs décennies entre le XIXe et le XXe siècle.',
  'Здание Национального конгресса Колумбии — внушительное неоклассическое сооружение на одной из сторон площади Боливара. Его строительство растянулось на несколько десятилетий между XIX и XX веками.',
  150,
  'a2a2a2a2-a2a2-a2a2-a2a2-a2a2a2a2a2a2'
),
(
  'ffffffff-ffff-ffff-ffff-ffffffffffff',
  'bogota-catedral-primada',
  'Cundinamarca', 'Bogotá',
  4.59784210, -74.07514690,
  50,
  '{}'::jsonb,
  'Catedral Primada de Bogotá', 'Bogotá Primate Cathedral', 'Cathédrale primatiale de Bogotá', 'Кафедральный собор Боготы',
  'La catedral más grande de Colombia, de fachada neoclásica, se levanta en la Plaza de Bolívar. En su interior descansan los restos del fundador de Bogotá, Gonzalo Jiménez de Quesada.',
  'Colombia''s largest cathedral, with a neoclassical façade, stands on Plaza de Bolívar. Inside rest the remains of Bogotá''s founder, Gonzalo Jiménez de Quesada.',
  'La plus grande cathédrale de Colombie, à la façade néoclassique, se dresse sur la Plaza de Bolívar. En son sein reposent les restes du fondateur de Bogotá, Gonzalo Jiménez de Quesada.',
  'Крупнейший собор Колумбии с неоклассическим фасадом стоит на площади Боливара. Внутри покоятся останки основателя Боготы, Гонсало Хименеса де Кесады.',
  140,
  'a3a3a3a3-a3a3-a3a3-a3a3-a3a3a3a3a3a3'
),
(
  '22222222-2222-2222-2222-222222222222',
  'bogota-monserrate',
  'Cundinamarca', 'Bogotá',
  4.60725390, -74.05430900,
  80,
  '{}'::jsonb,
  'Monserrate', 'Monserrate', 'Monserrate', 'Монсеррате',
  'Desde el Cerro de Monserrate, a más de 3.100 m de altura, se domina una vista panorámica de toda Bogotá. Se llega por un sendero empedrado o en teleférico/funicular; en la cima está el santuario del "Señor Caído", destino de peregrinación.',
  'From Cerro de Monserrate, over 3,100 m above sea level, you get a sweeping panoramic view of Bogotá. Reach the summit on foot via a stone path or by cable car/funicular; at the top stands the sanctuary of the venerated "Fallen Christ," a major pilgrimage site.',
  'Depuis le Cerro de Monserrate, à plus de 3 100 m d''altitude, on domine une vue panoramique sur tout Bogotá. On y accède à pied par un sentier pavé ou en téléphérique/funiculaire ; au sommet se trouve le sanctuaire du « Seigneur déchu », un important lieu de pèlerinage.',
  'С горы Монсеррате высотой более 3100 метров открывается панорамный вид на всю Боготу. Подняться можно пешком по мощёной тропе или на канатной дороге/фуникулёре; на вершине — святилище «Павшего Христа», место паломничества.',
  200,
  'a4a4a4a4-a4a4-a4a4-a4a4-a4a4a4a4a4a4'
),
(
  '33333333-3333-3333-3333-333333333333',
  'bogota-torre-colpatria',
  'Cundinamarca', 'Bogotá',
  4.61099360, -74.07024770,
  50,
  '{}'::jsonb,
  'Torre Colpatria', 'Colpatria Tower', 'Tour Colpatria', 'Башня Колпатрия',
  'Uno de los edificios más altos de Bogotá, con un mirador desde donde se aprecia la ciudad en 360 grados, incluyendo los cerros orientales y la sabana.',
  'One of Bogotá''s tallest buildings, with an observation deck offering 360-degree views of the city, including the eastern hills and the surrounding savanna.',
  'L''un des plus hauts immeubles de Bogotá, doté d''un belvédère offrant une vue à 360 degrés sur la ville, y compris les collines orientales et la savane.',
  'Одно из самых высоких зданий Боготы со смотровой площадкой, откуда открывается панорамный вид на город на 360 градусов, включая восточные холмы и саванну.',
  140,
  'a5a5a5a5-a5a5-a5a5-a5a5-a5a5a5a5a5a5'
),
(
  '77777777-7777-7777-7777-777777777777',
  'bogota-jardin-botanico',
  'Cundinamarca', 'Bogotá',
  4.66789530, -74.10008580,
  60,
  '{}'::jsonb,
  'Jardín Botánico José Celestino Mutis', 'José Celestino Mutis Botanical Garden', 'Jardin botanique José Celestino Mutis', 'Ботанический сад Хосе Селестино Мутиса',
  'Un extenso jardín botánico con colecciones de flora nativa colombiana, de páramo y bosque andino, además del Tropicario, un invernadero que recrea distintos ecosistemas tropicales del país.',
  'A sprawling botanical garden with collections of native Colombian flora, from páramo to Andean forest, plus the Tropicario, a greenhouse recreating the country''s tropical ecosystems.',
  'Un vaste jardin botanique avec des collections de flore native colombienne, du páramo à la forêt andine, ainsi que le Tropicario, une serre recréant les écosystèmes tropicaux du pays.',
  'Обширный ботанический сад с коллекциями местной колумбийской флоры — от парамо до андских лесов, а также «Тропикарио» — оранжерея, воссоздающая тропические экосистемы страны.',
  130,
  'a6a6a6a6-a6a6-a6a6-a6a6-a6a6a6a6a6a6'
),
(
  '88888888-8888-8888-8888-888888888888',
  'bogota-planetario',
  'Cundinamarca', 'Bogotá',
  4.61205760, -74.06882180,
  40,
  '{}'::jsonb,
  'Planetario de Bogotá', 'Bogotá Planetarium', 'Planétarium de Bogotá', 'Планетарий Боготы',
  'Ubicado en el Parque de la Independencia, este centro de divulgación científica ofrece proyecciones bajo su icónica cúpula sobre astronomía y el cosmos, junto con salas interactivas de ciencia.',
  'Located in Parque de la Independencia, this science center offers dome projection shows about astronomy and the cosmos, along with interactive science exhibition halls.',
  'Situé dans le Parque de la Independencia, ce centre de diffusion scientifique propose des projections sous sa coupole emblématique sur l''astronomie et le cosmos, ainsi que des salles d''exposition interactives.',
  'Расположенный в парке Индепенденсия, этот научный центр предлагает проекционные шоу под своим узнаваемым куполом об астрономии и космосе, а также интерактивные научные экспозиции.',
  100,
  'a7a7a7a7-a7a7-a7a7-a7a7-a7a7a7a7a7a7'
),
(
  '99999999-9999-9999-9999-999999999999',
  'bogota-biblioteca-virgilio-barco',
  'Cundinamarca', 'Bogotá',
  4.65695290, -74.08829740,
  50,
  '{}'::jsonb,
  'Biblioteca Virgilio Barco', 'Virgilio Barco Library', 'Bibliothèque Virgilio Barco', 'Библиотека Вирхилио Барко',
  'Diseñada por el arquitecto Rogelio Salmona, esta biblioteca pública se integra con un gran parque, destacando por sus muros curvos de ladrillo, terrazas y estanques que combinan arquitectura y naturaleza.',
  'Designed by architect Rogelio Salmona, this public library is integrated with a large park, notable for its curved brick walls, terraces, and reflecting pools blending architecture and nature.',
  'Conçue par l''architecte Rogelio Salmona, cette bibliothèque publique s''intègre à un grand parc, remarquable pour ses murs de brique courbes, ses terrasses et ses bassins qui allient architecture et nature.',
  'Спроектированная архитектором Рохелио Сальмоной, эта публичная библиотека органично вписана в большой парк — её отличают изогнутые кирпичные стены, террасы и пруды, сочетающие архитектуру и природу.',
  120,
  'a8a8a8a8-a8a8-a8a8-a8a8-a8a8a8a8a8a8'
),
(
  '06060606-0606-0606-0606-060606060606',
  'bogota-parque-usaquen',
  'Cundinamarca', 'Bogotá',
  4.69517040, -74.03094080,
  60,
  '{}'::jsonb,
  'Parque de Usaquén', 'Usaquén Park', 'Parc d''Usaquén', 'Парк Усакен',
  'Esta plaza es el corazón del antiguo pueblo de Usaquén, con calles empedradas, casas coloniales y una iglesia. Los fines de semana se llena con el tradicional mercado de las pulgas: artesanías, comida y arte.',
  'This plaza is the heart of the former town of Usaquén, with cobblestone streets, colonial houses, and a church. On weekends it fills with the traditional flea market: handicrafts, food, and art.',
  'Cette place est le cœur de l''ancien village d''Usaquén, avec ses rues pavées, ses maisons coloniales et son église. Le week-end, elle s''anime avec le traditionnel marché aux puces : artisanat, gastronomie et art.',
  'Эта площадь — сердце бывшего посёлка Усакен, с мощёными улицами, колониальными домами и церковью. По выходным здесь проходит традиционная блошиная ярмарка: ремесленные изделия, еда и искусство.',
  110,
  'a9a9a9a9-a9a9-a9a9-a9a9-a9a9a9a9a9a9'
),
(
  '12121212-1212-1212-1212-121212121212',
  'bogota-parque-93',
  'Cundinamarca', 'Bogotá',
  4.67676800, -74.04828740,
  50,
  '{}'::jsonb,
  'Parque de la 93', 'Parque de la 93', 'Parc de la 93', 'Парк де ла 93',
  'Un parque urbano rodeado de restaurantes, cafés y oficinas en pleno Chapinero, popular como punto de encuentro y para comer al aire libre en una de las zonas más exclusivas de la ciudad.',
  'An urban park surrounded by restaurants, cafés, and office buildings in Chapinero, popular as a meeting spot and for outdoor dining in one of the city''s most upscale areas.',
  'Un parc urbain entouré de restaurants, de cafés et de bureaux en plein Chapinero, prisé comme lieu de rencontre et pour dîner en plein air dans l''un des quartiers les plus huppés de la ville.',
  'Городской парк, окружённый ресторанами, кафе и офисами в районе Чапинеро — популярное место встреч и обедов на свежем воздухе в одном из самых престижных районов города.',
  90,
  'b1b1b1b1-b1b1-b1b1-b1b1-b1b1b1b1b1b1'
),
(
  '34343434-3434-3434-3434-343434343434',
  'bogota-parque-simon-bolivar',
  'Cundinamarca', 'Bogotá',
  4.65877740, -74.09409710,
  80,
  '{}'::jsonb,
  'Parque Simón Bolívar', 'Simón Bolívar Park', 'Parc Simón Bolívar', 'Парк Симона Боливара',
  'El parque metropolitano más grande de Bogotá, con lagos, senderos, canchas deportivas y grandes zonas verdes. Es sede habitual de festivales masivos como Rock al Parque.',
  'Bogotá''s largest metropolitan park, with lakes, trails, sports fields, and large green spaces. It regularly hosts massive festivals such as Rock al Parque.',
  'Le plus grand parc métropolitain de Bogotá, avec des lacs, des sentiers, des terrains de sport et de vastes espaces verts. Il accueille régulièrement des festivals de grande ampleur comme Rock al Parque.',
  'Крупнейший столичный парк Боготы с озёрами, тропами, спортивными площадками и обширными зелёными зонами. Здесь регулярно проходят масштабные фестивали, такие как Rock al Parque.',
  130,
  'b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2'
);
