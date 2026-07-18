-- ============================================================================
-- Pivote de contenido "Bogotá Oculta y Secreta" (2026-07-18).
--
-- 1) Se retiran del catálogo público los 3 sitios que el Director marcó como
--    genéricos (Parque de Usaquén, Parque de la 93, Parque Simón Bolívar).
--    SOFT DELETE (`is_active = false`), no DELETE: sus filas se quedan por
--    integridad referencial con user_visits/site_reviews que ya pudieran
--    existir; `sites_public_read` (ver 20260717120000_init_schema.sql) ya
--    filtra por `is_active`, así que dejan de aparecer sin romper nada.
--
-- 2) Se agregan 6 misiones nuevas de leyendas urbanas REALMENTE documentadas
--    (no inventadas) — mismos UUIDs que sus SiteBrief equivalentes en
--    android/.../data/local/SiteCatalog.kt. FR/RU son traducción directa del
--    mismo contenido ES/EN ya verificado, no datos nuevos.
-- ============================================================================

update public.historical_sites
set is_active = false
where code in ('bogota-parque-usaquen', 'bogota-parque-93', 'bogota-parque-simon-bolivar');

insert into public.badges (id, code, icon_url, rarity, name_es, name_en, name_fr, name_ru, description_es, description_en, description_fr, description_ru)
values
(
  'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1',
  'casa-del-bandido',
  'https://picsum.photos/seed/casa-del-bandido/200',
  'rare',
  'Testigo del Bandido', 'Witness of the Bandit', 'Témoin du Bandit', 'Свидетель Бандита',
  'Visitaste la casa donde la tradición dice que rondaba el fantasma de un bandido ejecutado.',
  'You visited the house where tradition says the ghost of an executed bandit once roamed.',
  'Vous avez visité la maison où, selon la tradition, rôdait le fantôme d''un bandit exécuté.',
  'Вы посетили дом, где, по преданию, обитал призрак казнённого бандита.'
),
(
  'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2',
  'hospital-san-juan-de-dios',
  'https://picsum.photos/seed/hospital-san-juan-de-dios/200',
  'epic',
  'Cronista del Hospital Olvidado', 'Chronicler of the Forgotten Hospital', 'Chroniqueur de l''Hôpital Oublié', 'Летописец забытой больницы',
  'Visitaste el exterior del hospital abandonado y sus leyendas.',
  'You visited the exterior of the abandoned hospital and its legends.',
  'Vous avez visité l''extérieur de l''hôpital abandonné et de ses légendes.',
  'Вы посетили территорию заброшенной больницы и её легенд снаружи.'
),
(
  'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3',
  'tumba-del-astronomo',
  'https://picsum.photos/seed/tumba-del-astronomo/200',
  'epic',
  'Devoto del Billete de 20 Mil', 'Devotee of the 20,000 Bill', 'Dévot du Billet de 20 000', 'Почитатель банкноты 20 000',
  'Visitaste la tumba del astrónomo venerado como amuleto de prosperidad económica.',
  'You visited the tomb of the astronomer venerated as a good-luck charm for financial prosperity.',
  'Vous avez visité la tombe de l''astronome vénéré comme porte-bonheur financier.',
  'Вы посетили могилу астронома, почитаемого как источник финансовой удачи.'
),
(
  'c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4',
  'antiguo-bronx',
  'https://picsum.photos/seed/antiguo-bronx/200',
  'rare',
  'Testigo de la Transformación', 'Witness to the Transformation', 'Témoin de la Transformation', 'Свидетель преображения',
  'Visitaste el barrio transformado que alguna vez fue El Bronx.',
  'You visited the transformed neighborhood once known as El Bronx.',
  'Vous avez visité le quartier transformé qui fut autrefois El Bronx.',
  'Вы посетили квартал, преобразившийся из некогда печально известного Эль-Бронкса.'
),
(
  'c5c5c5c5-c5c5-c5c5-c5c5-c5c5c5c5c5c5',
  'castillo-marroquin',
  'https://picsum.photos/seed/castillo-marroquin/200',
  'epic',
  'Huésped de la Monja Errante', 'Guest of the Wandering Nun', 'Invité de la Nonne Errante', 'Гость блуждающей монахини',
  'Visitaste el castillo de múltiples vidas y leyendas.',
  'You visited the castle of many lives and legends.',
  'Vous avez visité le château aux multiples vies et légendes.',
  'Вы посетили замок с богатой историей и легендами.'
),
(
  'c6c6c6c6-c6c6-c6c6-c6c6-c6c6c6c6c6c6',
  'piedra-del-muerto',
  'https://picsum.photos/seed/piedra-del-muerto/200',
  'rare',
  'Testigo de la Maldición', 'Witness to the Curse', 'Témoin de la Malédiction', 'Свидетель проклятия',
  'Visitaste la roca legendaria que, según cuentan, sangra y llora.',
  'You visited the legendary rock said to bleed and cry.',
  'Vous avez visité le rocher légendaire qui, dit-on, saigne et pleure.',
  'Вы посетили легендарный камень, который, по преданию, кровоточит и плачет.'
);

insert into public.historical_sites (
  id, code, department, city, lat, lng, geofence_radius_m, vision_reference,
  title_es, title_en, title_fr, title_ru,
  narrative_es, narrative_en, narrative_fr, narrative_ru,
  xp_reward, badge_id
)
values
(
  '56565656-5656-5656-5656-565656565656',
  'bogota-casa-del-bandido',
  'Cundinamarca', 'Bogotá',
  4.59538110, -74.07093190,
  60,
  '{}'::jsonb,
  'Casa del Bandido', 'House of the Bandit', 'Maison du Bandit', 'Дом Бандита',
  'En esta cuadra de La Candelaria, la tradición oral ubica la casa donde habría rondado el espíritu del abogado Raimundo Russi, fusilado en 1851 acusado de liderar una banda criminal. Vecinos antiguos contaban que se persignaban al pasar frente a la casa.',
  'On this block of La Candelaria, oral tradition places the house said to be haunted by the spirit of lawyer Raimundo Russi, executed by firing squad in 1851 for allegedly leading a criminal gang. Longtime neighbors reportedly crossed themselves walking past it.',
  'Dans ce pâté de maisons de La Candelaria, la tradition orale situe la maison où aurait rôdé l''esprit de l''avocat Raimundo Russi, fusillé en 1851 pour avoir prétendument dirigé une bande criminelle. Les anciens voisins racontaient qu''ils se signaient en passant devant la maison.',
  'На этом квартале Ла-Канделарии устная традиция помещает дом, где, по преданию, обитал дух адвоката Раймундо Русси, расстрелянного в 1851 году по обвинению в руководстве преступной бандой. Старожилы рассказывали, что крестились, проходя мимо этого дома.',
  160,
  'c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1'
),
(
  '78787878-7878-7878-7878-787878787878',
  'bogota-hospital-san-juan-de-dios',
  'Cundinamarca', 'Bogotá',
  4.58968910, -74.08770500,
  70,
  '{}'::jsonb,
  'Hospital San Juan de Dios', 'San Juan de Dios Hospital', 'Hôpital San Juan de Dios', 'Больница Сан-Хуан-де-Дьос',
  'Fundado en 1564 y trasladado a este lugar en 1925, el hospital cerró en 2001 tras 458 años de servicio. Hoy está en restauración: un túnel real conecta la torre central con el antiguo Instituto Materno Infantil. Es zona de obra activa — se admira desde fuera, no se entra.',
  'Founded in 1564 and moved here in 1925, the hospital closed in 2001 after 458 years of service. It''s under restoration today: a real tunnel connects the central tower to the former maternity institute. It''s an active construction zone — admire it from outside, don''t enter.',
  'Fondé en 1564 et transféré ici en 1925, l''hôpital a fermé en 2001 après 458 ans de service. Il est aujourd''hui en restauration : un vrai tunnel relie la tour centrale à l''ancien institut materno-infantile. C''est une zone de travaux active — on l''admire de l''extérieur, on n''y entre pas.',
  'Основанная в 1564 году и переехавшая сюда в 1925-м, больница закрылась в 2001 году после 458 лет службы. Сегодня она находится на реставрации: настоящий туннель соединяет центральную башню с бывшим родильным институтом. Это активная строительная зона — её можно рассматривать снаружи, но не заходить внутрь.',
  180,
  'c2c2c2c2-c2c2-c2c2-c2c2-c2c2c2c2c2c2'
),
(
  '9c9c9c9c-9c9c-9c9c-9c9c-9c9c9c9c9c9c',
  'bogota-tumba-del-astronomo',
  'Cundinamarca', 'Bogotá',
  4.61744500, -74.07588080,
  120,
  '{}'::jsonb,
  'Cementerio Central — Tumba de Julio Garavito', 'Central Cemetery — Julio Garavito''s Tomb', 'Cimetière Central — Tombe de Julio Garavito', 'Центральное кладбище — могила Хулио Гаравито',
  'Aquí descansa Julio Garavito Armero (1865-1920), matemático, ingeniero y astrónomo colombiano — no astrólogo, como suele decirse — cuyo rostro aparece en el billete de 20.000 pesos y que tiene un cráter lunar con su nombre. Su tumba recibe visitas constantes de quienes le piden prosperidad económica golpeando la lápida.',
  'Here lies Julio Garavito Armero (1865-1920), Colombian mathematician, engineer and astronomer — not astrologer, as popular speech often has it — whose face appears on the 20,000-peso bill and who has a lunar crater named after him. His tomb draws constant visits from people knocking on the stone asking for financial prosperity.',
  'Ici repose Julio Garavito Armero (1865-1920), mathématicien, ingénieur et astronome colombien — non astrologue, comme on le dit souvent —, dont le visage figure sur le billet de 20 000 pesos et qui a un cratère lunaire à son nom. Sa tombe reçoit des visites constantes de personnes qui lui demandent la prospérité économique en frappant la pierre.',
  'Здесь покоится Хулио Гаравито Армеро (1865-1920), колумбийский математик, инженер и астроном — а не астролог, как часто ошибочно говорят, — чьё лицо изображено на банкноте в 20 000 песо и в честь которого назван лунный кратер. Его могилу постоянно посещают люди, стучащие по камню в надежде на финансовое процветание.',
  170,
  'c3c3c3c3-c3c3-c3c3-c3c3-c3c3c3c3c3c3'
),
(
  'bdbdbdbd-bdbd-bdbd-bdbd-bdbdbdbdbdbd',
  'bogota-antiguo-bronx',
  'Cundinamarca', 'Bogotá',
  4.60111260, -74.08277160,
  80,
  '{}'::jsonb,
  'Bronx Distrito Creativo', 'Bronx Creative District', 'Bronx Distrito Creativo', 'Бронкс Дистрито Креативо',
  'El 28 de mayo de 2016, la fuerza pública intervino este sector, entonces conocido como "El Bronx", rescatando a 149 menores. Desde entonces la ciudad construyó aquí el Bronx Distrito Creativo: un proyecto de renovación urbana centrado en cultura y memoria, ganador del premio "Retrofit of the Year" 2025 en los GRI Awards.',
  'On May 28, 2016, police intervened in this area, then known as "El Bronx," rescuing 149 minors. Since then the city has built the Bronx Distrito Creativo here: an urban renewal project centered on culture and memory, winner of the 2025 GRI Awards "Retrofit of the Year."',
  'Le 28 mai 2016, les forces de l''ordre sont intervenues dans ce secteur, alors connu sous le nom de « El Bronx », sauvant 149 mineurs. Depuis, la ville y a construit le Bronx Distrito Creativo : un projet de rénovation urbaine centré sur la culture et la mémoire, lauréat du prix « Retrofit of the Year » 2025 aux GRI Awards.',
  '28 мая 2016 года силы правопорядка провели операцию в этом районе, тогда известном как «Эль-Бронкс», освободив 149 несовершеннолетних. С тех пор город построил здесь «Бронкс Дистрито Креативо» — проект городского обновления, посвящённый культуре и памяти, победитель премии GRI Awards 2025 года в номинации «Реконструкция года».',
  150,
  'c4c4c4c4-c4c4-c4c4-c4c4-c4c4c4c4c4c4'
),
(
  'cececece-cece-cece-cece-cececececece',
  'chia-castillo-marroquin',
  'Cundinamarca', 'Chía',
  4.86257500, -74.02590833,
  100,
  '{}'::jsonb,
  'Castillo Marroquín', 'Marroquín Castle', 'Château Marroquín', 'Замок Марокин',
  'Construido en 1898 por el arquitecto francés Gastón Lelarge, este castillo en Chía (a las afueras de Bogotá) fue cabaret, hospital psiquiátrico para mujeres y residencia privada antes de convertirse en centro de eventos y patrimonio cultural. La tradición local le atribuye varias apariciones, entre ellas una "monja errante".',
  'Built in 1898 by French architect Gastón Lelarge, this castle in Chía (just outside Bogotá) served as a cabaret, a women''s psychiatric hospital, and a private residence before becoming an events venue and heritage site. Local tradition attributes several ghost sightings to it, including a "wandering nun."',
  'Construit en 1898 par l''architecte français Gastón Lelarge, ce château à Chía (en périphérie de Bogotá) a été cabaret, hôpital psychiatrique pour femmes et résidence privée avant de devenir un lieu événementiel et patrimonial. La tradition locale lui attribue plusieurs apparitions, dont une « nonne errante ».',
  'Построенный в 1898 году французским архитектором Гастоном Лелажем, этот замок в Чиа (на окраине Боготы) был кабаре, психиатрической больницей для женщин и частной резиденцией, прежде чем стать площадкой для мероприятий и объектом культурного наследия. Местная традиция приписывает ему несколько явлений призраков, включая «блуждающую монахиню».',
  190,
  'c5c5c5c5-c5c5-c5c5-c5c5-c5c5c5c5c5c5'
),
(
  'dfdfdfdf-dfdf-dfdf-dfdf-dfdfdfdfdfdf',
  'bogota-piedra-del-muerto',
  'Cundinamarca', 'Bogotá',
  4.55227650, -74.14446030,
  150,
  '{}'::jsonb,
  'La Piedra del Muerto', 'The Dead Man''s Stone', 'La Pierre du Mort', 'Камень Мертвеца',
  'Entre el barrio Capri y el sector de Vista Hermosa, en Ciudad Bolívar, hay una roca de unos 4 metros con forma de hombre acostado. Según el libro de Álvaro Cristancho Lozano, la leyenda cuenta que una madre maldijo a su hijo perezoso y este quedó petrificado al caer por la ladera.',
  'Between the Capri neighborhood and the Vista Hermosa sector, in Ciudad Bolívar, sits a roughly 4-meter rock shaped like a reclining man. Per a book by Álvaro Cristancho Lozano, legend says a mother cursed her lazy son and he was turned to stone falling down the hillside.',
  'Entre le quartier Capri et le secteur de Vista Hermosa, à Ciudad Bolívar, se trouve un rocher d''environ 4 mètres en forme d''homme allongé. Selon un livre d''Álvaro Cristancho Lozano, la légende raconte qu''une mère maudit son fils paresseux, qui fut pétrifié en tombant sur le versant.',
  'Между районом Капри и сектором Виста-Эрмоса в Сьюдад-Боливар находится примерно 4-метровая скала в форме лежащего человека. Согласно книге Альваро Кристанчо Лосано, легенда гласит, что мать прокляла своего ленивого сына, и тот окаменел, упав со склона.',
  160,
  'c6c6c6c6-c6c6-c6c6-c6c6-c6c6c6c6c6c6'
);
