package com.application.ascend_android

abstract class SharedPrefTable {
    protected abstract fun clearAndUpdateValues(newValues: Map<String?, Any?>?)

    abstract operator fun contains(key: String?): Boolean

    open fun getBoolean(key: String?): Boolean {
        return getBoolean(key, false)
    }

    abstract fun getBoolean(key: String?, defaultValue: Boolean): Boolean

    abstract fun getBoolean(key: String?, defaultValue: Boolean, prefName: String?): Boolean

    abstract fun setBoolean(key: String?, value: Boolean)

    abstract fun setBoolean(key: String?, value: Boolean, prefName: String?)

    open fun getString(key: String?): String? {
        return getString(key, "")
    }

    abstract fun getString(key: String?, defaultValue: String?): String?

    abstract fun setString(key: String?, value: String?)


    open fun getInt(key: String?): Int {
        return getInt(key, 0)
    }

    abstract fun getInt(key: String?, defaultValue: Int): Int

    abstract fun setInt(key: String?, value: Int)

    abstract fun setLong(key: String?, value: Long)

    open fun getLong(key: String?): Long {
        return getLong(key, 0)
    }

    abstract fun getLong(key: String?, defaultValue: Long): Long

    abstract fun remove(key: String?)

    abstract fun clearDataFromPref()

    abstract fun clearDataFromPref(name: String?)

    protected abstract fun getEntries(): Map<String?, *>?

}