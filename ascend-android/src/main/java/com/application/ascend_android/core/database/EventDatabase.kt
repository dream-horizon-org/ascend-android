package com.application.ascend_android

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope


// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [DBEvents::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    private class EventDataBaseCallback : Callback()
    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): EventDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "plugger_database"
                )
                    .addCallback(EventDataBaseCallback())
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

