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
 * Dos grupos, ambos con coordenadas reales:
 *  - 4 hitos de la ciudad amurallada de Cartagena. Los 3 que no son el
 *    Baluarte tienen coordenadas ILUSTRATIVAS (ajustadas a mano para caer a
 *    ~100/300/500m exactos del Baluarte, como se pidió explícitamente
 *    "puntos simulados") — no son un levantamiento topográfico real.
 *  - 3 hitos de La Candelaria, Bogotá, con coordenadas verificadas contra
 *    el geocodificador Nominatim de OpenStreetMap (no estimadas de memoria).
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

    val all: List<SiteBrief> = listOf(
        plazaDeBolivar, chorroDeQuevedo, museoDelOro,
        baluarteSanFrancisco, puertaDelReloj, iglesiaSantoDomingo, lasBovedas,
    ).sortedBy { it.laminaNumber }
}
