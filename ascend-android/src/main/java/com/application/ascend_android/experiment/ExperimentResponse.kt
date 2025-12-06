package com.application.ascend_android

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DRSExperimentResponse {

    @SerializedName("data")
    @Expose
    var data: ExperimentData? = null
}

class ExperimentData {
    @SerializedName("experiment_map")
    @Expose
    var experimentMap: List<ExperimentDetails>? = null
}

class Variant {
    @SerializedName("display_name")
    @Expose
    var displayName: String? = null

    @SerializedName("variant_name")
    @Expose
    var variantName: String? = null

    @SerializedName("variables")
    @Expose
    var variables: List<Variable>? = null
}

class Variable {
    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("key")
    @Expose
    var key: String? = null

    @SerializedName("data_type")
    @Expose
    var dataType: String? = null
}
