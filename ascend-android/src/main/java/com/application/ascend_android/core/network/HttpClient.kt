package com.application.ascend_android

import android.content.Context
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.Arrays
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class HttpClient @Inject constructor(
    val context: Context, var baseUrl: String,
    callTimeOut: Long
) {

    var gson: Gson = gsonNumberPolicyBuilder()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }


    val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                chain.proceed(builder.build())
            }
            callTimeout(callTimeOut, TimeUnit.MILLISECONDS)

            if (BuildConfig.IS_LOG_ENABLED)
                addInterceptor(interceptor)
            retryOnConnectionFailure(false)
            try {
                getTrustManager()?.let {
                    this.sslSocketFactory(TLSSocketFactory(), it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.followRedirects(false).build()

    }
    private val interceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    }

    private fun getTrustManager(): X509TrustManager? {
        try {
            val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm()
            )
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
                throw IllegalStateException(
                    "Unexpected default trust managers:" + Arrays.toString(
                        trustManagers
                    )
                )
            }
            return trustManagers[0] as X509TrustManager
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}