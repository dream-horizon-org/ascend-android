package com.application.ascend_android

import android.content.Context
import android.content.SharedPreferences

class SharePrefTableImpl(val context: Context) : SharedPrefTable() {
    private var sharedPreferences: SharedPreferences? = null
    private val prefName = "DSPrefs"

    init {
        sharedPreferences = context.getSharedPreferences(
            prefName,
            Context.MODE_PRIVATE
        )
    }



    override fun clearAndUpdateValues(newValues: Map<String?, Any?>?) {
        TODO("Not yet implemented")
    }

    override fun contains(key: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String?, defaultValue: Boolean, prefName: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun setBoolean(key: String?, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun setBoolean(key: String?, value: Boolean, prefName: String?) {
        TODO("Not yet implemented")
    }

    override fun getString(key: String?, defaultValue: String?): String? {
        return sharedPreferences!!.getString(key, defaultValue)
    }

    override fun setString(key: String?, value: String?) {
        val editor = sharedPreferences!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    override fun getInt(key: String?, defaultValue: Int): Int {
        TODO("Not yet implemented")
    }

    override fun setInt(key: String?, value: Int) {
        TODO("Not yet implemented")
    }

    override fun setLong(key: String?, value: Long) {
        val editor = sharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    override fun getLong(key: String?, defaultValue: Long): Long {
        return sharedPreferences!!.getLong(key, defaultValue)
    }

    override fun remove(key: String?) {
        val editor = sharedPreferences!!.edit()
        editor.remove(key)
        editor.apply()
    }

    override fun clearDataFromPref() {
        TODO("Not yet implemented")
    }

    override fun clearDataFromPref(name: String?) {
        TODO("Not yet implemented")
    }

    override fun getEntries(): Map<String?, *>? {
        TODO("Not yet implemented")
    }
}