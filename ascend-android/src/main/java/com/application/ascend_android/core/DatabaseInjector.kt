package com.application.ascend_android

import android.content.Context
import kotlinx.coroutines.CoroutineScope

class DatabaseInjector(val context: Context) : IDatabase {

    private var sharedPrefTable: SharedPrefTable? = null
    private var eventDatabase: EventDatabase? = null

    override fun getKeyValueTable(): KeyValueTable? {
        TODO("Not yet implemented")
    }

    override fun getDataBase(coroutineScope: CoroutineScope): EventDatabase {
        if (eventDatabase == null) {
            eventDatabase = EventDatabase.getDatabase(context, coroutineScope)
        }
        return eventDatabase as EventDatabase
    }


    @Synchronized
    override fun getSharedPrefTable(): SharedPrefTable? {
        if (sharedPrefTable == null) {
            sharedPrefTable = SharePrefTableImpl(context = context)
        }
        return sharedPrefTable
    }

    override fun <T> loadResponseFromJsonAsset(jsonName: String?, type: Class<T>?): T {
        TODO("Not yet implemented")
    }
}