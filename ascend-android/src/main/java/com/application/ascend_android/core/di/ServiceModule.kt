package com.application.ascend_android

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule(val context : Context) {

    @Provides
    @Singleton
    fun provideContext() = context


    @Provides
    @Singleton
    fun provideSharedPrefTable(databaseInjector: DatabaseInjector) = databaseInjector.getSharedPrefTable()

}