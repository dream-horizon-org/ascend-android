package com.application.ascend_android
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule {

    @Provides
    @Singleton
    fun provideCoreClient() = CoreClient()

}