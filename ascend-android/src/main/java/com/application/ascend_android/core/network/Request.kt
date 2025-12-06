package com.application.ascend_android
import okhttp3.RequestBody

data class Request(
    val requestType: RequestType,
    val headerOrQueryMap: Map<String, String> = HashMap(),
    val headerMap: Map<String, String> = HashMap(),
    val shouldCheckResponseBody: Boolean = true,
    val needRawResponse: Boolean = false,
    val changedBaseURl: String = "",
    val requestBody: Any = Any(),
    val requestBodyMap: Map<String, Any> = HashMap(),
    val isHeaderMap: Boolean = false,
    val formData: MutableMap<String, RequestBody> = mutableMapOf()
)