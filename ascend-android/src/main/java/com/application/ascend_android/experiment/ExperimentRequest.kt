package com.application.ascend_android

import com.google.gson.annotations.SerializedName

data class DRSExperimentRequest(@SerializedName("apiPaths") val apiPaths: List<String>)