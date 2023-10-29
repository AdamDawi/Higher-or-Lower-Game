package com.example.myapplication.roomDatabase

import android.app.Application

/*
Application class (must be android:name=".roomDatabase.PlayerApp" in manifest)
allows us to get database instance
gets access to the dao
*/
class PlayerApp: Application() {
    /*
     lazy is used to create properties that are initialized only
     when you first refer to them, and then this value is cached and is not recalculated on subsequent references
     */
    val database by lazy {
        //passing application context
        PlayerDatabase.getInstance(this)
    }

}