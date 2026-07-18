package co.exploracolombia.data.local

import co.exploracolombia.domain.model.BadgeBrief
import co.exploracolombia.domain.model.BadgeRarity
import co.exploracolombia.domain.model.SiteBrief

/**
 * Catálogo local de hitos "cercanos" para el mapa. Copia exacta del
 * contenido de supabase/seed.sql — cuando exista un endpoint real de
 * "sitios cercanos a mi ubicación", este objeto se reemplaza por una
 * llamada de red, pero la forma (SiteBrief) no debería cambiar.
 *
 * Los 4 son lugares reales de la ciudad amurallada de Cartagena, muy cerca
 * entre sí. Las coordenadas de los 3 nuevos son ILUSTRATIVAS (ajustadas a
 * mano para caer a ~100/300/500m exactos del Baluarte, como se pidió
 * explícitamente "puntos simulados") — no son un levantamiento topográfico
 * real; para producción real habría que verificarlas con datos reales de
 * cada sitio.
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
        coverImageUrl = null,
        xpReward = 100,
        badge = BadgeBrief(
            code = "murallas-cartagena",
            nameEs = "Guardián de las Murallas",
            nameEn = "Guardian of the Walls",
            rarity = BadgeRarity.RARE,
        ),
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
        coverImageUrl = null,
        xpReward = 75,
        badge = BadgeBrief(
            code = "puerta-del-reloj",
            nameEs = "Guardián del Reloj",
            nameEn = "Keeper of the Clock",
            rarity = BadgeRarity.COMMON,
        ),
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
        coverImageUrl = null,
        xpReward = 150,
        badge = BadgeBrief(
            code = "santo-domingo",
            nameEs = "Alma de Piedra",
            nameEn = "Soul of Stone",
            rarity = BadgeRarity.EPIC,
        ),
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
        coverImageUrl = null,
        xpReward = 200,
        badge = BadgeBrief(
            code = "las-bovedas",
            nameEs = "Centinela de las Bóvedas",
            nameEn = "Sentinel of the Vaults",
            rarity = BadgeRarity.LEGENDARY,
        ),
    )

    val all: List<SiteBrief> = listOf(baluarteSanFrancisco, puertaDelReloj, iglesiaSantoDomingo, lasBovedas)
}
