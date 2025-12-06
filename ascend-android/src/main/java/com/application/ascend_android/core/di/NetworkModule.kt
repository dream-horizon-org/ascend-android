package com.application.ascend_android

import android.content.Context
import dagger.Lazy
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule(val config: IConfigProvider, val context: Context) {

    @Provides
    @Singleton
    fun provideHttpClient() = HttpClient(
        context = context,
        baseUrl = config.httpConfig().getBaseUrl(),
        callTimeOut = config.httpConfig().timeOutConfig().callTimeout()
    )


    @Provides
    @Singleton
    fun provideConfigProvider() = config

    @Provides
    @Singleton
    fun provideOkHttpClient(networkClient: Lazy<HttpClient>) = networkClient.get().okHttpClient

    @Provides
    @Singleton
    fun retrofit(networkClient: Lazy<HttpClient>) = networkClient.get().retrofit

    @Provides
    @Singleton
    fun restInterface(retrofit: Lazy<Retrofit>): IApi = retrofit.get().create(IApi::class.java)

    @Provides
    @Singleton
    internal fun provideNetworkClient(httpInterface: Lazy<IApi>) = NetworkClient(
        httpInterface.get(),
        shouldRetry = config.httpConfig().shouldRetry(),
        retryTimes = config.httpConfig().retrialConfig().maxLimit(),
        retrialPolicy = config.httpConfig().retrialConfig().delay().retrialPolicy(),
        pluginType = config.pluginType(),
        delayTime = config.httpConfig().retrialConfig().delay().delayTime(),
        omittedRetrialCodes = config.httpConfig().retrialConfig().omittedRetrialCodes()
    )



}