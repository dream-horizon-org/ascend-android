package com.application.ascend_android

import com.google.gson.annotations.SerializedName


data class NetworkError(

    @SerializedName("error") var error: Error? = Error()

)