package com.application.ascend_android
import kotlinx.coroutines.delay

class RetryOperation internal constructor(
    private val retries: Int,
    private val initialIntervalMilli: Long = 1000,
    private val retryStrategy: NetworkRetrialPolicy = NetworkRetrialPolicy.INCREMENTAL,
    private val retry: suspend RetryOperation.() -> Unit
) {
    var tryNumber: Int = 0
        internal set

    suspend fun operationFailed() : Boolean {
        tryNumber++
        return if (tryNumber < retries) {
            delay(calculateDelay(tryNumber, initialIntervalMilli, retryStrategy))
            retry.invoke(this)
            true
        }else {
            false
        }
    }
}
