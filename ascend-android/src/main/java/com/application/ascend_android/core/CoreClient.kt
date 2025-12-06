package com.application.ascend_android

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CoreClient {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var databaseInjector: DatabaseInjector


    @Inject
    lateinit var gson: Gson


    @Inject
    lateinit var networkClient: NetworkClient

    @Inject
    lateinit var configProvider: IConfigProvider

    suspend fun getNetworkData(
        request: Request
    ): NetworkState<Any> {
        return networkClient.getData(request)
    }


    fun saveLocalData(mapKey: String, mapValues: String) {
        databaseInjector.getSharedPrefTable()?.setString(
            mapKey,
            mapValues
        )
    }

    fun getLocalData(mapKey: String): String {
        return databaseInjector.getSharedPrefTable()?.getString(mapKey) ?: ""
    }

    fun getLocalLong(mapKey: String): Long {
        return databaseInjector.getSharedPrefTable()?.getLong(mapKey) ?: 0L
    }

    fun saveLocalLong(mapKey: String, value: Long) {
        databaseInjector.getSharedPrefTable()?.setLong(
            mapKey,
            value
        )
    }


    fun removeLocalData(mapKey: String) {
        databaseInjector.getSharedPrefTable()?.remove(mapKey)
    }

    suspend fun insert(coroutineScope: CoroutineScope, eventBody: DBEvents) {
        databaseInjector.getDataBase(coroutineScope).eventDao().insert(eventBody)
    }

    suspend fun insertAll(coroutineScope: CoroutineScope, eventBody: List<DBEvents>) {
        databaseInjector.getDataBase(coroutineScope).eventDao().insertAll(eventBody)
    }


    suspend fun insertAllConditionally(coroutineScope: CoroutineScope, eventBody: List<DBEvents>) {
        databaseInjector.getDataBase(coroutineScope).eventDao().insertAll(eventBody)
    }

    suspend fun retrieveAll(
        coroutineScope: CoroutineScope,
        filter: String,
        eventIds: ArrayList<String>
    ): List<DBEvents> {
        val x = databaseInjector.getDataBase(coroutineScope).eventDao()
            .getAllEventsWithFilter(filter, eventIds)
        return x.ifEmpty {
            ArrayList()
        }


    }


    suspend fun deleteAll(coroutineScope: CoroutineScope): Int {
        return databaseInjector.getDataBase(coroutineScope).eventDao().deleteAll()
    }

    suspend fun deleteAll(
        coroutineScope: CoroutineScope,
        eventIds: ArrayList<String>,
        filter: String,
        status: Int
    ): Int {
        return databaseInjector.getDataBase(coroutineScope).eventDao()
            .deleterConditionally(eventIds, filter, status)
    }

    suspend fun deleteWithoutStatus(
        coroutineScope: CoroutineScope,
        eventIds: ArrayList<String>,
        filter: String
    ): Int {
        return databaseInjector.getDataBase(coroutineScope).eventDao()
            .deleterWithoutStatus(eventIds, filter)
    }


    @OptIn(FlowPreview::class)
    suspend fun <T> Flow<List<T>>.flattenToList() =
        flatMapConcat { it.asFlow() }.toList()

}


