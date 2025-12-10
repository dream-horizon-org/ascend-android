package com.application.ascend_android
import kotlin.reflect.KFunction0

data class PluginConfig(
    val pluginType: KFunction0<Any>,
    val pluginName: String,
    val pluginConfig: Config
) : IPluginConfig {
    override fun pluginType(): KFunction0<Any> {
        return pluginType
    }

    override fun pluginName(): String {
            return pluginName
        }

    override fun configProvider(): IConfigProvider {
        return pluginConfig
    }
}



class AscendConfig(
    private val httpConfig: HttpConfig,
    val plugins: ArrayList<PluginConfig>,
    val clientConfig: ClientConfig
) : IPluggerConfig {
    override fun httpConfig(): IHttpConfigProvider {
        return httpConfig
    }

    override fun plugins(): ArrayList<IPluginConfig> {
        val list = ArrayList<IPluginConfig>()
        list.addAll(plugins)
        return list
    }

    override fun pluggerClientConfig(): IClientConfigProvider {
        return clientConfig
    }

}