package com.application.ascend_android

import android.content.Context
import dagger.Module
import dagger.Provides
import java.lang.ref.WeakReference
import javax.inject.Singleton


@Module
class PluggerModule(private val context: Context, private val configProvider: AscendConfig) {
    @Provides
    @Singleton
    fun providePluggerConfig(): AscendConfig = configProvider

    @Provides
    @Singleton
    fun injectDevice(): IDevice = Device(WeakReference(context))


    @Provides
    @Singleton
    fun providePluginManager() = PluginManager(configProvider, context)

}