package com.example.myapplication.roomDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Insert
    suspend fun insert(employeeEntity: PlayerEntity)

    @Update
    suspend fun update(employeeEntity: PlayerEntity)

    @Delete
    suspend fun delete(employeeEntity: PlayerEntity)

    @Query("Select * from `player-table`")
    fun fetchAllEmployee(): Flow<List<PlayerEntity>>

    @Query("Select * from `player-table` where id=:id")
    fun fetchEmployeeById(id:Int): Flow<PlayerEntity>
}