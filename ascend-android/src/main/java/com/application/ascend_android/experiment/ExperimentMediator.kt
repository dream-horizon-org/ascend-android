package com.application.ascend_android

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.application.ascend_android.experiment.cacheWindow
import com.application.ascend_android.experiment.lastCachedResponseTimestamp
import com.application.ascend_android.experiment.lastModifiedTimestamp
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.ref.SoftReference
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
internal class ExperimentMediator @Inject constructor(
    private val experimentRepository: IDRSDataSource,
    moduleProvider: IModuleProvider
) {
    private val logTag = "ExperimentMediator"
    lateinit var defaultMap: ConcurrentHashMap<String, JsonObject?>

    private var customHeaders: HashMap<String, Any> = HashMap()
    private val allHeaders: HashMap<String, Any> = HashMap()

    var activeActivityCount = 0
    var experimentMap: ConcurrentHashMap<String, ExperimentDetails> = ConcurrentHashMap()
    var accessedMap: ConcurrentHashMap<String, ConcurrentHashMap<String, Any>> = ConcurrentHashMap()

    val gson = Gson()

    private fun addLifeCycleCallbacks() {
        val context: Context? = experimentRepository.getDevice().getApplicationContext()
        context?.let {
            (it as Application).registerActivityLifecycleCallbacks(object :
                ApplicationCallback() {
                override var activeActivities: Int
                    get() = activeActivityCount
                    set(value) {
                        activeActivityCount = value
                    }

                override fun onApplicationInForeground() {
                    Log.d(logTag, "onApplicationInForeground")
                    if (experimentRepository.realtimeDRSOnForeground()) {
                        foregroundExecution()
                    }
                }

                override fun onApplicationInBackground() {
                    Log.d(logTag, "onApplicationInBackground")
                }
            })
        }

    }

    private fun foregroundExecution() {
        if (allHeaders[cacheWindow] == null && allHeaders[lastCachedResponseTimestamp] == null) {
            getRemoteWithPreDefinedRequest(SoftReference(null))
        } else {
            allHeaders[cacheWindow]?.let { cacheWindow ->
                allHeaders[lastCachedResponseTimestamp]?.let { notModifiedTimestamp ->
                    val currentTimestamp = System.currentTimeMillis()
                    if ((currentTimestamp - notModifiedTimestamp as Long) > (cacheWindow as Int) * 1000) {
                        getRemoteWithPreDefinedRequest(SoftReference(null))
                    }
                }
            }
        }
    }

    init {
        if (moduleProvider.getConfig() is ExperimentConfig) {
            defaultMap = (moduleProvider.getConfig() as ExperimentConfig).defaultExperiments
        }
        experimentMap = experimentRepository.getLocalMap()
        addLifeCycleCallbacks()
        experimentRepository.addAPIKeyToHeader()
    }


    fun appendDefaultMap(newApiPaths: HashMap<String, JsonObject?>) {
        newApiPaths.map {
            defaultMap[it.key] = it.value
        }
        Log.d(logTag, "appendDefaultMap called")
    }

    fun clearAllExperimentsData() {
        experimentMap.clear()
        defaultMap.clear()
        accessedMap.clear()
        Log.d(logTag, "clearAllExperimentsData called")
    }

    fun clearAllSessionData() {
        customHeaders.clear()
        allHeaders.clear()
        Log.d(logTag, "clearAllSessionData called")
    }

    fun getOnDemandData(
        newApiPaths: HashMap<String, JsonObject?>,
        iExperimentCallback: SoftReference<IExperimentCallback>
    ) {
        val drsExperimentRequest = createExperimentRequest(newApiPaths.keys.toList())
        getRemoteData(drsExperimentRequest, iExperimentCallback)
        Log.d(logTag, "getOnDemandData called with soft reference $iExperimentCallback")
    }

    fun getRemoteWithPreDefinedRequest(iExperimentCallback: SoftReference<IExperimentCallback>) {
        val drsExperimentRequest = createExperimentRequest(defaultMap.keys.toList())
        getRemoteData(drsExperimentRequest, iExperimentCallback)
        Log.d(
            logTag,
            "getRemoteWithPreDefinedRequest called with soft reference $iExperimentCallback"
        )
    }

    private fun createExperimentRequest(experimentKeys: List<String>): DRSExperimentRequest {
        val device = experimentRepository.getDevice()
        val attributes = ExperimentAttributes(
            platform = (device.getDevicePlatform() ?: "android").lowercase(),
            appVersion = device.getAppVersionName() ?: "",
            buildNumber = device.getAppVersionCode().toString()
        )
        return DRSExperimentRequest(
            experimentKeys = experimentKeys,
            attributes = attributes,
            stableId = AscendUser.stableId,
            userId = AscendUser.userId.takeIf { it.isNotEmpty() }
        )
    }


    private fun getRemoteData(
        drsExperimentRequest: DRSExperimentRequest,
        iExperimentCallback: SoftReference<IExperimentCallback>,
    ) {
        Log.d(logTag, "=== getRemoteData START ===")
        Log.d(logTag, "Default map size: ${defaultMap.size}")
        Log.d(logTag, "Default map keys: ${defaultMap.keys}")
        Log.d(logTag, "DRSExperimentRequest experimentKeys: ${drsExperimentRequest.experimentKeys}")
        
        if (defaultMap.isEmpty()) {
            Log.d(logTag, "Default map is empty, returning early")
            iExperimentCallback.get()?.onSuccess()
            return
        }
        val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
            Log.e(logTag, "Coroutine exception in getRemoteData: ${throwable.message}")
            CoroutineScope(Dispatchers.Main).launch {
                iExperimentCallback.get()?.onFailure(throwable)
            }
        }

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            experimentRepository.updateHeaderMaps(customHeaders)
            Log.d(logTag, "Calling experimentRepository.getRemoteData...")
            when (val networkState =
                experimentRepository.getRemoteData(
                    request = drsExperimentRequest,
                    needRawResponse = true
                )
            ) {
                is NetworkState.Success -> {
                    processSuccessData(iExperimentCallback, this, networkState)
                    Log.d(logTag, "getRemoteData NetworkState.Success")
                }

                is NetworkState.Error -> {
                    val errorResponse = processFailureData(networkState)
                    if (errorResponse != null)
                        launch(Dispatchers.Main) {
                            iExperimentCallback.get()
                                ?.onFailure(
                                    Throwable(
                                        errorResponse.error.toString()
                                            ?: "Error Msg Text is Null" //Check in future
                                    )
                                )
                            Log.e(
                                logTag,
                                "getRemoteData NetworkState.Error ${errorResponse.error?.toString() ?: "Error Msg Text is Null"}"
                            )
                        }
                    else
                        launch(Dispatchers.Main) {
                            iExperimentCallback.get()
                                ?.onFailure(Throwable("Failed with code " + networkState.response.raw().code + " & msg " + networkState.response.raw().message))
                            Log.e(
                                logTag,
                                "getRemoteData NetworkState.Error Failed with code ${networkState.response.raw().code}"
                            )
                        }
                }

                is NetworkState.APIException -> {
                    launch(Dispatchers.Main) {
                        iExperimentCallback.get()?.onFailure(Throwable(networkState.throwable))
                        Log.e(logTag, "getRemoteData NetworkState.APIException")
                    }
                }

            }
        }

    }

    private fun processFailureData(networkState: NetworkState.Error<Any>): NetworkError? {
        val errorCode = networkState.response.code()
        if (errorCode == HTTP_NOT_MODIFIED) {
            allHeaders[lastCachedResponseTimestamp] = System.currentTimeMillis()
            return null
        }
        val errorResponse =
            try {
                gson.fromJson(
                    networkState.response.errorBody()?.charStream(),
                    NetworkError::class.java
                )
            } catch (exception: Exception) {
                Log.e(logTag, exception.toString())
                null
            }
        return errorResponse
    }

    private fun processSuccessData(
        iExperimentCallback: SoftReference<IExperimentCallback>,
        coroutineScope: CoroutineScope,
        networkState: NetworkState.Success<Any>
    ) {
        try {
            Log.d(logTag, "=== processSuccessData START ===")
            val data = networkState.data as Response<*>
            Log.d(logTag, "Response code: ${data.code()}")
            Log.d(logTag, "Response isSuccessful: ${data.isSuccessful}")
            
            val responseBody = data.body()
            Log.d(logTag, "Response body is null: ${responseBody == null}")
            
            if (responseBody != null) {
                val responseBodyJson = gson.toJson(responseBody)
                Log.d(logTag, "Response body JSON length: ${responseBodyJson.length}")
                Log.d(logTag, "Response body JSON (first 500 chars): ${responseBodyJson.take(500)}")
            }
            
            if (experimentRepository.realtimeDRSOnForeground()) {
                processHeaders(data)
            }
            
            val response = gson.fromJson(
                gson.toJson(data.body()),
                DRSExperimentResponse::class.java
            )
            Log.d(logTag, "Parsed DRSExperimentResponse: response is null: ${response == null}")
            Log.d(logTag, "Response.data is null: ${response.data == null}")
            Log.d(logTag, "Response.data type: ${response.data?.javaClass?.simpleName}")
            
            // Get experiment details directly from the new format
            val experimentDetails = response.data?.experimentMap ?: emptyList()
            Log.d(logTag, "Experiment details count: ${experimentDetails.size}")
            
            if (experimentDetails.isNotEmpty()) {
                Log.d(logTag, "First experiment apiPath: ${experimentDetails[0].apiPath}")
                Log.d(logTag, "First experiment details: ${gson.toJson(experimentDetails[0])}")
            }

            val map = experimentDetails.associate { experiment ->
                Pair(
                    experiment.apiPath ?: "",
                    experiment
                )
            }
            Log.d(logTag, "Mapped experiments count: ${map.size}")
            Log.d(logTag, "Mapped experiment keys: ${map.keys}")

            Log.d(logTag, "Default map size: ${defaultMap.size}")
            Log.d(logTag, "Default map keys: ${defaultMap.keys}")
            Log.d(logTag, "ExperimentMap size BEFORE adding new experiments: ${experimentMap.size}")

            map.forEach {
                experimentMap[it.key] = it.value
                Log.d("Experiments Data--->", "${it.key} + ${it.value.toString()}")
            }
            Log.d(logTag, "ExperimentMap size AFTER adding new experiments: ${experimentMap.size}")
            Log.d(logTag, "ExperimentMap keys after adding: ${experimentMap.keys}")

            removeUnUsedExperiments()
            Log.d(logTag, "ExperimentMap size AFTER removeUnUsedExperiments: ${experimentMap.size}")
            Log.d(logTag, "ExperimentMap keys after removal: ${experimentMap.keys}")
            
            persistExperimentsData(experimentMap)
            Log.d(logTag, "=== processSuccessData END - Persisted ${experimentMap.size} experiments ===")
            
            coroutineScope.launch(Dispatchers.Main) {
                iExperimentCallback.get()?.onSuccess()
            }
        } catch (exception: Exception) {
            Log.e(logTag, "=== EXCEPTION in processSuccessData ===")
            Log.e(logTag, "Exception type: ${exception.javaClass.simpleName}")
            Log.e(logTag, "Exception message: ${exception.message}")
            Log.e(logTag, "Exception stack trace:")
            exception.printStackTrace()
            Log.e(logTag, "Full exception: ${exception.stackTraceToString()}")
            
            coroutineScope.launch(Dispatchers.Main) {
                iExperimentCallback.get()?.onFailure(Throwable(exception))
            }
        }
    }

    private fun removeUnUsedExperiments() {
        Log.d(logTag, "removeUnUsedExperiments called")
        Log.d(logTag, "ExperimentMap size before removal: ${experimentMap.size}")
        Log.d(logTag, "DefaultMap size: ${defaultMap.size}")
        
        val removedKeys = mutableListOf<String>()
        val iterator = experimentMap.keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            if (!defaultMap.containsKey(key)) {
                removedKeys.add(key)
                iterator.remove()
            }
        }
        
        Log.d(logTag, "Removed ${removedKeys.size} experiments not in defaultMap")
        if (removedKeys.isNotEmpty()) {
            Log.d(logTag, "Removed keys: $removedKeys")
        }
        Log.d(logTag, "ExperimentMap size after removal: ${experimentMap.size}")
    }

    private fun processHeaders(data: Response<*>) {
        try {
            val rawResponse = data.raw().headers
            val lastModifiedTimestampVal = rawResponse[lastModifiedTimestamp]?.let { it }
            val cacheWindowVal = rawResponse["max-age"]?.let { it }?.toInt()
            lastModifiedTimestampVal?.let {
                customHeaders[lastModifiedTimestamp] = it.toLong()
            }
            if (cacheWindowVal != null) {
                allHeaders[cacheWindow] = cacheWindowVal
                allHeaders[lastCachedResponseTimestamp] = System.currentTimeMillis()
            }
        } catch (exception: Exception) {
            Log.e(logTag, exception.toString())
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun updateServerMap(experiments: ConcurrentHashMap<String, ExperimentDetails>) {
        experimentMap = experiments
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun getPersistentMap(): HashMap<String, ExperimentDetails> {
        return HashMap(experimentRepository.getLocalMap())
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    internal fun persistExperimentsData(experiments: ConcurrentHashMap<String, ExperimentDetails>) {
        Log.d(logTag, "=== persistExperimentsData called ===")
        Log.d(logTag, "Experiments map size: ${experiments.size}")
        Log.d(logTag, "Experiments map keys: ${experiments.keys}")
        
        val experimentsJson = gson.toJson(experiments)
        Log.d(logTag, "Experiments JSON length: ${experimentsJson.length}")
        Log.d(logTag, "Experiments JSON (first 500 chars): ${experimentsJson.take(500)}")
        
        experimentRepository.saveLocalString(
            DRS_EXPERIMENTS_PREF_KEY,
            experimentsJson
        )
        Log.d(logTag, "=== persistExperimentsData completed ===")
    }

}
