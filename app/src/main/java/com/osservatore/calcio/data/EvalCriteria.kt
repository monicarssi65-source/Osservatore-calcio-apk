package com.osservatore.calcio.data

object EvalCriteria {
    val TECNICA = listOf(
        "Uso di entrambi i piedi",
        "Calcio della palla",
        "Colpo di testa",
        "Ricezione – controllo della palla",
        "Guida della palla in velocità"
    )
    val TATTICA = listOf(
        "Dribbling e finte",
        "Passaggio",
        "Tiro",
        "Contrasto",
        "Difesa/copertura palla",
        "Lettura della gara",
        "Fase di attacco",
        "Fase di difesa"
    )
    val COORDINAZIONE = listOf(
        "Orientamento spazio-tempo",
        "Ritmo",
        "Reazione",
        "Fantasia"
    )
    val FISICHE = listOf(
        "Forza (esplosività)",
        "Velocità",
        "Rapidità",
        "Resistenza generale",
        "Mobilità"
    )
    val MENTALI = listOf(
        "Concentrazione",
        "Forza di volontà",
        "Autostima",
        "Accettazione del rischio e della responsabilità"
    )
    val SOCIAL = listOf(
        "Comunicazione",
        "Comportamento",
        "Carisma/personalità",
        "Capacità di organizzarsi",
        "Spirito di squadra"
    )

    val RUOLI = listOf("", "Portiere", "Difensore centrale", "Terzino destro", "Terzino sinistro",
        "Mediano", "Mezzala", "Trequartista", "Ala destra", "Ala sinistra",
        "Prima punta", "Seconda punta", "Centrocampista")

    val CAMPIONATI = listOf("Serie A", "Serie B", "Serie C", "Serie D",
        "Eccellenza", "Promozione", "Prima Categoria", "Seconda Categoria",
        "Terza Categoria", "Primavera", "Under 18", "Under 17", "Under 16", "Under 15")

    val MODULI = listOf("4-4-2", "4-3-3", "3-5-2", "4-2-3-1", "3-4-3",
        "5-3-2", "4-5-1", "3-4-1-2", "4-1-4-1", "4-3-1-2")

    val NAZIONALITA = mapOf(
        "IT" to "🇮🇹 Italia", "BR" to "🇧🇷 Brasile", "AR" to "🇦🇷 Argentina",
        "FR" to "🇫🇷 Francia", "ES" to "🇪🇸 Spagna", "DE" to "🇩🇪 Germania",
        "PT" to "🇵🇹 Portogallo", "NL" to "🇳🇱 Olanda", "BE" to "🇧🇪 Belgio",
        "HR" to "🇭🇷 Croazia", "SN" to "🇸🇳 Senegal", "NG" to "🇳🇬 Nigeria",
        "GH" to "🇬🇭 Ghana", "MA" to "🇲🇦 Marocco", "AL" to "🇦🇱 Albania",
        "RS" to "🇷🇸 Serbia", "RO" to "🇷🇴 Romania", "OTHER" to "Altro"
    )
}
