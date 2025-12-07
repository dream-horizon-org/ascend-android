package com.application.ascend_android

import android.content.Context
import android.util.Log
import java.util.UUID

/**
 * StableIdProvider manages the generation and persistence of unique stable IDs.
 * A stable ID is created once per app installation and persists across app sessions.
 */
object StableIdProvider {

    private const val PREF_NAME = "ascend_sdk_prefs"
    private const val KEY_STABLE_ID = "stable_id"
    
    // Cached stable ID to avoid repeated SharedPreferences reads
    @Volatile
    private var cachedStableId: String? = null

    /**
     * Retrieves the existing stable ID or creates a new one if it doesn't exist.
     * The stable ID is persisted in SharedPreferences and cached in memory.
     * 
     * @param context The application context
     * @return The stable ID string (UUID format)
     */
    fun getOrCreate(context: Context): String {
        // Return cached value if available
        cachedStableId?.let {
            Log.d(LOG_TAG, "StableIdProvider: Using cached stable ID: $it")
            return it
        }

        // Retrieve from SharedPreferences
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var id = prefs.getString(KEY_STABLE_ID, null)

        if (id == null) {
            // Generate new stable ID
            id = UUID.randomUUID().toString()
            Log.i(LOG_TAG, "StableIdProvider: Generated new stable ID: $id")
            
            // Persist to SharedPreferences
            prefs.edit().putString(KEY_STABLE_ID, id).apply()
            Log.d(LOG_TAG, "StableIdProvider: Stable ID saved to SharedPreferences")
        } else {
            Log.d(LOG_TAG, "StableIdProvider: Retrieved existing stable ID from storage: $id")
        }

        // Cache the stable ID
        cachedStableId = id
        
        return id
    }

    /**
     * Gets the current stable ID without generating a new one.
     * Returns null if no stable ID exists yet.
     * 
     * @param context The application context
     * @return The stable ID string or null if not yet generated
     */
    fun get(context: Context): String? {
        // Return cached value if available
        cachedStableId?.let {
            Log.d(LOG_TAG, "StableIdProvider: Retrieved cached stable ID: $it")
            return it
        }

        // Try to retrieve from SharedPreferences
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_STABLE_ID, null)
        
        if (id != null) {
            Log.d(LOG_TAG, "StableIdProvider: Retrieved stable ID from storage: $id")
            cachedStableId = id
        } else {
            Log.d(LOG_TAG, "StableIdProvider: No stable ID found in storage")
        }
        
        return id
    }

    /**
     * Clears the stable ID from memory and persistent storage.
     * Use this method when the user logs out or you need to reset the stable ID.
     * 
     * @param context The application context
     */
    fun clear(context: Context) {
        Log.i(LOG_TAG, "StableIdProvider: Clearing stable ID")
        
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_STABLE_ID).apply()
        
        cachedStableId = null
        
        Log.d(LOG_TAG, "StableIdProvider: Stable ID cleared from storage and cache")
    }

    /**
     * Logs the current stable ID status for debugging purposes.
     * 
     * @param context The application context
     */
    fun logStatus(context: Context) {
        val id = get(context)
        if (id != null) {
            Log.i(LOG_TAG, "StableIdProvider: Status - Stable ID is active: $id")
        } else {
            Log.i(LOG_TAG, "StableIdProvider: Status - No stable ID exists")
        }
    }
}

