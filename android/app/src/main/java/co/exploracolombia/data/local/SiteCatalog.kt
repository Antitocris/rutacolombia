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
 * Pivote de contenido (2026-07-18): se quitaron los 3 sitios que se
 * sintieron genéricos/turísticos-de-manual (Parque de Usaquén, Parque de la
 * 93, Parque Simón Bolívar — cualquiera los encuentra en Google Maps sin
 * ayuda) y se agregaron 6 misiones de "Bogotá Oculta y Secreta" (leyendas
 * urbanas REALMENTE documentadas — prensa, Wikipedia, tradición oral
 * registrada — nunca inventadas, presentadas siempre como "cuenta la
 * leyenda..." nunca como hecho histórico). 21 hitos en total. TODOS los
 * sitios, no solo los nuevos, ahora tienen `missionTitleEs`/`missionTitleEn`
 * (ver SiteBrief.kt) — el nombre técnico (`titleEs`/`titleEn`) sigue
 * existiendo pero ya no es lo primero que el jugador ve.
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
        missionTitleEs = "El Bastión que Vigiló la Bahía",
        missionTitleEn = "The Bastion that Watched the Bay",
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
        missionTitleEs = "El Umbral del Tiempo Detenido",
        missionTitleEn = "The Threshold of Stopped Time",
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
        missionTitleEs = "La Torre que Torció el Diablo",
        missionTitleEn = "The Tower the Devil Twisted",
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
        missionTitleEs = "Los Calabozos del Contrabando",
        missionTitleEn = "The Smugglers' Dungeons",
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
        missionTitleEs = "El Corazón de la República",
        missionTitleEn = "Heart of the Republic",
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
        missionTitleEs = "El Espectro del Duelo Final",
        missionTitleEn = "The Ghost of the Final Duel",
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
        funFactEs = "La tradición oral de La Candelaria cuenta que aquí ronda el " +
            "espíritu de un joven muerto en un duelo de honor — dicen que todavía " +
            "se le siente rondar la plaza cuando cae la noche.",
        funFactEn = "La Candelaria's oral tradition says the spirit of a young man " +
            "killed in a duel of honor still roams here — some say you can feel him " +
            "in the plaza after dark.",
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
        missionTitleEs = "El Tesoro Escondido de El Dorado",
        missionTitleEn = "The Hidden Treasure of El Dorado",
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
        missionTitleEs = "El Refugio Secreto del Libertador",
        missionTitleEn = "The Liberator's Secret Retreat",
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
        missionTitleEs = "El Palacio que Tardó un Siglo",
        missionTitleEn = "The Palace a Century in the Making",
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
        missionTitleEs = "La Cripta del Fundador",
        missionTitleEn = "The Founder's Crypt",
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
        missionTitleEs = "La Cumbre del Peregrino",
        missionTitleEn = "The Pilgrim's Summit",
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
        missionTitleEs = "El Vigía de Cristal de la Sabana",
        missionTitleEn = "The Glass Watchtower of the Savanna",
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
        missionTitleEs = "El Bosque Encantado de Mutis",
        missionTitleEn = "Mutis' Enchanted Forest",
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
        missionTitleEs = "La Cúpula de las Estrellas Perdidas",
        missionTitleEn = "The Dome of the Lost Stars",
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
        missionTitleEs = "El Laberinto de Ladrillo y Agua",
        missionTitleEn = "The Labyrinth of Brick and Water",
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

    // ------------------------------------------------------------------
    // Bogotá Oculta y Secreta — pivote de contenido (2026-07-18): leyendas
    // urbanas REALMENTE documentadas (prensa, Wikipedia, tradición oral
    // registrada), nunca inventadas. Donde la premisa original pedida no
    // coincidía con lo que de verdad está documentado, se corrigió en vez
    // de forzarla:
    //  - "La Casita del Parque de la Candelaria" no se pudo verificar como
    //    tal → se usa en su lugar la Casa del Bandido (Raimundo Russi),
    //    leyenda real con dirección real.
    //  - "Tumba del Astrólogo" → Julio Garavito Armero fue astrónomo, no
    //    astrólogo (confusión popular); se corrige el nombre.
    //  - "Piedra del Muerto en Suba" → la piedra real está en Ciudad
    //    Bolívar, no en Suba; se corrige la localidad.
    //  - "Castillo Marroquín" es real pero queda en Chía, Cundinamarca, no
    //    en Bogotá — se deja el campo `city` honesto en vez de forzarlo.
    // ------------------------------------------------------------------

    /** Coordenada aproximada sobre la cuadra correcta de Carrera 2 (verificada vía Nominatim), no el lote exacto. */
    val casaDelBandido = SiteBrief(
        id = "56565656-5656-5656-5656-565656565656",
        code = "bogota-casa-del-bandido",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.5953811,
        lng = -74.0709319,
        geofenceRadiusMeters = 60,
        titleEs = "Casa del Bandido",
        titleEn = "House of the Bandit",
        missionTitleEs = "La Casa del Bandido",
        missionTitleEn = "House of the Bandit",
        narrativeEs = "En esta cuadra de La Candelaria, la tradición oral ubica la " +
            "casa donde habría rondado el espíritu del abogado Raimundo Russi, " +
            "fusilado en 1851 acusado de liderar una banda criminal. Vecinos " +
            "antiguos contaban que se persignaban al pasar frente a la casa.",
        narrativeEn = "On this block of La Candelaria, oral tradition places the " +
            "house said to be haunted by the spirit of lawyer Raimundo Russi, " +
            "executed by firing squad in 1851 for allegedly leading a criminal gang. " +
            "Longtime neighbors reportedly crossed themselves walking past it.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/" +
            "La_Candelaria_-_Casa_colonial_%28Bogot%C3%A1%29_01.JPG/960px-" +
            "La_Candelaria_-_Casa_colonial_%28Bogot%C3%A1%29_01.JPG",
        xpReward = 160,
        badge = BadgeBrief(
            code = "casa-del-bandido",
            nameEs = "Testigo del Bandido",
            nameEn = "Witness of the Bandit",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 16,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        funFactEs = "Cuentan que al amanecer se oían pasos y gritos saliendo de la " +
            "casa — nadie se quedaba a comprobarlo dos veces.",
        funFactEn = "Locals say cries and footsteps could be heard from the house at " +
            "dawn — nobody stuck around to check twice.",
    )

    val hospitalSanJuanDeDios = SiteBrief(
        id = "78787878-7878-7878-7878-787878787878",
        code = "bogota-hospital-san-juan-de-dios",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.5896891,
        lng = -74.0877050,
        geofenceRadiusMeters = 70,
        titleEs = "Hospital San Juan de Dios",
        titleEn = "San Juan de Dios Hospital",
        missionTitleEs = "Los Túneles del Hospital Abandonado",
        missionTitleEn = "The Abandoned Hospital's Tunnels",
        narrativeEs = "Fundado en 1564 y trasladado a este lugar en 1925, el hospital " +
            "cerró en 2001 tras 458 años de servicio. Hoy está en restauración: un " +
            "túnel real conecta la torre central con el antiguo Instituto Materno " +
            "Infantil. Es zona de obra activa — se admira desde fuera, no se entra.",
        narrativeEn = "Founded in 1564 and moved here in 1925, the hospital closed in " +
            "2001 after 458 years of service. It's under restoration today: a real " +
            "tunnel connects the central tower to the former maternity institute. " +
            "It's an active construction zone — admire it from outside, don't enter.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/04/" +
            "Bogot%C3%A1%2C_Hospital_San_Juan_de_Dios%2C_carrera_D%C3%A9cima.JPG/960px-" +
            "Bogot%C3%A1%2C_Hospital_San_Juan_de_Dios%2C_carrera_D%C3%A9cima.JPG",
        xpReward = 180,
        badge = BadgeBrief(
            code = "hospital-san-juan-de-dios",
            nameEs = "Cronista del Hospital Olvidado",
            nameEn = "Chronicler of the Forgotten Hospital",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 17,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        funFactEs = "La tradición oral habla de una monja fantasma que repartía " +
            "medicinas mientras las enfermeras caían en un sueño extrañamente profundo.",
        funFactEn = "Oral tradition tells of a ghost nun who handed out medicine while " +
            "the nurses fell into a strangely deep sleep.",
    )

    val tumbaDelAstronomo = SiteBrief(
        id = "9c9c9c9c-9c9c-9c9c-9c9c-9c9c9c9c9c9c",
        code = "bogota-tumba-del-astronomo",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6174450,
        lng = -74.0758808,
        geofenceRadiusMeters = 120,
        titleEs = "Cementerio Central — Tumba de Julio Garavito",
        titleEn = "Central Cemetery — Julio Garavito's Tomb",
        missionTitleEs = "La Tumba del Astrónomo",
        missionTitleEn = "The Astronomer's Tomb",
        narrativeEs = "Aquí descansa Julio Garavito Armero (1865-1920), matemático, " +
            "ingeniero y astrónomo colombiano — no astrólogo, como suele decirse — " +
            "cuyo rostro aparece en el billete de 20.000 pesos y que tiene un " +
            "cráter lunar con su nombre. Su tumba recibe visitas constantes de " +
            "quienes le piden prosperidad económica golpeando la lápida.",
        narrativeEn = "Here lies Julio Garavito Armero (1865-1920), Colombian " +
            "mathematician, engineer and astronomer — not astrologer, as popular " +
            "speech often has it — whose face appears on the 20,000-peso bill and " +
            "who has a lunar crater named after him. His tomb draws constant visits " +
            "from people knocking on the stone asking for financial prosperity.",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/68/" +
            "Tumba_Julio_Garavito.jpg/960px-Tumba_Julio_Garavito.jpg",
        xpReward = 170,
        badge = BadgeBrief(
            code = "tumba-del-astronomo",
            nameEs = "Devoto del Billete de 20 Mil",
            nameEn = "Devotee of the 20,000 Bill",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 18,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        funFactEs = "Le dicen \"el santo del billete de 20 mil\": tanta gente golpea " +
            "su lápida pidiendo plata que la piedra ya muestra el desgaste.",
        funFactEn = "He's nicknamed \"the saint of the 20,000 bill\": so many people " +
            "knock on his tombstone asking for money that the stone shows the wear.",
    )

    val antiguoBronx = SiteBrief(
        id = "bdbdbdbd-bdbd-bdbd-bdbd-bdbdbdbdbdbd",
        code = "bogota-antiguo-bronx",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.6011126,
        lng = -74.0827716,
        geofenceRadiusMeters = 80,
        titleEs = "Bronx Distrito Creativo",
        titleEn = "Bronx Creative District",
        missionTitleEs = "El Renacer del Antiguo Bronx",
        missionTitleEn = "The Rebirth of the Old Bronx",
        narrativeEs = "El 28 de mayo de 2016, la fuerza pública intervino este sector, " +
            "entonces conocido como \"El Bronx\", rescatando a 149 menores. Desde " +
            "entonces la ciudad construyó aquí el Bronx Distrito Creativo: un " +
            "proyecto de renovación urbana centrado en cultura y memoria, ganador " +
            "del premio \"Retrofit of the Year\" 2025 en los GRI Awards.",
        narrativeEn = "On May 28, 2016, police intervened in this area, then known as " +
            "\"El Bronx,\" rescuing 149 minors. Since then the city has built the " +
            "Bronx Distrito Creativo here: an urban renewal project centered on " +
            "culture and memory, winner of the 2025 GRI Awards \"Retrofit of the Year.\"",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/" +
            "Bogot%C3%A1%2C_calle_10_carrera_15_El_Bronx.JPG/960px-" +
            "Bogot%C3%A1%2C_calle_10_carrera_15_El_Bronx.JPG",
        xpReward = 150,
        badge = BadgeBrief(
            code = "antiguo-bronx",
            nameEs = "Testigo de la Transformación",
            nameEn = "Witness to the Transformation",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 19,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        // A propósito, sin dato "curioso" con humor: es la historia más
        // sensible del catálogo (vidas reales, explotación real). El
        // dato aquí es cívico, no una anécdota para reírse.
        funFactEs = "La renovación de casi 35.000 m² se hizo con más de $222.000 " +
            "millones de inversión pública — hoy el foco es cultura y memoria, no " +
            "su pasado.",
        funFactEn = "The nearly 35,000 m² renewal involved over 222 billion pesos in " +
            "public investment — today the focus is culture and memory, not its past.",
    )

    val castilloMarroquin = SiteBrief(
        id = "cececece-cece-cece-cece-cececececece",
        code = "chia-castillo-marroquin",
        department = "Cundinamarca",
        city = "Chía",
        lat = 4.862575,
        lng = -74.02590833,
        geofenceRadiusMeters = 100,
        titleEs = "Castillo Marroquín",
        titleEn = "Marroquín Castle",
        missionTitleEs = "El Castillo de las Apariciones",
        missionTitleEn = "The Castle of Apparitions",
        // No es técnicamente Bogotá (queda en Chía, ~20km al norte) — se
        // deja así, honesto, en vez de forzarlo dentro de los límites de la
        // ciudad como pedía la premisa original.
        narrativeEs = "Construido en 1898 por el arquitecto francés Gastón Lelarge, " +
            "este castillo en Chía (a las afueras de Bogotá) fue cabaret, hospital " +
            "psiquiátrico para mujeres y residencia privada antes de convertirse en " +
            "centro de eventos y patrimonio cultural. La tradición local le atribuye " +
            "varias apariciones, entre ellas una \"monja errante\".",
        narrativeEn = "Built in 1898 by French architect Gastón Lelarge, this castle " +
            "in Chía (just outside Bogotá) served as a cabaret, a women's psychiatric " +
            "hospital, and a private residence before becoming an events venue and " +
            "heritage site. Local tradition attributes several ghost sightings to it, " +
            "including a \"wandering nun.\"",
        coverImageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e1/" +
            "CASTILLO_MARROQUIN%2C_sobre_la_autopista_norte%2C_Ch%C3%ADa%2C_Cundinamarca.JPG/960px-" +
            "CASTILLO_MARROQUIN%2C_sobre_la_autopista_norte%2C_Ch%C3%ADa%2C_Cundinamarca.JPG",
        xpReward = 190,
        badge = BadgeBrief(
            code = "castillo-marroquin",
            nameEs = "Huésped de la Monja Errante",
            nameEn = "Guest of the Wandering Nun",
            rarity = BadgeRarity.EPIC,
        ),
        laminaNumber = 20,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        funFactEs = "Antes de ser salón de eventos, fue hospital psiquiátrico — dicen " +
            "que algunas internas nunca se fueron del todo.",
        funFactEn = "Before it was an events venue, it was a psychiatric hospital — " +
            "some say a few patients never quite left.",
    )

    /** Coordenada aproximada (barrio Vista Hermosa) — la piedra en sí no tiene dirección geocodificable. */
    val piedraDelMuerto = SiteBrief(
        id = "dfdfdfdf-dfdf-dfdf-dfdf-dfdfdfdfdfdf",
        code = "bogota-piedra-del-muerto",
        department = "Cundinamarca",
        city = "Bogotá",
        lat = 4.5522765,
        lng = -74.1444603,
        geofenceRadiusMeters = 150,
        titleEs = "La Piedra del Muerto",
        titleEn = "The Dead Man's Stone",
        missionTitleEs = "La Piedra del Muerto de Ciudad Bolívar",
        missionTitleEn = "The Dead Man's Stone of Ciudad Bolívar",
        narrativeEs = "Entre el barrio Capri y el sector de Vista Hermosa, en Ciudad " +
            "Bolívar, hay una roca de unos 4 metros con forma de hombre acostado. " +
            "Según el libro de Álvaro Cristancho Lozano, la leyenda cuenta que una " +
            "madre maldijo a su hijo perezoso y este quedó petrificado al caer por " +
            "la ladera.",
        narrativeEn = "Between the Capri neighborhood and the Vista Hermosa sector, " +
            "in Ciudad Bolívar, sits a roughly 4-meter rock shaped like a reclining " +
            "man. Per a book by Álvaro Cristancho Lozano, legend says a mother " +
            "cursed her lazy son and he was turned to stone falling down the hillside.",
        coverImageUrl = null,
        xpReward = 160,
        badge = BadgeBrief(
            code = "piedra-del-muerto",
            nameEs = "Testigo de la Maldición",
            nameEn = "Witness to the Curse",
            rarity = BadgeRarity.RARE,
        ),
        laminaNumber = 21,
        albumPage = AlbumPage.BOGOTA_OCULTA,
        funFactEs = "Dicen que la piedra sangra si la golpean, llora si la insultan, " +
            "y que sigue creciendo con los años.",
        funFactEn = "Locals say the stone bleeds if you hit it, cries if you insult " +
            "it, and keeps growing year after year.",
    )

    val all: List<SiteBrief> = listOf(
        plazaDeBolivar, chorroDeQuevedo, museoDelOro,
        baluarteSanFrancisco, puertaDelReloj, iglesiaSantoDomingo, lasBovedas,
        quintaDeBolivar, capitolioNacional, catedralPrimada, monserrate,
        torreColpatria, jardinBotanico, planetario, bibliotecaVirgilioBarco,
        casaDelBandido, hospitalSanJuanDeDios, tumbaDelAstronomo, antiguoBronx,
        castilloMarroquin, piedraDelMuerto,
    ).sortedBy { it.laminaNumber }
}
