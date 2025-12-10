package com.application.ascend_android

/**
 * Enum representing the data types supported for experiment variables.
 * Maps to the data_type field in the API response.
 */
enum class DataType(val apiValue: String) {
    BOOLEAN("BOOLEAN"),
    STRING("STRING"),
    INT("INT"),
    DOUBLE("DOUBLE"),
    LONG("LONG");

    companion object {
        /**
         * Converts API data_type string to DataType enum.
         * Returns null if the type is not recognized.
         */
        fun fromApiValue(apiValue: String?): DataType? {
            return values().find { it.apiValue.equals(apiValue, ignoreCase = true) }
        }
    }
}
