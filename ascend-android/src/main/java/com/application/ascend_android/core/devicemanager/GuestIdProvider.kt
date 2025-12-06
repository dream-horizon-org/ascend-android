package com.application.ascend_android

import android.content.Context
import android.util.Log
import java.util.UUID

/**
 * GuestIdProvider manages the generation and persistence of unique guest IDs.
 * A guest ID is created once per app installation and persists across app sessions.
 */
object GuestIdProvider {

    private const val PREF_NAME = "ascend_sdk_prefs"
    private const val KEY_GUEST_ID = "guest_id"
    
    // Cached guest ID to avoid repeated SharedPreferences reads
    @Volatile
    private var cachedGuestId: String? = null

    /**
     * Retrieves the existing guest ID or creates a new one if it doesn't exist.
     * The guest ID is persisted in SharedPreferences and cached in memory.
     * 
     * @param context The application context
     * @return The guest ID string (UUID format)
     */
    fun getOrCreate(context: Context): String {
        // Return cached value if available
        cachedGuestId?.let {
            Log.d(LOG_TAG, "GuestIdProvider: Using cached guest ID: $it")
            return it
        }

        // Retrieve from SharedPreferences
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var id = prefs.getString(KEY_GUEST_ID, null)

        if (id == null) {
            // Generate new guest ID
            id = UUID.randomUUID().toString()
            Log.i(LOG_TAG, "GuestIdProvider: Generated new guest ID: $id")
            
            // Persist to SharedPreferences
            prefs.edit().putString(KEY_GUEST_ID, id).apply()
            Log.d(LOG_TAG, "GuestIdProvider: Guest ID saved to SharedPreferences")
        } else {
            Log.d(LOG_TAG, "GuestIdProvider: Retrieved existing guest ID from storage: $id")
        }

        // Cache the guest ID
        cachedGuestId = id
        
        return id
    }

    /**
     * Gets the current guest ID without generating a new one.
     * Returns null if no guest ID exists yet.
     * 
     * @param context The application context
     * @return The guest ID string or null if not yet generated
     */
    fun get(context: Context): String? {
        // Return cached value if available
        cachedGuestId?.let {
            Log.d(LOG_TAG, "GuestIdProvider: Retrieved cached guest ID: $it")
            return it
        }

        // Try to retrieve from SharedPreferences
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_GUEST_ID, null)
        
        if (id != null) {
            Log.d(LOG_TAG, "GuestIdProvider: Retrieved guest ID from storage: $id")
            cachedGuestId = id
        } else {
            Log.d(LOG_TAG, "GuestIdProvider: No guest ID found in storage")
        }
        
        return id
    }

    /**
     * Clears the guest ID from memory and persistent storage.
     * Use this method when the user logs out or you need to reset the guest ID.
     * 
     * @param context The application context
     */
    fun clear(context: Context) {
        Log.i(LOG_TAG, "GuestIdProvider: Clearing guest ID")
        
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_GUEST_ID).apply()
        
        cachedGuestId = null
        
        Log.d(LOG_TAG, "GuestIdProvider: Guest ID cleared from storage and cache")
    }

    /**
     * Logs the current guest ID status for debugging purposes.
     * 
     * @param context The application context
     */
    fun logStatus(context: Context) {
        val id = get(context)
        if (id != null) {
            Log.i(LOG_TAG, "GuestIdProvider: Status - Guest ID is active: $id")
        } else {
            Log.i(LOG_TAG, "GuestIdProvider: Status - No guest ID exists")
        }
    }
}

