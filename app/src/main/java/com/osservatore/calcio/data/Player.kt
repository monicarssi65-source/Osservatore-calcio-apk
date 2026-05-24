package com.osservatore.calcio.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey val id: String,
    val createdAt: Long = System.currentTimeMillis(),
    // Anagrafica
    val cognome: String,
    val nome: String,
    val dataNascita: String = "",
    val luogoNascita: String = "",
    val nazionalita: String = "IT",
    val ruolo: String = "",
    val piede: String = "destro",
    val stato: String = "svincolato",
    val societa: String = "",
    val luogoRilevazione: String = "",
    val altezza: String = "",
    val peso: String = "",
    val morfologia: String = "normotipo",
    val stagione: String = "2024/2025",
    val presenze: Int = 0,
    val gol: Int = 0,
    val assist: Int = 0,
    val minuti: Int = 0,
    val note: String = "",
    val tags: String = "", // comma separated
    // Infortunio
    val infortunioTipo: String = "",
    val infortunioGravita: String = "nessuno",
    val infortunioGiorni: Int = 0,
    // Punteggi (media 1-4)
    val scoreTecnica: Float = 3f,
    val scoreTattica: Float = 3f,
    val scoreCoordinazione: Float = 3f,
    val scoreFisiche: Float = 3f,
    val scoreMentali: Float = 3f,
    val scoreSocialita: Float = 3f,
    val scoreComplessivo: Float = 3f,
    // Valori radio serializzati come JSON string
    val radioValues: String = "{}"
)
