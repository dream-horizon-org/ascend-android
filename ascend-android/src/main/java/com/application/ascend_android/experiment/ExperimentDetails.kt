package com.application.ascend_android


import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExperimentDetails {
    @SerializedName("experiment_id")
    @Expose
    var experimentId: String? = null

    // experiment_key is the key used for identifying experiments (apiPath)
    @SerializedName("experiment_key")
    @Expose
    var apiPath: String? = null

    @SerializedName("experiment_name")
    @Expose
    var experimentName: String? = null

    @SerializedName("variant_name")
    @Expose
    var variantName: String? = null

    @SerializedName("variant")
    @Expose
    var variant: Variant? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("assigned_at")
    @Expose
    var assignedAt: Long? = null

    // Computed property to get variables as JsonObject from the variant
    // Now uses type-safe conversion based on data_type
    var variables: JsonObject?
        get() {
            val jsonObject = JsonObject()
            variant?.variables?.forEach { variable ->
                // Use type-safe conversion to properly handle data_type
                TypeSafetyUtils.variableToJsonProperty(variable)?.let { (key, jsonPrimitive) ->
                    jsonObject.add(key, jsonPrimitive)
                }
            }
            return if (jsonObject.size() > 0) jsonObject else null
        }
        set(value) {
            // Keep setter for backward compatibility but don't use it
            // New format uses variant.variables list
        }
}