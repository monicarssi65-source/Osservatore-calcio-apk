package com.osservatore.calcio.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(p: Player)
    @Delete suspend fun delete(p: Player)
    @Query("SELECT * FROM players ORDER BY createdAt DESC") fun getAll(): LiveData<List<Player>>
    @Query("SELECT * FROM players ORDER BY createdAt DESC") suspend fun getAllSync(): List<Player>
    @Query("SELECT * FROM players WHERE id = :id") suspend fun getById(id: String): Player?
    @Query("DELETE FROM players") suspend fun deleteAll()
}

@Dao
interface TeamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(t: Team)
    @Delete suspend fun delete(t: Team)
    @Query("SELECT * FROM teams ORDER BY createdAt DESC") fun getAll(): LiveData<List<Team>>
    @Query("SELECT * FROM teams ORDER BY createdAt DESC") suspend fun getAllSync(): List<Team>
    @Query("DELETE FROM teams") suspend fun deleteAll()
}

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(m: Match)
    @Delete suspend fun delete(m: Match)
    @Query("SELECT * FROM matches ORDER BY createdAt DESC") fun getAll(): LiveData<List<Match>>
    @Query("SELECT * FROM matches ORDER BY createdAt DESC") suspend fun getAllSync(): List<Match>
    @Query("DELETE FROM matches") suspend fun deleteAll()
}
