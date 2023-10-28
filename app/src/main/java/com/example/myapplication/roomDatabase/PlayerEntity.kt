package com.example.myapplication.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player-table")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name: String="",
    val points: Int=0
)
