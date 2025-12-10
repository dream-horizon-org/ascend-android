package com.application.androidSdk

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.util.*

class ConfigStore(private val context: Context) {
     var  properties: Properties = Properties()

     lateinit var map: Map<String , String>

    private fun init() {
        try {
            val test = context.assets.open(CONFIG_PROPERTIES_FILENAME)
            properties.load(test)
            map =getOrderedProperties(test)
            Log.d("Files----->",properties.toString())
        } catch (e: Exception) {
            Log.d("ConfigStore", "Error in init properties: " + e.message)
        }
    }

    fun getValue(key: String?): String {
        return properties.getProperty(key)
    }

    @Throws(IOException::class)
    fun getOrderedProperties(input: InputStream?): Map<String, String> {
        val mp: MutableMap<String, String> = LinkedHashMap()
       object : Properties() {
            @Synchronized
            override fun put(key: Any?, value: Any?): Any? {
                return mp.put(key as String , value as String)
            }
        }
      //  properties.load(input)
        return mp
    }

    companion object {
        private const val CONFIG_PROPERTIES_FILENAME = "config.local.properties"
    }

    init {
        init()
    }
}