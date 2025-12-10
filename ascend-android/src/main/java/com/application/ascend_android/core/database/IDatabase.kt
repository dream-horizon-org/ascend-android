package com.application.ascend_android

import kotlinx.coroutines.CoroutineScope

interface IDatabase {
    fun getKeyValueTable(): KeyValueTable?
    fun getDataBase(coroutineScope: CoroutineScope): EventDatabase

    fun getSharedPrefTable(): SharedPrefTable?

    fun <T> loadResponseFromJsonAsset(jsonName: String?, type: Class<T>?): T
}