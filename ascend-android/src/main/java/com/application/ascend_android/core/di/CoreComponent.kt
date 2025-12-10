package com.application.ascend_android
import android.content.Context
import dagger.Component
import javax.inject.Singleton


@Component(modules = [CoreModule::class, ServiceModule::class , DatabaseModule::class, NetworkModule::class] )
@Singleton
interface CoreComponent : BaseComponent<CoreClient> {
    @Component.Builder
    interface Builder {
        fun build(): CoreComponent
        fun serviceModule(serviceModule: ServiceModule) : Builder
        fun databaseModule(databaseModule: DatabaseModule) : Builder
        fun networkModule(networkModule: NetworkModule) : Builder
    }

    fun provideContext() : Context
    fun  provideCoreClient() : CoreClient
    fun provideDataBaseInjector() : DatabaseInjector
    fun provideNetworkClient() : NetworkClient
}


