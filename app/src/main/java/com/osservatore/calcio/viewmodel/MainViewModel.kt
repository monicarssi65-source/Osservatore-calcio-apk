package com.osservatore.calcio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.osservatore.calcio.data.*
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.get(app)
    val players = db.playerDao().getAll()
    val teams = db.teamDao().getAll()
    val matches = db.matchDao().getAll()

    fun savePlayer(p: Player) = viewModelScope.launch { db.playerDao().insert(p) }
    fun deletePlayer(p: Player) = viewModelScope.launch { db.playerDao().delete(p) }
    fun saveTeam(t: Team) = viewModelScope.launch { db.teamDao().insert(t) }
    fun deleteTeam(t: Team) = viewModelScope.launch { db.teamDao().delete(t) }
    fun saveMatch(m: Match) = viewModelScope.launch { db.matchDao().insert(m) }
    fun deleteMatch(m: Match) = viewModelScope.launch { db.matchDao().delete(m) }

    suspend fun getAllPlayers() = db.playerDao().getAllSync()
    suspend fun getAllTeams() = db.teamDao().getAllSync()
    suspend fun getAllMatches() = db.matchDao().getAllSync()

    fun deleteAllPlayers() = viewModelScope.launch { db.playerDao().deleteAll() }
    fun deleteAllTeams() = viewModelScope.launch { db.teamDao().deleteAll() }
    fun deleteAllMatches() = viewModelScope.launch { db.matchDao().deleteAll() }
}
