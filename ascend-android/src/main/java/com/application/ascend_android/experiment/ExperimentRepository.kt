package com.application.ascend_android

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

const val DRS_EXPERIMENTS_PREF_KEY = "experiments_plugger"
const val DRS_EXPERIMENTS_FETCH_TIME = "DRS_EXPERIMENTS_FETCH_TIME"

@Suppress("UNCHECKED_CAST")
class ExperimentRepository @Inject constructor(val moduleProvider: IModuleProvider) :
    IDRSDataSource {
    private val gson = Gson()
    override suspend fun getRemoteData(
        request: DRSExperimentRequest,
        needRawResponse: Boolean
    ): NetworkState<Any> {
        val networkRequest = Request(
            requestType = RequestType.FETCH_EXPERIMENTS,
            requestBody = request,
            headerOrQueryMap = moduleProvider.getConfig().httpConfig().getHeaderMap(),
            needRawResponse = true,
        )
        return moduleProvider.getCoreClient().getNetworkData(networkRequest)
    }

    override fun updateHeaderMaps(customHeaders: HashMap<String, Any>) {
        if (AscendUser.guestId.isNotEmpty()) {
            moduleProvider.getConfig().httpConfig()
                .updateHeaderMap(guestId, AscendUser.guestId)
        } else {
            moduleProvider.getConfig().httpConfig().removeKeyFromHeaderMap(guestId)
        }

        if (AscendUser.userId.isNotEmpty()) {
            moduleProvider.getConfig().httpConfig()
                .updateHeaderMap(userId, AscendUser.userId)
        } else {
            moduleProvider.getConfig().httpConfig().removeKeyFromHeaderMap(userId)
        }

        for ((key, value) in customHeaders) {
            if (value != null) {
                moduleProvider.getConfig().httpConfig().updateHeaderMap(key, value.toString())
            }
        }
    }


    override fun getLocalMap(): ConcurrentHashMap<String, ExperimentDetails> {
        var map = ConcurrentHashMap<String, ExperimentDetails>()
        if (moduleProvider.getCoreClient().getLocalData(DRS_EXPERIMENTS_PREF_KEY).isNotEmpty())
            map = moduleProvider.getCoreClient().getLocalData(DRS_EXPERIMENTS_PREF_KEY).let {
                gson.fromJson<ConcurrentHashMap<String, ExperimentDetails>>(
                    it,
                    object : TypeToken<ConcurrentHashMap<String, ExperimentDetails>>() {}.type
                )
            }
        return map
    }

    override fun realtimeDRSOnForeground(): Boolean {
        return (moduleProvider.getConfig() as ExperimentConfig).shouldRefreshDRSOnForeground
    }

    override fun addAPIKeyToHeader() {
        moduleProvider.getConfig().httpConfig().updateHeaderMap(
            "TestKey",
            moduleProvider.getPluggerConfig().pluggerClientConfig().clientApiKey()
        )
    }

    override fun saveLocalMap(experimentsMap: Map<String, ExperimentDetails>) {
        moduleProvider.getCoreClient().saveLocalData(
            DRS_EXPERIMENTS_PREF_KEY,
            gson.toJson(experimentsMap)
        )
    }

    override fun saveLocalString(key: String, value: String) {
        moduleProvider.getCoreClient().saveLocalData(
            key,
            value
        )
    }

    override fun saveLocalLong(key: String, value: Long) {
        moduleProvider.getCoreClient().saveLocalLong(
            key,
            value
        )
    }

    override fun getLocalLong(key: String): Long {
        return moduleProvider.getCoreClient().getLocalLong(
            key
        )
    }

    override fun deleteLocal() {
        CoroutineUtils.launchCoroutine {
            moduleProvider.getCoreClient().removeLocalData(DRS_EXPERIMENTS_PREF_KEY)
        }
    }

    override fun areGuestAndAccessTokenEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getDevice(): IDevice {
        return moduleProvider.getDevice()
    }
}
