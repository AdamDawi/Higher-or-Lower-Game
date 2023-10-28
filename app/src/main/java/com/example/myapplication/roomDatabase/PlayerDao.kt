package com.example.myapplication.roomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Data Access Object
//for querying the database
@Dao
interface PlayerDao {

    @Insert
    suspend fun insert(playerEntity: PlayerEntity)

    @Update
    suspend fun update(playerEntity: PlayerEntity)

    @Delete
    suspend fun delete(playerEntity: PlayerEntity)

    @Query("Select * from `player-table`")
    //flow is used to hold values that can always change at runtime(live update)
    fun fetchAllPlayers(): Flow<List<PlayerEntity>>

    @Query("Select * from `player-table` where id=:id")
    fun fetchPlayerById(id:Int): Flow<PlayerEntity>
}