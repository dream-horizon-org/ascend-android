package com.application.ascend_android

import javax.inject.Inject

class ModuleProvider @Inject constructor(
    private val mCoreClient: CoreClient,
    private val mConfigProvider: Config
) : IModuleProvider {

    override fun getCoreClient(): CoreClient {
        return mCoreClient
    }

    override fun getConfig(): Config {
        return mConfigProvider
    }

    override fun getDevice(): IDevice {
        return PluggerCapabilities.daggerPluggerComponent.provideDevice()
    }

    override fun getPluggerConfig(): IPluggerConfig {
        return PluggerCapabilities.daggerPluggerComponent.providePluggerConfig()
    }

}



