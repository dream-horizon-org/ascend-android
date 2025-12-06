package com.application.ascend_android

open class Config(
    var httpConfig: HttpConfig,
    private val pluginType: Plugins,
) : IConfigProvider {

    override fun httpConfig(): IHttpConfigProvider {
        return httpConfig
    }

    override fun pluginType(): Plugins {
        return pluginType
    }

}

data class HttpConfig(
    var headers: HashMap<String, String> = HashMap<String, String>(
    ).also {
        it["Content-Type"] = "application/json"
        it["Accept"] = "application/json"
    },
    val apiBaseUrl: String,
    val shouldRetry: Boolean = true,
    val fetchInterval: Long = 5000L,
    val timeOutConfig: TimeoutConfig = TimeoutConfig(10000L),
    val retrialConfig: RetrialConfig = RetrialConfig(3)
) : IHttpConfigProvider {

    override fun getHeaderMap(): Map<String, String> {
        if (!headers.containsKey("Content-Type"))
            headers["Content-Type"] = "application/json"

        return headers
    }

    override fun shouldRetry(): Boolean {
        return shouldRetry
    }

    override fun getBaseUrl(): String {
        return apiBaseUrl
    }

    override fun fetchInterval(): Long {
        return fetchInterval
    }

    override fun timeOutConfig(): ITimeoutConfig {
        return timeOutConfig
    }

    override fun retrialConfig(): IRetryConfig {
        return retrialConfig
    }

    override fun updateHeaderMap(key: String, value: String) {
        headers[key] = value
    }

    override fun removeKeyFromHeaderMap(key: String) {
        headers.remove(key)
    }


}

data class RetrialConfig(
    val attempts: Int,
    val delay: Delay = Delay(),
    val omittedRetrialCodes: HashSet<Int> = HashSet()
) : IRetryConfig {
    override fun maxLimit(): Int {
        return attempts
    }

    override fun delay(): IDelay {
        return delay
    }

    override fun omittedRetrialCodes(): HashSet<Int> {
        return omittedRetrialCodes
    }

}

data class Delay(
    val time: Long = 500L,
    val policy: RetryPolicy = RetryPolicy.LINEAR
) : IDelay {
    override fun delayTime(): Long {
        return time
    }

    override fun retrialPolicy(): NetworkRetrialPolicy {
        return when (policy) {
            RetryPolicy.LINEAR -> NetworkRetrialPolicy.LINEAR
            RetryPolicy.INCREMENTAL -> NetworkRetrialPolicy.INCREMENTAL
            RetryPolicy.QUADRATIC -> NetworkRetrialPolicy.QUADRATIC
        }
    }

}

data class TimeoutConfig(
    val callTimeout: Long,
    val shouldEnableLogging: Boolean = false
) : ITimeoutConfig {
    override fun callTimeout(): Long {
        return callTimeout
    }

    override fun shouldEnableLogging(): Boolean {
        return shouldEnableLogging
    }

}

data class ClientConfig(val apiKey: String) : IClientConfigProvider {
    override fun clientApiKey(): String {
        return apiKey
    }

}