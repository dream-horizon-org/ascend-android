package com.application.ascend_android
import dagger.Component
import javax.inject.Singleton

@Component(modules = [PluggerModule::class])
@Singleton
interface PluggerComponent{

    @Component.Builder
    interface Builder {
        fun build(): PluggerComponent
        fun pluggerModule(pluggerModule: PluggerModule) : Builder
    }

    fun providePluginManager(): PluginManager
    fun providePluggerConfig(): AscendConfig
    fun provideDevice(): IDevice

}
