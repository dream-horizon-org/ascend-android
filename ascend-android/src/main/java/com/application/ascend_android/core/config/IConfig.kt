package com.application.ascend_android

import android.content.Context
import kotlin.reflect.KFunction0


enum class NetworkRetrialPolicy {
    LINEAR, INCREMENTAL, QUADRATIC
}

enum class RequestType {
    FETCH_EXPERIMENTS,
    FETCH_FEATURE_FLAGS,
    FETCH_FEATURE_FLAGS_POST,
    POST_EVENTS,
    EVENTS_CLIENT_CONFIG,
    CHECK_SESSION,
    MARK_CONSENT,
    AUTHORIZE,
    GET_TOKENS
}

enum class PluggerEvents(val value: String) {
    USER_LOGGED_IN("Plugger_User_Logged_In"),
    USER_LOGGED_OUT("Plugger_User_Logged_Out")

}

enum class Plugins(val pluginName: String) {
    EVENTS("EventsPlugin"),
    EXPERIMENTS("DRSPlugin"),
}

interface IConfigProvider {
    fun httpConfig(): IHttpConfigProvider
    fun pluginType(): Plugins

}

interface IHttpConfigProvider {
    fun getHeaderMap(): Map<String, String>
    fun shouldRetry(): Boolean
    fun getBaseUrl(): String
    fun fetchInterval(): Long
    fun timeOutConfig(): ITimeoutConfig
    fun retrialConfig(): IRetryConfig
    fun updateHeaderMap(key: String, value: String)
    fun removeKeyFromHeaderMap(key: String)
}

interface ITimeoutConfig {
    fun callTimeout(): Long
    fun shouldEnableLogging(): Boolean
}

interface IRetryConfig {
    fun maxLimit(): Int
    fun delay(): IDelay
    fun omittedRetrialCodes(): HashSet<Int>

}

interface IPluginConfig {
    fun pluginType(): KFunction0<Any>
    fun pluginName(): String
    fun configProvider(): IConfigProvider
}

interface IPluggerConfig {
    fun httpConfig(): IHttpConfigProvider
    fun plugins(): ArrayList<IPluginConfig>
    fun pluggerClientConfig(): IClientConfigProvider
}


interface IDelay {
    fun delayTime(): Long
    fun retrialPolicy(): NetworkRetrialPolicy
}

interface IClientConfigProvider {
    fun clientApiKey(): String
}

interface IPlugin {

    fun init(context: Context, config: IConfigProvider)
    fun onNotify(pluggerEvents: PluggerEvents, data: Any?)

}
