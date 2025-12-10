package com.application.ascend_android

import android.content.Context
import kotlin.reflect.KFunction0

class PluginManager(
    val config: AscendConfig, val context: Context,
    private var referenceMap: HashMap<String, KFunction0<Any>> = HashMap(), //Store Instances & Ref
    private var instanceMap: HashMap<String, Any> = HashMap()
) {


    init {
        initPlugins(config)
    }


    private fun initPlugins(config: AscendConfig) {
        val list = config.plugins
        list.forEach { plugin ->
            referenceMap[plugin.pluginName()] = plugin.pluginType()
        }
    }

    internal fun addPlugin(pluginConfig: PluginConfig) {
        referenceMap[pluginConfig.pluginName()] = pluginConfig.pluginType()
        config.plugins.add(pluginConfig)
    }


    fun <T> getPlugin(plugin: Plugins): T {
        return if (instanceMap.containsKey(plugin.pluginName)) {
            (instanceMap[plugin.pluginName]) as T
        } else {
            referenceMap[plugin.pluginName]?.let {
                instanceMap[plugin.pluginName] = (it.invoke())

                config.plugins.forEach { pluginConfig ->
                    if ((instanceMap[plugin.pluginName] is IPlugin) && plugin.pluginName == pluginConfig.pluginName)
                        (instanceMap[plugin.pluginName] as IPlugin).init(
                            context,
                            pluginConfig.configProvider()
                        )
                }

            }

            (instanceMap[plugin.pluginName]) as T
        }
    }


    fun notifyAllPlugins(pluggerEvents : PluggerEvents , data : Any? = null){
        instanceMap.forEach { plugin ->
            (plugin.value as IPlugin).onNotify(pluggerEvents, data)
        }
    }




}





