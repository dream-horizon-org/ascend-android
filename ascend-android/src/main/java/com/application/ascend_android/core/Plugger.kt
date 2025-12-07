package com.application.ascend_android

import android.content.Context
import android.util.Log

class Ascend {
    companion object {
        private var isInitialized: Boolean = false

        var user = AscendUser

        private val customException = "Plugger is already Initialized"

        @Throws(Exception::class)
        fun init(
            pluggerConfig: AscendConfig,
            context: Context,
        ) {

            if (BuildConfig.DEBUG) {
                PluggerCapabilities.init(context, pluggerConfig)

                user.initializeStableId(context.applicationContext)
                Log.i(LOG_TAG, "Plugger core initialized successfully")
                return
            }

            if (!isInitialized) {
                isInitialized = true
                PluggerCapabilities.init(context, pluggerConfig)
                user.initializeStableId(context.applicationContext)
                Log.i(LOG_TAG, "Plugger core initialized successfully")
            } else {
                Log.e(LOG_TAG, customException)
                throw PluggerException(customException)
            }

        }

        fun addPlugin(pluginConfig: PluginConfig) {
            PluggerCapabilities.daggerPluggerComponent.providePluginManager()
                .addPlugin(pluginConfig)
        }

        fun isPluggerInitialised(): Boolean {
            return isInitialized
        }

        fun <T> getPlugin(plugin: Plugins): T {
            return PluggerCapabilities.daggerPluggerComponent.providePluginManager()
                .getPlugin(plugin)
        }

    }


}