package com.application.ascend_android

import android.content.Context
import android.location.Address
import android.util.Log


const val guestId = "guest-id"
const val userId = "user-id"


object AscendUser  {
    var isUserLoggedIn: Boolean = false
    var guestId: String = ""
    var userId: String = ""
    var refreshToken: String = ""

    /**
     * Initializes the guest ID for the user.
     * This should be called during SDK initialization.
     * 
     * @param context The application context
     */
    fun initializeGuestId(context: Context) {
        if (guestId.isEmpty()) {
            guestId = GuestIdProvider.getOrCreate(context)
            Log.i(LOG_TAG, "AscendUser: Guest ID initialized: $guestId")
        } else {
            Log.d(LOG_TAG, "AscendUser: Guest ID already set: $guestId")
        }
    }


    private fun setAsLoggedOut(guestId: String, authToken: String) {
        isUserLoggedIn = false
        userId = guestId
        PluggerCapabilities.daggerPluggerComponent.providePluginManager()
            .notifyAllPlugins(PluggerEvents.USER_LOGGED_OUT)

    }

    fun setLocation(address: Address) {
        PluggerCapabilities.daggerPluggerComponent.provideDevice().setLocation(address)
    }

    fun setLatLong(latitude: Double, longitude: Double) {
        PluggerCapabilities.daggerPluggerComponent.provideDevice().setLatLong(latitude, longitude)
    }


    fun setGuest(guestId: String) {
        this.guestId = guestId
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
