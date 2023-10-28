package com.example.myapplication.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlayerEntity::class],version = 2)
abstract class PlayerDatabase: RoomDatabase() {
    abstract fun playerDao(): PlayerDao //generated implementation automatically by roomDatabase

    companion object {
        @Volatile
        private var INSTANCE: PlayerDatabase? = null
        /**
         * Helper function to get the database.
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         * @param context The application context Singleton, used to get access to the filesystem.
         */
        //initializes the database instance if it has not already been created
        fun getInstance(context: Context): PlayerDatabase {
            //only one thread may enter a synchronized block at a time
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlayerDatabase::class.java,
                        "player_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}