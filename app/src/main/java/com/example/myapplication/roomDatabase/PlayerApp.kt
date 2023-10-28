package com.example.myapplication.roomDatabase

import android.app.Application

class PlayerApp: Application() {

    val db by lazy {
        PlayerDatabase.getInstance(this)
    }

}