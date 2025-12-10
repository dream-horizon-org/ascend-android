package com.application.ascend_android

import com.google.gson.annotations.SerializedName

data class DRSExperimentRequest(
    @SerializedName("experiment_keys") val experimentKeys: List<String>,
    @SerializedName("attributes") val attributes: ExperimentAttributes,
    @SerializedName("stable_id") val stableId: String,
    @SerializedName("user_id") val userId: String? = null
)

data class ExperimentAttributes(
    @SerializedName("platform") val platform: String,
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("build_number") val buildNumber: String
)