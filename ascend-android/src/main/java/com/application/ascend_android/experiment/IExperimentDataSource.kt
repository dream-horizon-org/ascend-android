package com.application.ascend_android

import java.util.concurrent.ConcurrentHashMap


interface IDRSDataSource {
    fun getLocalMap(): ConcurrentHashMap<String, ExperimentDetails>
    fun saveLocalMap(experimentsMap: Map<String, ExperimentDetails>)
    fun saveLocalString(key: String, value: String)
    fun saveLocalLong(key: String, value: Long)
    fun getLocalLong(key: String): Long

    fun deleteLocal()
    fun areGuestAndAccessTokenEmpty(): Boolean
    suspend fun getRemoteData(
        request: DRSExperimentRequest,
        needRawResponse: Boolean = false
    ): NetworkState<Any>

    fun updateHeaderMaps(customHeaders: HashMap<String, Any>)
    fun getDevice(): IDevice
    fun realtimeDRSOnForeground(): Boolean
    fun addAPIKeyToHeader()
}

