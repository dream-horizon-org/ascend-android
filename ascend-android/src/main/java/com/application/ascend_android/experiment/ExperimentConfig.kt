package com.application.ascend_android

import com.google.gson.JsonObject
import java.util.concurrent.ConcurrentHashMap

class ExperimentConfig private constructor(
    var shouldFetchOnInit: Boolean = false,
    var defaultExperiments: ConcurrentHashMap<String, JsonObject?>,
    var iExperimentCallback: IExperimentCallback,
    var shouldRefreshDRSOnForeground: Boolean = false,
    var shouldFetchOnLogout: Boolean,
    httpConfig: HttpConfig,
    pluginType: Plugins
) : Config(httpConfig, pluginType) {


    data class Builder(val experimentCallback: IExperimentCallback) {
        private lateinit var httpConfig: HttpConfig
        private lateinit var experimentKeys: ConcurrentHashMap<String, JsonObject?>
        private var shouldFetchOnInit: Boolean = false
        private var shouldFetchOnLogout: Boolean = false
        private var refreshDRSOnForeground: Boolean = false

        fun httpConfig(httpConfig: HttpConfig) = apply { this.httpConfig = httpConfig }

        fun defaultValues(experiment_keys: HashMap<String, JsonObject?>) = apply {
            this.experimentKeys =
                ConcurrentHashMap(experiment_keys)
        }

        fun shouldFetchOnInit(shouldFetchOnInit: Boolean) =
            apply { this.shouldFetchOnInit = shouldFetchOnInit }

        fun shouldFetchOnLogout(shouldFetchOnLogout: Boolean) =
            apply { this.shouldFetchOnLogout = shouldFetchOnLogout }

        fun shouldRefreshDRSOnForeground(refreshDRSOnForeground: Boolean) =
            apply { this.refreshDRSOnForeground = refreshDRSOnForeground }

        fun build(): ExperimentConfig {
            return ExperimentConfig(
                shouldFetchOnInit = shouldFetchOnInit,
                defaultExperiments = experimentKeys,
                httpConfig = httpConfig,
                iExperimentCallback = experimentCallback,
                shouldFetchOnLogout = false,
                pluginType = Plugins.EXPERIMENTS,
                shouldRefreshDRSOnForeground = refreshDRSOnForeground
            )
        }
    }
}

