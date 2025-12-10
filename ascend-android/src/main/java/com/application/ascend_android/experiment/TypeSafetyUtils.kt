package com.application.ascend_android

import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 * Utility class for type-safe value manipulation and validation.
 * Similar to the React Native implementation's flagValueManipulation and safe parsing functions.
 */
object TypeSafetyUtils {
    private const val TAG = "TypeSafetyUtils"

    /**
     * Default values for each data type, similar to FF_DEFAULT_VALUES in React Native.
     */
    object DefaultValues {
        const val STRING = ""
        const val BOOLEAN = false
        const val INT = -1
        const val DOUBLE = -1.0
        const val LONG = -1L
    }

    /**
     * Safely converts a variable value to the expected type based on data_type.
     * Validates that the variable's data_type matches the expected type.
     * 
     * @param variable The variable object containing value, key, and data_type
     * @param expectedType The expected data type for this variable
     * @param defaultValue The default value to return if conversion fails
     * @return The converted value of type T, or defaultValue if conversion fails
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> convertValue(
        variable: Variable?,
        expectedType: DataType,
        defaultValue: T
    ): T {
        if (variable == null) {
            Log.w(TAG, "Variable is null, returning default value")
            return defaultValue
        }

        val value = variable.value
        val dataType = DataType.fromApiValue(variable.dataType)

        // Type validation: Check if the variable's data_type matches expected type
        if (dataType != null && dataType != expectedType) {
            Log.w(
                TAG,
                "Type mismatch for variable '${variable.key}': " +
                        "Expected ${expectedType.apiValue}, but got ${dataType.apiValue}. " +
                        "Using default value."
            )
            return defaultValue
        }

        // If data_type is null or doesn't match, try to parse anyway but log a warning
        if (dataType == null) {
            Log.w(
                TAG,
                "data_type is null or unrecognized for variable '${variable.key}'. " +
                        "Attempting to parse as ${expectedType.apiValue}."
            )
        }

        return when (expectedType) {
            DataType.BOOLEAN -> safeBooleanParse(value, defaultValue as? Boolean ?: DefaultValues.BOOLEAN) as T
            DataType.STRING -> safeStringParse(value, defaultValue as? String ?: DefaultValues.STRING) as T
            DataType.INT -> safeIntParse(value, defaultValue as? Int ?: DefaultValues.INT) as T
            DataType.DOUBLE -> safeDoubleParse(value, defaultValue as? Double ?: DefaultValues.DOUBLE) as T
            DataType.LONG -> safeLongParse(value, defaultValue as? Long ?: DefaultValues.LONG) as T
        }
    }

    /**
     * Safely parses a string value to Boolean.
     * Similar to React Native's boolean conversion logic.
     */
    private fun safeBooleanParse(value: String?, defaultValue: Boolean): Boolean {
        if (value.isNullOrBlank() || value == "null") {
            return defaultValue
        }
        return value.trim().lowercase() == "true"
    }

    /**
     * Safely parses a string value, returning it as-is or default if null/empty.
     */
    private fun safeStringParse(value: String?, defaultValue: String): String {
        if (value.isNullOrBlank() || value == "null") {
            return defaultValue
        }
        return value
    }

    /**
     * Safely parses a string value to Int.
     * Similar to React Native's safeNumberParse.
     */
    private fun safeIntParse(value: String?, defaultValue: Int): Int {
        if (value.isNullOrBlank() || value == "null") {
            return defaultValue
        }
        return try {
            value.trim().toInt()
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse '$value' as Int: ${e.message}")
            defaultValue
        }
    }

    /**
     * Safely parses a string value to Double.
     * Similar to React Native's safeNumberParse.
     */
    private fun safeDoubleParse(value: String?, defaultValue: Double): Double {
        if (value.isNullOrBlank() || value == "null") {
            return defaultValue
        }
        return try {
            value.trim().toDouble()
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse '$value' as Double: ${e.message}")
            defaultValue
        }
    }

    /**
     * Safely parses a string value to Long.
     */
    private fun safeLongParse(value: String?, defaultValue: Long): Long {
        if (value.isNullOrBlank() || value == "null") {
            return defaultValue
        }
        return try {
            value.trim().toLong()
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse '$value' as Long: ${e.message}")
            defaultValue
        }
    }

    /**
     * Converts a Variable to JsonObject property based on its data_type.
     * This ensures type-safe storage in JsonObject.
     */
    fun variableToJsonProperty(variable: Variable): Pair<String, JsonPrimitive>? {
        val key = variable.key ?: return null
        val value = variable.value ?: return null
        val dataType = DataType.fromApiValue(variable.dataType)

        val jsonPrimitive = when (dataType) {
            DataType.BOOLEAN -> JsonPrimitive(safeBooleanParse(value, false))
            DataType.INT -> {
                val intValue = safeIntParse(value, 0)
                JsonPrimitive(intValue)
            }
            DataType.DOUBLE -> {
                val doubleValue = safeDoubleParse(value, 0.0)
                JsonPrimitive(doubleValue)
            }
            DataType.LONG -> {
                val longValue = safeLongParse(value, 0L)
                JsonPrimitive(longValue)
            }
            DataType.STRING, null -> JsonPrimitive(value) // Default to string if type is null or STRING
        }

        return Pair(key, jsonPrimitive)
    }

    /**
     * Gets a variable from the variant's variables list by key.
     */
    fun getVariableByKey(variant: Variant?, key: String): Variable? {
        return variant?.variables?.find { it.key == key }
    }

    /**
     * Safely extracts a value from JsonObject with type-safe conversion.
     * Used for defaultMap and backward compatibility with JsonObject-based variables.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> extractFromJsonObject(
        jsonObject: JsonObject?,
        key: String,
        expectedType: DataType,
        defaultValue: T
    ): T {
        if (jsonObject == null || !jsonObject.has(key)) {
            return defaultValue
        }

        val element = jsonObject.get(key)
        if (element.isJsonNull) {
            return defaultValue
        }

        return try {
            when (expectedType) {
                DataType.BOOLEAN -> {
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isBoolean) {
                        element.asBoolean as T
                    } else if (element.isJsonPrimitive) {
                        // Try to parse string representation
                        safeBooleanParse(element.asString, defaultValue as? Boolean ?: DefaultValues.BOOLEAN) as T
                    } else {
                        defaultValue
                    }
                }
                DataType.STRING -> {
                    element.asString as T
                }
                DataType.INT -> {
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                        element.asInt as T
                    } else if (element.isJsonPrimitive) {
                        safeIntParse(element.asString, defaultValue as? Int ?: DefaultValues.INT) as T
                    } else {
                        defaultValue
                    }
                }
                DataType.DOUBLE -> {
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                        element.asDouble as T
                    } else if (element.isJsonPrimitive) {
                        safeDoubleParse(element.asString, defaultValue as? Double ?: DefaultValues.DOUBLE) as T
                    } else {
                        defaultValue
                    }
                }
                DataType.LONG -> {
                    if (element.isJsonPrimitive && element.asJsonPrimitive.isNumber) {
                        element.asLong as T
                    } else if (element.isJsonPrimitive) {
                        safeLongParse(element.asString, defaultValue as? Long ?: DefaultValues.LONG) as T
                    } else {
                        defaultValue
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to extract $key from JsonObject as ${expectedType.apiValue}: ${e.message}")
            defaultValue
        }
    }

    /**
     * Safely extracts a cached value from accessedMap with type casting.
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> extractCachedValue(
        cachedValue: Any?,
        expectedType: DataType,
        defaultValue: T,
        logTag: String,
        methodName: String,
        variableKey: String
    ): T? {
        if (cachedValue == null) return null
        
        return try {
            when (expectedType) {
                DataType.BOOLEAN -> (cachedValue as? Boolean) as? T
                DataType.STRING -> (cachedValue as? String) as? T
                DataType.INT -> (cachedValue as? Int) as? T
                DataType.DOUBLE -> (cachedValue as? Double) as? T
                DataType.LONG -> (cachedValue as? Long) as? T
            } ?: run {
                Log.w(logTag, "$methodName: Failed to cast cached value for $variableKey, type mismatch")
                null
            }
        } catch (e: Exception) {
            Log.w(logTag, "$methodName: Failed to cast cached value for $variableKey: ${e.message}")
            null
        }
    }

    /**
     * Unified type-safe value retrieval following the fallback chain:
     * 1. experimentMap -> variant.variables (type-safe from Variable objects)
     * 2. defaultMap (type-safe extraction from JsonObject)
     * 3. fallback (default value)
     * 
     * Note: accessedMap check should be done before calling this function.
     * This function handles the experimentMap -> defaultMap -> fallback chain.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> getTypeSafeValue(
        experiment: ExperimentDetails?,
        defaultMap: JsonObject?,
        variableKey: String,
        expectedType: DataType,
        defaultValue: T,
        logTag: String,
        methodName: String
    ): T {
        // Step 1: Check experimentMap -> variant.variables (type-safe from Variable objects)
        val variableObj = getVariableByKey(experiment?.variant, variableKey)
        if (variableObj != null) {
            val convertedValue = convertValue(variableObj, expectedType, defaultValue)
            Log.d(
                logTag,
                "$methodName: returning type-safe value from experimentMap for $variableKey: $convertedValue (data_type: ${variableObj.dataType})"
            )
            return convertedValue
        }

        // Step 2: Check defaultMap (type-safe extraction from JsonObject)
        defaultMap?.let { defaultJson ->
            val extractedValue = extractFromJsonObject(defaultJson, variableKey, expectedType, defaultValue)
            // Check if we got a different value than default (meaning extraction succeeded)
            if (extractedValue != defaultValue || defaultJson.has(variableKey)) {
                Log.d(
                    logTag,
                    "$methodName: returning type-safe value from defaultMap for $variableKey: $extractedValue"
                )
                return extractedValue
            }
        }

        // Step 3: Return fallback value
        Log.d(logTag, "$methodName: returning fallback value for $variableKey")
        return defaultValue
    }

    /**
     * Extracts a cached value from accessedMap with type safety.
     * Returns the cached value if found and type matches, null otherwise.
     */
    fun <T> getCachedValue(
        accessedMap: Map<String, Any>?,
        variableKey: String,
        expectedType: DataType,
        defaultValue: T,
        logTag: String,
        methodName: String
    ): T? {
        return accessedMap?.get(variableKey)?.let { cachedValue ->
            extractCachedValue(cachedValue, expectedType, defaultValue, logTag, methodName, variableKey)?.also {
                Log.d(logTag, "$methodName: returning accessed (cached) value for $variableKey: $it")
            }
        }
    }
}
