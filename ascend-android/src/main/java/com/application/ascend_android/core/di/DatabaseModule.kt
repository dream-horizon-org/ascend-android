package com.application.ascend_android
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {

    @Provides
    @Singleton
    fun provideDataBaseInjector() = DatabaseInjector(context = context)

    @Provides
    @Singleton
    fun provideGson() = Gson()
}