package com.application.ascend_android

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface IApi {
    @POST("/v1/allocations")
    suspend fun getDRSExperiments(
        @HeaderMap headers: Map<String, String>,
        @Body commonRequest: Any?
    ): Response<Any>

    @GET("/users/feature-flags")
    suspend fun getFeatureFlags(@HeaderMap headers: Map<String, String>): Response<Any>

    @POST("/v1/users/features")
    @JvmSuppressWildcards
    suspend fun getFeatureFlagsPost(@HeaderMap headers: Map<String, String>,
                                    @Body body: Map<String, Any>): Response<Any>

    @POST("/process")
    suspend fun trackEvent(@HeaderMap headers: Map<String, String>, @Body body: Any): Response<Void>

    @GET
    suspend fun getClientConfigData(
        @HeaderMap headers: Map<String, String>,
        @Url url: String,
        @QueryMap queryMap: Map<String, String>
    ): Response<Any>


    @POST("/check-session")
    suspend fun checkSession(
        @Body commonRequest: Any?
    ): Response<Void>

    @POST("/consent")
    suspend fun markConsent(
        @Body commonRequest: Any?
    ): Response<Any>

    @GET("/authorize")
    suspend fun authorize(
        @QueryMap(encoded = true) queryMap: Map<String, String>
    ): Response<Unit>

    @Multipart
    @POST("/token")
    suspend fun getTokens(
        @PartMap() partMap: MutableMap<String, RequestBody>,
        @HeaderMap headerMap: Map<String, String>
    ): Response<Unit>

}

