package com.application.ascend_android

import retrofit2.Response


sealed class NetworkState<out T> {
    data class Success<out T>(val data: T): NetworkState<T>()
    data class Error<T>(val response: Response<T>): NetworkState<T>()
    data class APIException<T>(val throwable: Throwable): NetworkState<T>()
}