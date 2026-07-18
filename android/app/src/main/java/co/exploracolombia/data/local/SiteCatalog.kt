package co.exploracolombia.data.local

import co.exploracolombia.domain.model.AlbumPage
import co.exploracolombia.domain.model.BadgeBrief
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.SiteBrief

/**
 * Catálogo local de hitos "cercanos" para el mapa y el Álbum. Copia exacta
 * del contenido de supabase/seed.sql — cuando exista un endpoint real de
 * "sitios cercanos a mi ubicación", este objeto se reemplaza por una
 * llamada de red, pero la forma (SiteBrief) no debería cambiar.
 *
 * 18 hitos en total, todos con coordenadas reales:
 *  - 4 hitos de la ciudad amurallada de Cartagena. Los 3 que no son el
 *    Baluarte tienen coordenadas ILUSTRATIVAS (ajustadas a mano para caer a
 *    ~100/300/500m exactos del Baluarte, como se pidió explícitamente
 *    "puntos simulados") — no son un levantamiento topográfico real.
 *  - 14 hitos de Bogotá (La Candelaria + Chapinero + Usaquén + cerros
 *    orientales), con coordenadas verificadas contra el geocodificador
 *    Nominatim de OpenStreetMap (no estimadas de memoria).
 *
 * `coverImageUrl` apunta a fotos reales de Wikimedia Commons (licencia
 * libre), verificadas una por una contra la API de Commons — no son
 * búsquedas de Google Images con derechos inciertos. Ojo con el nombre
 * "Cartagena": hay una Cartagena en España con varios de estos mismos
 * nombres de lugares (Iglesia de Santo Domingo, murallas púnicas...); cada
 * URL se confirmó que corresponde a Cartagena de Indias, Colombia.
 */
object SiteCatalog {
    val baluarteSanFrancisco = SiteBrief(
        id = "11111111-1111-1111-1111-111111111111",
        code = "cartagena-murallas-baluarte-san-francisco",
        department = "Bolívar",
        city = "Cartagena de Indias",
        lat = 10.4236,
        lng = -75.5478,
        geofenceRadiusMeters = 40,
        titleEs = "Baluarte de San Francisco",
        titleEn = "San Francisco Bastion",
        narrativeEs = "Estás frente a uno de los baluartes que formaron el sistema " +
            "defensivo más grande construido por España en América. Desde aquí, los " +
            "vigías avistaban las velas enemigas mucho antes de que llegaran a la bahía.",
        narrativeEn = "You are standing before one of the bastions that formed the " +
            "largest defensive system Spain ever built in the Americas. From here, " +
            "watchmen spotted enemy sails long before they reached the bay.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/" +
            "Baluarte_San_Francisco_Javier_CTG_11_2019_2175.jpg/960px-" +
            "Baluarte_San_Francisco_Javier_CTG_11_2019_2175.jpg",
        xpReward = 100,
        badge = BadgeBrief(
            code = "murallas-cartagena",
            nameEs = "Guardián de las Murallas",
            nameEn = "Guardian of the Walls",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 4,
        albumPage = AlbumPage.MURALLAS_DE_CARTAGENA,
        funFactEs = "Estos muros aguantaron cañonazos de piratas como Francis Drake. " +
            "Hoy en día lo único que \"ataca\" son los vendedores de raspao.",
        funFactEn = "These walls withstood cannon fire from pirates like Francis Drake. " +
            "These days the only thing \"attacking\" is the shaved-ice vendors.",
    )

    /** ~100m al norte del Baluarte. */
    val puertaDelReloj = SiteBrief(
        id = "44444444-4444-4444-4444-444444444444",
        code = "cartagena-puerta-del-reloj",
        department = "Bolívar",
        city = "Cartagena de Indias",
        lat = 10.4245,
        lng = -75.5478,
        geofenceRadiusMeters = 40,
        titleEs = "Puerta del Reloj",
        titleEn = "Clock Gate",
        narrativeEs = "Estás en la puerta principal de acceso a la ciudad amurallada, " +
            "coronada por su icónica torre del reloj. Durante siglos, todo comerciante " +
            "y viajero que entraba a Cartagena pasaba bajo este arco.",
        narrativeEn = "You're at the main gateway into the walled city, crowned by its " +
            "iconic clock tower. For centuries, every merchant and traveler entering " +
            "Cartagena passed beneath this arch.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fe/" +
            "024_Torre_del_Reloj_Cartagena_Colombia.JPG/960px-" +
            "024_Torre_del_Reloj_Cartagena_Colombia.JPG",
        xpReward = 75,
        badge = BadgeBrief(
            code = "puerta-del-reloj",
            nameEs = "Guardián del Reloj",
            nameEn = "Keeper of the Clock",
            rarity = BadgeRarity.COMMON,
        ),
        laminaNumber = 5,
        albumPage = AlbumPage.MURALLAS_DE_CARTAGENA,
        funFactEs = "El reloj que corona la puerta no es el original — el primero se " +
            "dañó tantas veces que los cartageneros perdieron la cuenta de la hora real.",
        funFactEn = "The clock crowning the gate isn't the original — the first one " +
            "broke down so many times that locals lost track of the real time.",
    )

    /** ~300m al oeste del Baluarte. */
    val iglesiaSantoDomingo = SiteBrief(
        id = "55555555-5555-5555-5555-555555555555",
        code = "cartagena-iglesia-santo-domingo",
        department = "Bolívar",
        city = "Cartagena de Indias",
        lat = 10.4236,
        lng = -75.5505,
        geofenceRadiusMeters = 40,
        titleEs = "Iglesia de Santo Domingo",
        titleEn = "Santo Domingo Church",
        narrativeEs = "La iglesia más antigua de Cartagena, con una torre inclinada que " +
            "sobrevive desde el siglo XVI. La leyenda cuenta que el mismísimo diablo " +
            "torció su estructura una noche de tormenta.",
        narrativeEn = "Cartagena's oldest church, with a leaning tower that has stood " +
            "since the 16th century. Legend says the devil himself twisted its " +
            "structure during a stormy night.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/" +
            "Fachada_frontal_Iglesia_de_Santo_Domingo._Cartagena_de_indias.jpg/960px-" +
            "Fachada_frontal_Iglesia_de_Santo_Domingo._Cartagena_de_indias.jpg",
        xpReward = 150,
        badge = BadgeBrief(
            code = "santo-domingo",
            nameEs = "Alma de Piedra",
            nameEn = "Soul of Stone",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 6,
        albumPage = AlbumPage.MURALLAS_DE_CARTAGENA,
        funFactEs = "Su torre está torcida de verdad — algunos dicen que fue un error " +
            "de construcción, otros prefieren culpar al diablo. Tú decides qué versión contar.",
        funFactEn = "Its tower really is crooked — some blame a construction error, " +
            "others prefer to blame the devil. You get to pick which version to tell.",
    )

    /** ~500m al este del Baluarte. */
    val lasBovedas = SiteBrief(
        id = "66666666-6666-6666-6666-666666666666",
        code = "cartagena-las-bovedas",
        department = "Bolívar",
        city = "Cartagena de Indias",
        lat = 10.4236,
        lng = -75.5432,
        geofenceRadiusMeters = 40,
        titleEs = "Las Bóvedas",
        titleEn = "The Vaults",
        narrativeEs = "Estas bóvedas de piedra sirvieron como depósito militar y, según " +
            "la tradición oral, como calabozo. Hoy sus 23 arcos albergan artesanías, " +
            "pero las paredes todavía guardan ecos de la Cartagena colonial.",
        narrativeEn = "These stone vaults once served as a military storehouse and, " +
            "according to oral tradition, a dungeon. Today their 23 arches house " +
            "craft shops, but the walls still echo colonial Cartagena.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/38/" +
            "LAS_BOVEDAS%2C_CARTAGENA%2C_COLOMBIA.jpg/960px-" +
            "LAS_BOVEDAS%2C_CARTAGENA%2C_COLOMBIA.jpg",
        xpReward = 200,
        badge = BadgeBrief(
            code = "las-bovedas",
            nameEs = "Centinela de las Bóvedas",
            nameEn = "Sentinel of the Vaults",
            rarity = BadgeRarity.LEGENDARY,
        ),
        laminaNumber = 7,
        albumPage = AlbumPage.MURALLAS_DE_CARTAGENA,
        funFactEs = "23 arcos que antes guardaban pólvora y prisioneros, hoy guardan " +
            "hamacas, sombreros vueltiaos y imanes para la nevera.",
        funFactEn = "23 arches that once held gunpowder and prisoners now hold " +
            "hammocks, straw hats, and fridge magnets.",
    )

    // Coordenadas verificadas el 2026-07-18 contra Nominatim (OpenStreetMap),
    // no estimadas de memoria — ver historial de la conversación.
    val plazaDeBolivar = SiteBrief(
        id = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
        code = "bogota-plaza-de-bolivar",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.598146,
        lng = -74.076004,
        geofenceRadiusMeters = 60,
        titleEs = "Plaza de Bolívar",
        titleEn = "Bolívar Square",
        narrativeEs = "Estás en el corazón cívico de Colombia: aquí laten el Capitolio " +
            "Nacional, la Alcaldía, el Palacio de Justicia y la Catedral Primada, todos " +
            "alrededor de la estatua de Simón Bolívar.",
        narrativeEn = "You're in the civic heart of Colombia: the National Capitol, City " +
            "Hall, the Palace of Justice and the Primatial Cathedral all surround the " +
            "statue of Simón Bolívar here.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/64/" +
            "Plaza_de_Bolivar%2C_Bogota%2C_Colombia_%285770632336%29.jpg/960px-" +
            "Plaza_de_Bolivar%2C_Bogota%2C_Colombia_%285770632336%29.jpg",
        xpReward = 100,
        badge = BadgeBrief(
            code = "plaza-bolivar",
            nameEs = "Corazón de la República",
            nameEn = "Heart of the Republic",
            rarity = BadgeRarity.COMMON,
        ),
        laminaNumber = 1,
        albumPage = AlbumPage.BOGOTA_COLONIAL,
        funFactEs = "¿Sabías que la plaza cambió de nombre varias veces y hasta fue " +
            "mercado de verduras? Ahora es el patio central de todo un país.",
        funFactEn = "Did you know the square changed names several times and was once " +
            "a vegetable market? Now it's the whole country's front yard.",
        // Fotografía real de 1900 (Wikimedia Commons, "Bogota recruiting
        // 1900.jpg", categorizada como fotografía histórica de la Plaza de
        // Bolívar) — verificada, no una foto moderna con filtro sepia.
        historicalPhotoUrl = "https://upload.wikimedia.org/wikipedia/commons/2/2e/Bogota_recruiting_1900.jpg",
        historicalPhotoYear = "1900",
    )

    val chorroDeQuevedo = SiteBrief(
        id = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
        code = "bogota-chorro-de-quevedo",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.597234,
        lng = -74.069708,
        geofenceRadiusMeters = 40,
        titleEs = "Chorro de Quevedo",
        titleEn = "Chorro de Quevedo",
        narrativeEs = "La tradición señala este rincón como el lugar exacto donde se " +
            "fundó Bogotá en 1538. Hoy es el corazón bohemio de La Candelaria, lleno de " +
            "artistas callejeros y cuenteros.",
        narrativeEn = "Tradition marks this corner as the exact spot where Bogotá was " +
            "founded in 1538. Today it's the bohemian heart of La Candelaria, full of " +
            "street artists and storytellers.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7f/" +
            "Chorro_quevedo.jpg/960px-Chorro_quevedo.jpg",
        xpReward = 125,
        badge = BadgeBrief(
            code = "chorro-quevedo",
            nameEs = "Fundador de la Ciudad",
            nameEn = "City Founder",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 2,
        albumPage = AlbumPage.BOGOTA_COLONIAL,
        funFactEs = "Cuenta la leyenda que aquí se dijo la primera misa de la ciudad " +
            "bajo un techo de paja. Hoy es más fácil encontrar un cuentero contando " +
            "chismes que un conquistador.",
        funFactEn = "Legend says the city's first mass was held here under a straw " +
            "roof. These days you're more likely to find a storyteller spilling gossip " +
            "than a conquistador.",
    )

    val museoDelOro = SiteBrief(
        id = "cccccccc-cccc-cccc-cccc-cccccccccccc",
        code = "bogota-museo-del-oro",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.601840,
        lng = -74.071853,
        geofenceRadiusMeters = 40,
        titleEs = "Museo del Oro",
        titleEn = "Gold Museum",
        narrativeEs = "Alberga la colección de orfebrería prehispánica más grande del " +
            "mundo: más de 30.000 piezas de oro que cuentan la historia de las " +
            "culturas indígenas de Colombia.",
        narrativeEn = "Home to the largest collection of pre-Hispanic goldwork in the " +
            "world: over 30,000 gold pieces that tell the story of Colombia's " +
            "Indigenous cultures.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/" +
            "BOG_Museo_del_Oro.JPG/960px-BOG_Museo_del_Oro.JPG",
        xpReward = 175,
        badge = BadgeBrief(
            code = "museo-del-oro",
            nameEs = "Buscador de El Dorado",
            nameEn = "Seeker of El Dorado",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 3,
        albumPage = AlbumPage.MUSEOS_E_HISTORIA,
        funFactEs = "Tiene tanto oro guardado que si lo derritieras todo llenarías más " +
            "de una tina... pero mejor no lo intentes, hay cámaras por todos lados.",
        funFactEn = "There's so much gold in here that melting it all down would fill " +
            "more than one bathtub... but maybe don't try, there are cameras everywhere.",
    )

    // ------------------------------------------------------------------
    // Ampliación de Bogotá (11 hitos nuevos) — coordenadas verificadas
    // contra Nominatim y fotos resueltas vía la API de Wikimedia Commons
    // (iiurlwidth), no URLs adivinadas. Ver el reporte de investigación en
    // el historial de esta conversación para el detalle de cada búsqueda.
    // ------------------------------------------------------------------

    val quintaDeBolivar = SiteBrief(
        id = "dddddddd-dddd-dddd-dddd-dddddddddddd",
        code = "bogota-quinta-de-bolivar",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6025734,
        lng = -74.0628512,
        geofenceRadiusMeters = 50,
        titleEs = "Quinta de Bolívar",
        titleEn = "Quinta de Bolívar",
        narrativeEs = "Esta antigua casa de campo colonial, a los pies de Monserrate, " +
            "fue residencia del Libertador Simón Bolívar. Hoy funciona como museo casa " +
            "con mobiliario de época, objetos personales y jardines de estilo republicano.",
        narrativeEn = "This colonial-era country house at the foot of Monserrate was " +
            "once home to Simón Bolívar, the Liberator. It now operates as a house " +
            "museum with period furnishings, personal artifacts, and Republican gardens.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b4/" +
            "Quinta_de_Bol%C3%ADvar.JPG/960px-Quinta_de_Bol%C3%ADvar.JPG",
        xpReward = 130,
        badge = BadgeBrief(
            code = "quinta-bolivar",
            nameEs = "Huésped del Libertador",
            nameEn = "Guest of the Liberator",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 8,
        albumPage = AlbumPage.BOGOTA_COLONIAL,
        funFactEs = "La casa fue un regalo del cabildo de Santa Fe a Bolívar en 1820. " +
            "Hoy es Monumento Nacional, pero en su época era simplemente su casa de descanso.",
        funFactEn = "The house was a gift from the Santa Fe town council to Bolívar in " +
            "1820. Today it's a National Monument, but back then it was just his weekend house.",
    )

    val capitolioNacional = SiteBrief(
        id = "eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee",
        code = "bogota-capitolio-nacional",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.5972122,
        lng = -74.0766225,
        geofenceRadiusMeters = 50,
        titleEs = "Capitolio Nacional",
        titleEn = "National Capitol",
        narrativeEs = "Sede del Congreso de la República, este imponente edificio " +
            "neoclásico ocupa un costado de la Plaza de Bolívar. Su construcción se " +
            "extendió por varias décadas entre el siglo XIX y el XX.",
        narrativeEn = "Home to the Colombian Congress, this imposing neoclassical " +
            "building occupies one side of Plaza de Bolívar. Its construction stretched " +
            "across several decades from the 19th into the 20th century.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/" +
            "Capitolio_Nacional%2C_Bogot%C3%A1.JPG/960px-Capitolio_Nacional%2C_Bogot%C3%A1.JPG",
        xpReward = 150,
        badge = BadgeBrief(
            code = "capitolio-nacional",
            nameEs = "Testigo de la República",
            nameEn = "Witness to the Republic",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 9,
        albumPage = AlbumPage.BOGOTA_COLONIAL,
        funFactEs = "Se demoró tanto en construirse que pasó por las manos de varios " +
            "arquitectos y de más gobiernos de los que cualquiera quisiera contar.",
        funFactEn = "Construction took so long that it passed through several architects " +
            "and more governments than anyone cares to count.",
        // Postal real de los años 30 (Wikimedia Commons) con vista aérea del Capitolio.
        historicalPhotoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3b/" +
            "Postal_Vista_A%C3%A9rea_Capitolio_Nacional_-_Bogot%C3%A1.jpg/960px-" +
            "Postal_Vista_A%C3%A9rea_Capitolio_Nacional_-_Bogot%C3%A1.jpg",
        historicalPhotoYear = "1930s",
    )

    val catedralPrimada = SiteBrief(
        id = "ffffffff-ffff-ffff-ffff-ffffffffffff",
        code = "bogota-catedral-primada",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.5978421,
        lng = -74.0751469,
        geofenceRadiusMeters = 50,
        titleEs = "Catedral Primada de Bogotá",
        titleEn = "Bogotá Primate Cathedral",
        narrativeEs = "La catedral más grande de Colombia, de fachada neoclásica, se " +
            "levanta en la Plaza de Bolívar. En su interior descansan los restos del " +
            "fundador de Bogotá, Gonzalo Jiménez de Quesada.",
        narrativeEn = "Colombia's largest cathedral, with a neoclassical façade, stands " +
            "on Plaza de Bolívar. Inside rest the remains of Bogotá's founder, Gonzalo " +
            "Jiménez de Quesada.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/" +
            "2021_Bogot%C3%A1_-_Catedral_Primada_de_Colombia.jpg/960px-" +
            "2021_Bogot%C3%A1_-_Catedral_Primada_de_Colombia.jpg",
        xpReward = 140,
        badge = BadgeBrief(
            code = "catedral-primada",
            nameEs = "Guardián de Jiménez de Quesada",
            nameEn = "Keeper of Jiménez de Quesada",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 10,
        albumPage = AlbumPage.BOGOTA_COLONIAL,
        funFactEs = "Bajo su nave reposan los restos del fundador de la ciudad — llevas " +
            "más de 480 años de historia bogotana pisando el mismo lugar.",
        funFactEn = "Beneath its nave lie the remains of the city's founder — you'd be " +
            "standing on the same spot as over 480 years of Bogotá history.",
    )

    val monserrate = SiteBrief(
        id = "22222222-2222-2222-2222-222222222222",
        code = "bogota-monserrate",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6072539,
        lng = -74.0543090,
        geofenceRadiusMeters = 80,
        titleEs = "Monserrate",
        titleEn = "Monserrate",
        narrativeEs = "Desde el Cerro de Monserrate, a más de 3.100 m de altura, se " +
            "domina una vista panorámica de toda Bogotá. Se llega por un sendero " +
            "empedrado o en teleférico/funicular; en la cima está el santuario del " +
            "\"Señor Caído\", destino de peregrinación.",
        narrativeEn = "From Cerro de Monserrate, over 3,100 m above sea level, you get " +
            "a sweeping panoramic view of Bogotá. Reach the summit on foot via a stone " +
            "path or by cable car/funicular; at the top stands the sanctuary of the " +
            "venerated \"Fallen Christ,\" a major pilgrimage site.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/" +
            "Monserrate_%28Bogot%C3%A1%29.jpg/960px-Monserrate_%28Bogot%C3%A1%29.jpg",
        xpReward = 200,
        badge = BadgeBrief(
            code = "monserrate",
            nameEs = "Peregrino de las Alturas",
            nameEn = "Pilgrim of the Heights",
            rarity = BadgeRarity.LEGENDARY,
        ),
        laminaNumber = 11,
        albumPage = AlbumPage.MIRADORES_Y_NATURALEZA,
        funFactEs = "Cada Jueves y Viernes Santo, cientos de fieles suben a pie por " +
            "devoción, algunos descalzos o de rodillas en el último tramo.",
        funFactEn = "On Holy Thursday and Good Friday, hundreds of pilgrims climb on " +
            "foot as an act of faith — some barefoot or on their knees for the final stretch.",
    )

    val torreColpatria = SiteBrief(
        id = "33333333-3333-3333-3333-333333333333",
        code = "bogota-torre-colpatria",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6109936,
        lng = -74.0702477,
        geofenceRadiusMeters = 50,
        titleEs = "Torre Colpatria",
        titleEn = "Colpatria Tower",
        narrativeEs = "Uno de los edificios más altos de Bogotá, con un mirador desde " +
            "donde se aprecia la ciudad en 360 grados, incluyendo los cerros orientales " +
            "y la sabana.",
        narrativeEn = "One of Bogotá's tallest buildings, with an observation deck " +
            "offering 360-degree views of the city, including the eastern hills and the " +
            "surrounding savanna.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/32/" +
            "2021_Bogot%C3%A1_-_Torre_Colpatria.jpg/960px-2021_Bogot%C3%A1_-_Torre_Colpatria.jpg",
        xpReward = 140,
        badge = BadgeBrief(
            code = "torre-colpatria",
            nameEs = "Vigía de la Sabana",
            nameEn = "Watcher of the Savanna",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 12,
        albumPage = AlbumPage.MIRADORES_Y_NATURALEZA,
        funFactEs = "En diciembre, sus luces se encienden formando la silueta de un " +
            "gigantesco árbol de Navidad visible desde gran parte de la ciudad.",
        funFactEn = "In December, the tower's lights form the silhouette of a giant " +
            "Christmas tree, visible from much of the city.",
    )

    val jardinBotanico = SiteBrief(
        id = "77777777-7777-7777-7777-777777777777",
        code = "bogota-jardin-botanico",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6678953,
        lng = -74.1000858,
        geofenceRadiusMeters = 60,
        titleEs = "Jardín Botánico José Celestino Mutis",
        titleEn = "José Celestino Mutis Botanical Garden",
        narrativeEs = "Un extenso jardín botánico con colecciones de flora nativa " +
            "colombiana, de páramo y bosque andino, además del Tropicario, un " +
            "invernadero que recrea distintos ecosistemas tropicales del país.",
        narrativeEn = "A sprawling botanical garden with collections of native " +
            "Colombian flora, from páramo to Andean forest, plus the Tropicario, a " +
            "greenhouse recreating the country's tropical ecosystems.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cf/" +
            "Tropicario_-_Jard%C3%ADn_Bot%C3%A1nico_de_Bogot%C3%A1_Jos%C3%A9_Celestino_Mutis.jpg/960px-" +
            "Tropicario_-_Jard%C3%ADn_Bot%C3%A1nico_de_Bogot%C3%A1_Jos%C3%A9_Celestino_Mutis.jpg",
        xpReward = 130,
        badge = BadgeBrief(
            code = "jardin-botanico",
            nameEs = "Guardián del Páramo",
            nameEn = "Guardian of the Páramo",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 13,
        albumPage = AlbumPage.MIRADORES_Y_NATURALEZA,
        funFactEs = "Lleva el nombre de José Celestino Mutis, quien lideró la Real " +
            "Expedición Botánica del Nuevo Reino de Granada en la época colonial.",
        funFactEn = "It's named after José Celestino Mutis, who led the Royal Botanical " +
            "Expedition of the New Kingdom of Granada during colonial times.",
    )

    val planetario = SiteBrief(
        id = "88888888-8888-8888-8888-888888888888",
        code = "bogota-planetario",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6120576,
        lng = -74.0688218,
        geofenceRadiusMeters = 40,
        titleEs = "Planetario de Bogotá",
        titleEn = "Bogotá Planetarium",
        narrativeEs = "Ubicado en el Parque de la Independencia, este centro de " +
            "divulgación científica ofrece proyecciones bajo su icónica cúpula sobre " +
            "astronomía y el cosmos, junto con salas interactivas de ciencia.",
        narrativeEn = "Located in Parque de la Independencia, this science center " +
            "offers dome projection shows about astronomy and the cosmos, along with " +
            "interactive science exhibition halls.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/" +
            "Planetario_de_Bogot%C3%A1.JPG/960px-Planetario_de_Bogot%C3%A1.JPG",
        xpReward = 100,
        badge = BadgeBrief(
            code = "planetario-bogota",
            nameEs = "Explorador del Cosmos",
            nameEn = "Explorer of the Cosmos",
            rarity = BadgeRarity.COMMON,
        ),
        laminaNumber = 14,
        albumPage = AlbumPage.BOGOTA_MODERNA,
        funFactEs = "Su cúpula es uno de los edificios más reconocibles del centro de " +
            "Bogotá, visible desde varias calles del sector.",
        funFactEn = "Its dome is one of the most recognizable buildings in downtown " +
            "Bogotá, visible from several streets in the area.",
    )

    val bibliotecaVirgilioBarco = SiteBrief(
        id = "99999999-9999-9999-9999-999999999999",
        code = "bogota-biblioteca-virgilio-barco",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6569529,
        lng = -74.0882974,
        geofenceRadiusMeters = 50,
        titleEs = "Biblioteca Virgilio Barco",
        titleEn = "Virgilio Barco Library",
        narrativeEs = "Diseñada por el arquitecto Rogelio Salmona, esta biblioteca " +
            "pública se integra con un gran parque, destacando por sus muros curvos de " +
            "ladrillo, terrazas y estanques que combinan arquitectura y naturaleza.",
        narrativeEn = "Designed by architect Rogelio Salmona, this public library is " +
            "integrated with a large park, notable for its curved brick walls, " +
            "terraces, and reflecting pools blending architecture and nature.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/" +
            "Biblioteca_P%C3%BAblica_Virgilio_Barco.JPG/960px-Biblioteca_P%C3%BAblica_Virgilio_Barco.JPG",
        xpReward = 120,
        badge = BadgeBrief(
            code = "biblioteca-virgilio-barco",
            nameEs = "Lector de Ladrillo y Agua",
            nameEn = "Reader of Brick and Water",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 15,
        albumPage = AlbumPage.BOGOTA_MODERNA,
        funFactEs = "Su arquitecto, Rogelio Salmona, es uno de los colombianos más " +
            "reconocidos internacionalmente en su oficio, ganador de la Medalla Alvar Aalto.",
        funFactEn = "Its architect, Rogelio Salmona, is one of Colombia's most " +
            "internationally recognized professionals in his field, winner of the Alvar Aalto Medal.",
    )

    val parqueDeUsaquen = SiteBrief(
        id = "06060606-0606-0606-0606-060606060606",
        code = "bogota-parque-usaquen",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6951704,
        lng = -74.0309408,
        geofenceRadiusMeters = 60,
        titleEs = "Parque de Usaquén",
        titleEn = "Usaquén Park",
        narrativeEs = "Esta plaza es el corazón del antiguo pueblo de Usaquén, con " +
            "calles empedradas, casas coloniales y una iglesia. Los fines de semana se " +
            "llena con el tradicional mercado de las pulgas: artesanías, comida y arte.",
        narrativeEn = "This plaza is the heart of the former town of Usaquén, with " +
            "cobblestone streets, colonial houses, and a church. On weekends it fills " +
            "with the traditional flea market: handicrafts, food, and art.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/" +
            "Mercado_de_Pulgas%2C_Usaqu%C3%A9n%2C_Bogot%C3%A1%2C_Colombia.jpg/960px-" +
            "Mercado_de_Pulgas%2C_Usaqu%C3%A9n%2C_Bogot%C3%A1%2C_Colombia.jpg",
        xpReward = 110,
        badge = BadgeBrief(
            code = "parque-usaquen",
            nameEs = "Cazador de Pulgas",
            nameEn = "Flea Market Hunter",
            rarity = BadgeRarity.COMMON,
        ),
        laminaNumber = 16,
        albumPage = AlbumPage.PARQUES_Y_BARRIOS,
        funFactEs = "Usaquén fue un municipio independiente hasta 1954, cuando fue " +
            "anexado a Bogotá junto con otros pueblos vecinos.",
        funFactEn = "Usaquén was its own independent municipality until 1954, when it " +
            "was annexed into Bogotá along with several neighboring towns.",
    )

    val parqueDeLa93 = SiteBrief(
        id = "12121212-1212-1212-1212-121212121212",
        code = "bogota-parque-93",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6767680,
        lng = -74.0482874,
        geofenceRadiusMeters = 50,
        titleEs = "Parque de la 93",
        titleEn = "Parque de la 93",
        narrativeEs = "Un parque urbano rodeado de restaurantes, cafés y oficinas en " +
            "pleno Chapinero, popular como punto de encuentro y para comer al aire " +
            "libre en una de las zonas más exclusivas de la ciudad.",
        narrativeEn = "An urban park surrounded by restaurants, cafés, and office " +
            "buildings in Chapinero, popular as a meeting spot and for outdoor dining " +
            "in one of the city's most upscale areas.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/" +
            "Parque_de_la_93_en_Bogot%C3%A1.jpg/960px-Parque_de_la_93_en_Bogot%C3%A1.jpg",
        xpReward = 90,
        badge = BadgeBrief(
            code = "parque-93",
            nameEs = "Sobremesa de Chapinero",
            nameEn = "Chapinero Regular",
            rarity = BadgeRarity.COMMON,
        ),
        laminaNumber = 17,
        albumPage = AlbumPage.PARQUES_Y_BARRIOS,
        funFactEs = "Está rodeado por algunas de las zonas comerciales y de oficinas " +
            "más costosas de Bogotá.",
        funFactEn = "It's surrounded by some of the most expensive commercial and " +
            "office real estate in Bogotá.",
    )

    val parqueSimonBolivar = SiteBrief(
        id = "34343434-3434-3434-3434-343434343434",
        code = "bogota-parque-simon-bolivar",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6587774,
        lng = -74.0940971,
        geofenceRadiusMeters = 80,
        titleEs = "Parque Simón Bolívar",
        titleEn = "Simón Bolívar Park",
        narrativeEs = "El parque metropolitano más grande de Bogotá, con lagos, " +
            "senderos, canchas deportivas y grandes zonas verdes. Es sede habitual de " +
            "festivales masivos como Rock al Parque.",
        narrativeEn = "Bogotá's largest metropolitan park, with lakes, trails, sports " +
            "fields, and large green spaces. It regularly hosts massive festivals such " +
            "as Rock al Parque.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/8f/" +
            "Bogot%C3%A1_Parque_Sim%C3%B3n_Bol%C3%ADvar.JPG/960px-" +
            "Bogot%C3%A1_Parque_Sim%C3%B3n_Bol%C3%ADvar.JPG",
        xpReward = 130,
        badge = BadgeBrief(
            code = "parque-simon-bolivar",
            nameEs = "Corredor del Parque Grande",
            nameEn = "Runner of the Big Park",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 18,
        albumPage = AlbumPage.PARQUES_Y_BARRIOS,
        funFactEs = "Con su extensión, suele mencionarse como más grande que el " +
            "Central Park de Nueva York.",
        funFactEn = "Given its size, it's often described as bigger than New York's " +
            "Central Park.",
    )

    val all: List<SiteBrief> = listOf(
        plazaDeBolivar, chorroDeQuevedo, museoDelOro,
        baluarteSanFrancisco, puertaDelReloj, iglesiaSantoDomingo, lasBovedas,
        quintaDeBolivar, capitolioNacional, catedralPrimada, monserrate,
        torreColpatria, jardinBotanico, planetario, bibliotecaVirgilioBarco,
        parqueDeUsaquen, parqueDeLa93, parqueSimonBolivar,
    ).sortedBy { it.laminaNumber }
}
