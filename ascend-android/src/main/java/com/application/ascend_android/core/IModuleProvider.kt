package com.application.ascend_android
interface IModuleProvider {
    fun getCoreClient() : CoreClient

    fun getConfig(): Config
    fun getDevice(): IDevice

    fun getPluggerConfig(): IPluggerConfig

}

