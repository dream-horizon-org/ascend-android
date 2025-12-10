package com.application.ascend_android
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.pow

const val SUCCESS_EMPTY = "Success"

class NetworkClient @Inject constructor(
    private val httpInterface: IApi,
    var shouldRetry: Boolean = false,
    val retrialPolicy: NetworkRetrialPolicy = NetworkRetrialPolicy.LINEAR,
    var retryTimes: Int = 0,
    var delayTime: Long = 1000,
    val pluginType: Plugins,
    val omittedRetrialCodes: HashSet<Int> = HashSet()
) {

    suspend fun getData(
        request: Request
    ): NetworkState<Any> {
        var networkState: NetworkState<Any> = NetworkState.APIException(Throwable())
        if (!shouldRetry) retryTimes = 0
        retryOperation(
            retries = retryTimes,
            retryStrategy = retrialPolicy,
            initialIntervalMilli = delayTime
        ) {
            try {
                val response = getResponseFromCall(request)
                if (response.raw().isRedirect) {
                    networkState = NetworkState.Success(response)
                    return@retryOperation
                }
                if (response.isSuccessful) {
                    if (request.needRawResponse) {
                        networkState = NetworkState.Success(response)
                        return@retryOperation
                    } else if (!request.shouldCheckResponseBody) {
                        networkState = NetworkState.Success(SUCCESS_EMPTY)
                        return@retryOperation
                    }
                    val responseBody = response.body()
                    if (responseBody != null) {
                        networkState = NetworkState.Success(responseBody)
                        return@retryOperation
                    } else {
                        if (!operationFailed())
                            networkState = NetworkState.Error(response)
                    }
                } else {
                    if (omittedRetrialCodes.contains(response.code())) {
                        networkState = NetworkState.Error(response)
                        return@retryOperation
                    } else if (!operationFailed())
                        networkState = NetworkState.Error(response)
                }
            } catch (e: Exception) {
                if (!operationFailed()) {
                    networkState = NetworkState.APIException(e)

                }
            }
        }
        return networkState

    }


    private suspend fun getResponseFromCall(
        request: Request
    ): Response<out Any> {

        val response = when (request.requestType) {
            RequestType.FETCH_EXPERIMENTS -> httpInterface.getDRSExperiments(
                request.headerOrQueryMap,
                request.requestBody
            )

            RequestType.FETCH_FEATURE_FLAGS -> {
                httpInterface.getFeatureFlags(request.headerOrQueryMap)
            }

            RequestType.FETCH_FEATURE_FLAGS_POST -> {
                httpInterface.getFeatureFlagsPost(request.headerOrQueryMap,request.requestBodyMap )
            }

            RequestType.EVENTS_CLIENT_CONFIG -> {
                httpInterface.getClientConfigData(request.headerMap, request.changedBaseURl, request.headerOrQueryMap)
            }

            RequestType.POST_EVENTS -> {
                httpInterface.trackEvent(request.headerMap, request.requestBody)
            }

            RequestType.CHECK_SESSION -> {
                httpInterface.checkSession(request.requestBody)
            }

            RequestType.MARK_CONSENT -> {
                httpInterface.markConsent(request.requestBody)
            }

            RequestType.AUTHORIZE -> {
                httpInterface.authorize(request.headerOrQueryMap)
            }

            RequestType.GET_TOKENS -> {
                httpInterface.getTokens(
                    request.formData,
                    request.headerOrQueryMap
                )
            }
        }
        return response
    }


}

suspend fun retryOperation(
    retries: Int = 100,
    initialDelay: Long = 0,
    initialIntervalMilli: Long = 1000,
    retryStrategy: NetworkRetrialPolicy = NetworkRetrialPolicy.INCREMENTAL,
    operation: suspend RetryOperation.() -> Unit
) {
    val retryOperation = RetryOperation(
        retries,
        initialIntervalMilli,
        retryStrategy,
        operation,
    )
    delay(initialDelay)
    operation.invoke(retryOperation)
}

internal fun calculateDelay(
    tryNumber: Int,
    initialIntervalMilli: Long,
    retryPolicy: NetworkRetrialPolicy
): Long {
    return when (retryPolicy) {
        NetworkRetrialPolicy.LINEAR -> initialIntervalMilli
        NetworkRetrialPolicy.INCREMENTAL -> initialIntervalMilli * tryNumber
        NetworkRetrialPolicy.QUADRATIC -> ((initialIntervalMilli/1000).toDouble()).pow(tryNumber).toLong()*1000
    }
}







