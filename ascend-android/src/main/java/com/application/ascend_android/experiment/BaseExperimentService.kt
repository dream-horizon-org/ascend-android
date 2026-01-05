package com.application.ascend_android

import com.google.gson.JsonObject

const val VALUE = "value"

abstract class BaseExperimentService {
    abstract fun getBooleanFlag(
        experimentKey: String,
        variable: String = VALUE,
        dontCache: Boolean = false,
        ignoreCache: Boolean = false
    ): Boolean

    abstract fun getIntFlag(
        experimentKey: String,
        variable: String = VALUE,
        dontCache: Boolean = false,
        ignoreCache: Boolean = false
    ): Int

    abstract fun getDoubleFlag(
        experimentKey: String,
        variable: String = VALUE,
        dontCache: Boolean = false,
        ignoreCache: Boolean = false
    ): Double

    abstract fun getLongFlag(
        experimentKey: String,
        variable: String = VALUE,
        dontCache: Boolean = false,
        ignoreCache: Boolean = false
    ): Long

    abstract fun getStringFlag(
        experimentKey: String,
        variable: String = VALUE,
        dontCache: Boolean = false,
        ignoreCache: Boolean = false
    ): String

    abstract fun getAllVariables(experimentKey: String): JsonObject?
    abstract fun fetchExperiments(map: HashMap<String, JsonObject?>, callback: IExperimentCallback)
    abstract fun refreshExperiment(callback: IExperimentCallback)
    abstract fun getExperimentVariants(): HashMap<String, ExperimentDetails>
    abstract fun setExperimentsToStorage(experiments: HashMap<String, ExperimentDetails>)
    abstract fun getExperimentsFromStorage(): HashMap<String, ExperimentDetails>
    internal abstract fun clearAllExperimentsData()
    internal abstract fun clearUserSessionData()
}