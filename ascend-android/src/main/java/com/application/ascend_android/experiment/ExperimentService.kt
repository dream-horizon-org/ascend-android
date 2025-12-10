package com.application.ascend_android

import android.util.Log
import com.google.gson.JsonObject
import java.lang.ref.SoftReference
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


internal class DRSExperimentService @Inject constructor(private val experimentMediator: ExperimentMediator) :
    BaseExperimentService() {
    private val logTag = "ExperimentService"

    override fun getBooleanFlag(
        apiPath: String,
        variable: String,
        dontCache: Boolean,
        ignoreCache: Boolean
    ): Boolean {
        try {
            val experiment = experimentMediator.experimentMap[apiPath]
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            
            // Use unified type-safe retrieval with full fallback chain
            val finalValue = if (!ignoreCache) {
                // Step 1: Check accessedMap (cached values) first
                TypeSafetyUtils.getCachedValue(
                    accessedMap = currentValueMap,
                    variableKey = variable,
                    expectedType = DataType.BOOLEAN,
                    defaultValue = TypeSafetyUtils.DefaultValues.BOOLEAN,
                    logTag = logTag,
                    methodName = "getBooleanFlag"
                ) ?: run {
                    // Steps 2-4: Check experimentMap -> defaultMap -> fallback
                    TypeSafetyUtils.getTypeSafeValue(
                        experiment = experiment,
                        defaultMap = experimentMediator.defaultMap[apiPath],
                        variableKey = variable,
                        expectedType = DataType.BOOLEAN,
                        defaultValue = TypeSafetyUtils.DefaultValues.BOOLEAN,
                        logTag = logTag,
                        methodName = "getBooleanFlag"
                    )
                }
            } else {
                // Steps 2-4: Check experimentMap -> defaultMap -> fallback (ignoreCache = true)
                TypeSafetyUtils.getTypeSafeValue(
                    experiment = experiment,
                    defaultMap = experimentMediator.defaultMap[apiPath],
                    variableKey = variable,
                    expectedType = DataType.BOOLEAN,
                    defaultValue = TypeSafetyUtils.DefaultValues.BOOLEAN,
                    logTag = logTag,
                    methodName = "getBooleanFlag"
                )
            }
            
            if (!dontCache) {
                setAccessMap(
                    apiPath = apiPath,
                    variable = variable,
                    currentValueMap,
                    finalValue
                )
            }
            return finalValue
        } catch (e: Exception) {
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            if (!dontCache) {
                setAccessMap(apiPath, variable, currentValueMap, TypeSafetyUtils.DefaultValues.BOOLEAN)
            }
            Log.e(logTag, "getBooleanFlag error: ${e.message}", e)
            return TypeSafetyUtils.DefaultValues.BOOLEAN
        }
    }


    override fun getIntFlag(
        apiPath: String,
        variable: String,
        dontCache: Boolean,
        ignoreCache: Boolean
    ): Int {
        try {
            val experiment = experimentMediator.experimentMap[apiPath]
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            
            // Use unified type-safe retrieval with full fallback chain
            val finalValue = if (!ignoreCache) {
                // Step 1: Check accessedMap (cached values) first
                TypeSafetyUtils.getCachedValue(
                    accessedMap = currentValueMap,
                    variableKey = variable,
                    expectedType = DataType.INT,
                    defaultValue = TypeSafetyUtils.DefaultValues.INT,
                    logTag = logTag,
                    methodName = "getIntFlag"
                ) ?: run {
                    // Steps 2-4: Check experimentMap -> defaultMap -> fallback
                    TypeSafetyUtils.getTypeSafeValue(
                        experiment = experiment,
                        defaultMap = experimentMediator.defaultMap[apiPath],
                        variableKey = variable,
                        expectedType = DataType.INT,
                        defaultValue = TypeSafetyUtils.DefaultValues.INT,
                        logTag = logTag,
                        methodName = "getIntFlag"
                    )
                }
            } else {
                // Steps 2-4: Check experimentMap -> defaultMap -> fallback (ignoreCache = true)
                TypeSafetyUtils.getTypeSafeValue(
                    experiment = experiment,
                    defaultMap = experimentMediator.defaultMap[apiPath],
                    variableKey = variable,
                    expectedType = DataType.INT,
                    defaultValue = TypeSafetyUtils.DefaultValues.INT,
                    logTag = logTag,
                    methodName = "getIntFlag"
                )
            }
            
            if (!dontCache) {
                setAccessMap(
                    apiPath = apiPath,
                    variable = variable,
                    currentValueMap,
                    finalValue
                )
            }
            return finalValue
        } catch (e: Exception) {
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            if (!dontCache) {
                setAccessMap(apiPath = apiPath, variable = variable, currentValueMap, TypeSafetyUtils.DefaultValues.INT)
            }
            Log.e(logTag, "getIntFlag error: ${e.message}", e)
            return TypeSafetyUtils.DefaultValues.INT
        }
    }

    override fun getDoubleFlag(
        apiPath: String,
        variable: String,
        dontCache: Boolean,
        ignoreCache: Boolean
    ): Double {
        try {
            val experiment = experimentMediator.experimentMap[apiPath]
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            
            // Use unified type-safe retrieval with full fallback chain
            val finalValue = if (!ignoreCache) {
                // Step 1: Check accessedMap (cached values) first
                TypeSafetyUtils.getCachedValue(
                    accessedMap = currentValueMap,
                    variableKey = variable,
                    expectedType = DataType.DOUBLE,
                    defaultValue = TypeSafetyUtils.DefaultValues.DOUBLE,
                    logTag = logTag,
                    methodName = "getDoubleFlag"
                ) ?: run {
                    // Steps 2-4: Check experimentMap -> defaultMap -> fallback
                    TypeSafetyUtils.getTypeSafeValue(
                        experiment = experiment,
                        defaultMap = experimentMediator.defaultMap[apiPath],
                        variableKey = variable,
                        expectedType = DataType.DOUBLE,
                        defaultValue = TypeSafetyUtils.DefaultValues.DOUBLE,
                        logTag = logTag,
                        methodName = "getDoubleFlag"
                    )
                }
            } else {
                // Steps 2-4: Check experimentMap -> defaultMap -> fallback (ignoreCache = true)
                TypeSafetyUtils.getTypeSafeValue(
                    experiment = experiment,
                    defaultMap = experimentMediator.defaultMap[apiPath],
                    variableKey = variable,
                    expectedType = DataType.DOUBLE,
                    defaultValue = TypeSafetyUtils.DefaultValues.DOUBLE,
                    logTag = logTag,
                    methodName = "getDoubleFlag"
                )
            }
            
            if (!dontCache) {
                setAccessMap(
                    apiPath = apiPath,
                    variable = variable,
                    currentValueMap,
                    finalValue
                )
            }
            return finalValue
        } catch (e: Exception) {
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()

            if (!dontCache) {
                setAccessMap(apiPath = apiPath, variable = variable, currentValueMap, TypeSafetyUtils.DefaultValues.DOUBLE)
            }
            Log.e(logTag, "getDoubleFlag error: ${e.message}", e)
            return TypeSafetyUtils.DefaultValues.DOUBLE
        }
    }

    override fun getLongFlag(
        apiPath: String,
        variable: String,
        dontCache: Boolean,
        ignoreCache: Boolean
    ): Long {
        try {
            val experiment = experimentMediator.experimentMap[apiPath]
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            
            // Use unified type-safe retrieval with full fallback chain
            val finalValue = if (!ignoreCache) {
                // Step 1: Check accessedMap (cached values) first
                TypeSafetyUtils.getCachedValue(
                    accessedMap = currentValueMap,
                    variableKey = variable,
                    expectedType = DataType.LONG,
                    defaultValue = TypeSafetyUtils.DefaultValues.LONG,
                    logTag = logTag,
                    methodName = "getLongFlag"
                ) ?: run {
                    // Steps 2-4: Check experimentMap -> defaultMap -> fallback
                    TypeSafetyUtils.getTypeSafeValue(
                        experiment = experiment,
                        defaultMap = experimentMediator.defaultMap[apiPath],
                        variableKey = variable,
                        expectedType = DataType.LONG,
                        defaultValue = TypeSafetyUtils.DefaultValues.LONG,
                        logTag = logTag,
                        methodName = "getLongFlag"
                    )
                }
            } else {
                // Steps 2-4: Check experimentMap -> defaultMap -> fallback (ignoreCache = true)
                TypeSafetyUtils.getTypeSafeValue(
                    experiment = experiment,
                    defaultMap = experimentMediator.defaultMap[apiPath],
                    variableKey = variable,
                    expectedType = DataType.LONG,
                    defaultValue = TypeSafetyUtils.DefaultValues.LONG,
                    logTag = logTag,
                    methodName = "getLongFlag"
                )
            }
            
            if (!dontCache) {
                setAccessMap(
                    apiPath = apiPath,
                    variable = variable,
                    currentValueMap,
                    finalValue
                )
            }

            return finalValue
        } catch (e: Exception) {
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            if (!dontCache) {
                setAccessMap(apiPath = apiPath, variable = variable, currentValueMap, TypeSafetyUtils.DefaultValues.LONG)
            }
            Log.e(logTag, "getLongFlag error: ${e.message}", e)
            return TypeSafetyUtils.DefaultValues.LONG
        }

    }

    override fun getStringFlag(
        apiPath: String,
        variable: String,
        dontCache: Boolean,
        ignoreCache: Boolean
    ): String {
        try {
            val experiment = experimentMediator.experimentMap[apiPath]
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            
            // Use unified type-safe retrieval with full fallback chain
            val finalValue = if (!ignoreCache) {
                // Step 1: Check accessedMap (cached values) first
                TypeSafetyUtils.getCachedValue(
                    accessedMap = currentValueMap,
                    variableKey = variable,
                    expectedType = DataType.STRING,
                    defaultValue = TypeSafetyUtils.DefaultValues.STRING,
                    logTag = logTag,
                    methodName = "getStringFlag"
                ) ?: run {
                    // Steps 2-4: Check experimentMap -> defaultMap -> fallback
                    TypeSafetyUtils.getTypeSafeValue(
                        experiment = experiment,
                        defaultMap = experimentMediator.defaultMap[apiPath],
                        variableKey = variable,
                        expectedType = DataType.STRING,
                        defaultValue = TypeSafetyUtils.DefaultValues.STRING,
                        logTag = logTag,
                        methodName = "getStringFlag"
                    )
                }
            } else {
                // Steps 2-4: Check experimentMap -> defaultMap -> fallback (ignoreCache = true)
                TypeSafetyUtils.getTypeSafeValue(
                    experiment = experiment,
                    defaultMap = experimentMediator.defaultMap[apiPath],
                    variableKey = variable,
                    expectedType = DataType.STRING,
                    defaultValue = TypeSafetyUtils.DefaultValues.STRING,
                    logTag = logTag,
                    methodName = "getStringFlag"
                )
            }
            
            if (!dontCache) {
                setAccessMap(
                    apiPath = apiPath,
                    variable = variable,
                    currentValueMap,
                    finalValue
                )
            }
            return finalValue
        } catch (e: Exception) {
            val currentValueMap = experimentMediator.accessedMap[apiPath] ?: ConcurrentHashMap()
            if (!dontCache) {
                setAccessMap(apiPath = apiPath, variable = variable, currentValueMap, TypeSafetyUtils.DefaultValues.STRING)
            }
            Log.e(logTag, "getStringFlag error: ${e.message}", e)
            return TypeSafetyUtils.DefaultValues.STRING
        }
    }

    override fun getAllVariables(apiPath: String): JsonObject? {
        val experiment = experimentMediator.experimentMap[apiPath]
        try {
            experiment?.variables?.let {
                Log.d(logTag, "getAllVariables: returning all variables for $apiPath")
                return it
            } ?: run {
                Log.d(logTag, "getAllVariables: returning default variables for $apiPath")
                return experimentMediator.defaultMap[apiPath]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(logTag, e.toString())
        }
        return null
    }

    override fun fetchExperiments(
        map: HashMap<String, JsonObject?>,
        callback: IExperimentCallback
    ) {
        experimentMediator.appendDefaultMap(map)
        experimentMediator.getOnDemandData(map, SoftReference(callback))
        Log.i(logTag, "fetchExperiments called")
    }

    override fun refreshExperiment(callback: IExperimentCallback) {
        experimentMediator.getRemoteWithPreDefinedRequest(SoftReference(callback))
        Log.i(logTag, "refreshExperiments called")
    }

    override fun getExperimentVariants(): HashMap<String, ExperimentDetails> {
        Log.i(logTag, "getExperimentVariants called")
        return HashMap(experimentMediator.experimentMap)
    }

    override fun setExperimentsToStorage(experiments: HashMap<String, ExperimentDetails>) {
        Log.i(logTag, "persistExperimentsData called")
        val concurrentExperiments = ConcurrentHashMap(experiments)
        experimentMediator.persistExperimentsData(concurrentExperiments)
        experimentMediator.updateServerMap(concurrentExperiments)
    }

    override fun getExperimentsFromStorage(): HashMap<String, ExperimentDetails> {
        Log.i(
            logTag,
            "getExperimentFromStorage called with size" + experimentMediator.getPersistentMap().size
        )
        return experimentMediator.getPersistentMap()
    }

    override fun clearAllExperimentsData() {
        Log.i(logTag, "clearAllExperimentsData called")
        experimentMediator.clearAllExperimentsData()
    }

    override fun clearUserSessionData() {
        Log.i(logTag, "clearUserSessionData called")
        experimentMediator.clearAllSessionData()
    }

    private fun setAccessMap(
        apiPath: String,
        variable: String,
        currentValueMap: ConcurrentHashMap<String, Any>,
        value: Any
    ) {
        currentValueMap[variable] = value
        experimentMediator.accessedMap[apiPath] = currentValueMap
    }
}