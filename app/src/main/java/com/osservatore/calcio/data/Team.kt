package com.osservatore.calcio.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class Team(
    @PrimaryKey val id: String,
    val createdAt: Long = System.currentTimeMillis(),
    val nome: String,
    val campionato: String = "Serie D",
    val allenatore: String = "",
    val modulo: String = "4-4-2",
    val tipoGioco: String = "Pressing alto",
    val passaggi: String = "Corti",
    val difesa: String = "4 in linea",
    val attacco: String = "2 punte",
    val forza: String = "",
    val criticita: String = "",
    val note: String = "",
    val formazione: String = "" // JSON
)
