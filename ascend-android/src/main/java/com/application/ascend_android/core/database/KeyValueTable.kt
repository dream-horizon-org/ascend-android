package com.application.ascend_android

interface KeyValueTable {
    fun set(key: String?, value: String?)

    fun get(key: String?): String?

    fun get(key: String?, defaultValue: String?): String?

    fun delete(key: String?)

    fun entrySet(): MutableSet<MutableMap.MutableEntry<String?, String?>?>?
}