package com.osservatore.calcio.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class Match(
    @PrimaryKey val id: String,
    val createdAt: Long = System.currentTimeMillis(),
    val data: String = "",
    val casa: String = "",
    val ospite: String = "",
    val risultato: String = "",
    val competizione: String = "",
    val stadio: String = "",
    val arbitro: String = "",
    val allCasa: String = "",
    val allOspite: String = "",
    val angCasa: Int = 0,
    val angOspite: Int = 0,
    val cronaca: String = "",
    val marcatori: String = "", // JSON array
    val sostituzioni: String = "", // JSON array
    val formazioneCasa: String = "", // JSON
    val formazioneOspite: String = "" // JSON
)
