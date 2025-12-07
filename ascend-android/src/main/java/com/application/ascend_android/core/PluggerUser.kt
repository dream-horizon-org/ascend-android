package com.application.ascend_android

import android.content.Context
import android.location.Address
import android.util.Log


const val stableId = "stable-id"
const val userId = "user-id"


object AscendUser  {
    var isUserLoggedIn: Boolean = false
    var stableId: String = ""
    var userId: String = ""
    var refreshToken: String = ""

    /**
     * Initializes the stable ID for the user.
     * This should be called during SDK initialization.
     * 
     * @param context The application context
     */
    fun initializeStableId(context: Context) {
        if (stableId.isEmpty()) {
            stableId = StableIdProvider.getOrCreate(context)
            Log.i(LOG_TAG, "AscendUser: Stable ID initialized: $stableId")
        } else {
            Log.d(LOG_TAG, "AscendUser: Stable ID already set: $stableId")
        }
    }


    private fun setAsLoggedOut(stableId: String, authToken: String) {
        isUserLoggedIn = false
        userId = stableId
        PluggerCapabilities.daggerPluggerComponent.providePluginManager()
            .notifyAllPlugins(PluggerEvents.USER_LOGGED_OUT)

    }

    fun setLocation(address: Address) {
        PluggerCapabilities.daggerPluggerComponent.provideDevice().setLocation(address)
    }

    fun setLatLong(latitude: Double, longitude: Double) {
        PluggerCapabilities.daggerPluggerComponent.provideDevice().setLatLong(latitude, longitude)
    }


    fun setStable(stableId: String) {
        this.stableId = stableId
        this.userId = ""
    }

    fun setToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    fun setUser(userId: String) {
        this.userId = userId
    }


    private fun setAsLoggedIn(userId: String, authToken: String) {
        isUserLoggedIn = true
        AscendUser.userId = userId

        PluggerCapabilities.daggerPluggerComponent.providePluginManager()
            .notifyAllPlugins(PluggerEvents.USER_LOGGED_IN)
    }


}
