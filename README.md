# Ascend Android SDK

A powerful Android SDK for experiment management and feature flagging.

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [API](#api)

---

## Installation

### Prerequisites

Before integrating the Ascend Android SDK, ensure your project meets the following requirements:

- **Android API Level**: Minimum SDK 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Java Version**: 11 or higher
- **Kotlin**: 2.0.21 or higher
- **Android Gradle Plugin**: 8.13.0 or higher

### Gradle Setup

#### 1. Add Repository

Add Maven Central to your project's `settings.gradle.kts` (usually already included by default):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // Maven Central is usually included by default
    }
}
```

#### 2. Add Dependency

Add the SDK dependency to your app module's `build.gradle.kts`.  
To ensure you're always using the latest version, check the latest release on  
[Maven Central](https://central.sonatype.com/artifact/org.dreamhorizon/ascend-android-sdk/overview).

```kotlin
dependencies {
    implementation("org.dreamhorizon:ascend-android-sdk:<latest-version>")
}
```

---

## Quick Start

Get started with the Ascend Android SDK in just a few steps.

### Initialize the SDK

```kotlin
import android.app.Application
import android.util.Log
import com.application.ascend_android.*
import com.google.gson.JsonObject

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val httpConfig = HttpConfig(
            apiBaseUrl = "http://localhost:8100", // Replace with your actual API endpoint
            headers = hashMapOf("api-key" to "project-api-key") // Replace with your actual API key
        )
        
        val experimentConfig = ExperimentConfig.Builder(object : IExperimentCallback {
            override fun onSuccess() {
                // Called when experiments are successfully fetched
            }
            override fun onFailure(throwable: Throwable) {
                // Called when experiment fetch fails
            }
        })
            .defaultValues(hashMapOf("button_color" to JsonObject().apply { addProperty("color", "blue") }))
            .shouldFetchOnInit(true) // Fetch experiments immediately on initialization
            .httpConfig(httpConfig)
            .build()
        
        val experimentPluginConfig = PluginConfig(::DRSPlugin, Plugins.EXPERIMENTS.pluginName, experimentConfig)
        val ascendConfig = AscendConfig(httpConfig, arrayListOf(experimentPluginConfig), ClientConfig("your-key")) // Replace with your actual API key
        
        Ascend.init(ascendConfig, this)
        Ascend.user.setUser("user123") // Replace with your actual user ID
    }
}
```

### Use Experiments

```kotlin
val experimentPlugin = Ascend.getPlugin<DRSPlugin>(Plugins.EXPERIMENTS)

val buttonColor = experimentPlugin.getExperimentService()
    .getStringFlag("button_color", "color")

button.setBackgroundColor(Color.parseColor(buttonColor))
```

---

## API

This section documents all APIs for experiments and events. The methods are only available through Ascend. To get all the methods, use the following:

```kotlin
// This returns the object which contains all the methods
val experimentPlugin = Ascend.getPlugin<DRSPlugin>(Plugins.EXPERIMENTS)

// console this object to see what are available methods here.
println(experimentPlugin)
```

### Experiments

| Method | Return Type |
|--------|-------------|
| `getBooleanFlag(experiment_key: String, variable: String)` | `Boolean` |
| `getIntFlag(experiment_key: String, variable: String)` | `Int` |
| `getDoubleFlag(experiment_key: String, variable: String)` | `Double` |
| `getLongFlag(experiment_key: String, variable: String)` | `Long` |
| `getStringFlag(experiment_key: String, variable: String)` | `String` |
| `getAllVariables(experiment_key: String)` | `JsonObject?` |
| `getExperimentVariants()` | `HashMap<String, ExperimentDetails>` |
| `getExperimentsFromStorage()` | `HashMap<String, ExperimentDetails>` |
| `fetchExperiments(map: HashMap<String, JsonObject?>, callback: IExperimentCallback)` | `Unit` |
| `refreshExperiment(callback: IExperimentCallback)` | `Unit` |
| `setExperimentsToStorage(experiments: HashMap<String, ExperimentDetails>)` | `Unit` |

### Method Details

#### 1. getBooleanFlag

Retrieves a boolean value from an experiment.

**Parameters:**
- `experiment_key`: The experiment identifier (e.g., "feature_toggle")
- `variable`: Variable name within the experiment (default: "value")
- `dontCache`: If true, doesn't cache the result in memory
- `ignoreCache`: If true, bypasses the cache and fetches fresh

**Returns:** Boolean value with fallback to `false`

**Fallback Order:**
1. Accessed cache (if `ignoreCache = false`)
2. Experiment map (fetched from server)
3. Default map (provided at initialization)
4. Hard-coded `false`

---

#### 2. getIntFlag

Retrieves an integer value from an experiment.

**Parameters:**
- `experiment_key`: The experiment identifier (e.g., "retry_config")
- `variable`: Variable name within the experiment (default: "max_attempts")
- `dontCache`: If true, doesn't cache the result in memory
- `ignoreCache`: If true, bypasses the cache and fetches fresh

**Returns:** Integer value with fallback to `-1`

**Fallback Order:**
1. Accessed cache (if `ignoreCache = false`)
2. Experiment map (fetched from server)
3. Default map (provided at initialization)
4. Hard-coded `-1`

---

#### 3. getDoubleFlag

Retrieves a double value from an experiment.

**Parameters:**
- `experiment_key`: The experiment identifier (e.g., "network_config")
- `variable`: Variable name within the experiment (default: "timeout")
- `dontCache`: If true, doesn't cache the result in memory
- `ignoreCache`: If true, bypasses the cache and fetches fresh

**Returns:** Double value with fallback to `-1.0`

**Fallback Order:**
1. Accessed cache (if `ignoreCache = false`)
2. Experiment map (fetched from server)
3. Default map (provided at initialization)
4. Hard-coded `-1.0`

---

#### 4. getLongFlag

Retrieves a long value from an experiment.

**Parameters:**
- `experiment_key`: The experiment identifier (e.g., "cache_config")
- `variable`: Variable name within the experiment (default: "ttl")
- `dontCache`: If true, doesn't cache the result in memory
- `ignoreCache`: If true, bypasses the cache and fetches fresh

**Returns:** Long value with fallback to `-1L`

**Fallback Order:**
1. Accessed cache (if `ignoreCache = false`)
2. Experiment map (fetched from server)
3. Default map (provided at initialization)
4. Hard-coded `-1L`

---

#### 5. getStringFlag

Retrieves a string value from an experiment.

**Parameters:**
- `experiment_key`: The experiment identifier (e.g., "button_exp_test")
- `variable`: Variable name within the experiment (default: "color")
- `dontCache`: If true, doesn't cache the result in memory
- `ignoreCache`: If true, bypasses the cache and fetches fresh

**Returns:** String value with fallback to `""` (empty string)

**Fallback Order:**
1. Accessed cache (if `ignoreCache = false`)
2. Experiment map (fetched from server)
3. Default map (provided at initialization)
4. Hard-coded `""`

---

#### 6. getAllVariables

Retrieves all variables for a specific experiment as a JsonObject.

**Parameters:**
- `experiment_key`: The experiment identifier

**Returns:** `JsonObject?` containing all variables, or default values if not found

**Fallback Order:**
1. Experiment map (fetched from server)
2. Default map (provided at initialization)
3. Hard-coded `null`

---

#### 7. fetchExperiments

Fetches experiments from the server on-demand.

**Parameters:**
- `map`: HashMap of experiment keys with their default values
- `callback`: Callback interface for success/failure handling

**Behavior:**
- Appends the provided default values to the default map
- Triggers network requests to fetch the experiments via `getOnDemandData(newKeys, callback) -> getRemoteData(request, callback)`

**getRemoteData Details:**
- If `defaultMap` is empty, immediately calls `onSuccess()` and returns
- Adds custom headers to the HTTP request (last-modified, user-id)
- On API success:
  - Updates headers if `shouldRefreshDRSOnForeground` is `true` in config
  - Updates `experimentMap`
  - Removes unused experiments (not in `defaultMap`)
  - Persists `experimentMap` to SharedPreferences
  - Calls `onSuccess()` or `onFailure()` on the callback

---

#### 8. refreshExperiment

Fetches experiments using predefined keys in the default map.

**Parameters:**
- `callback`: Callback interface for success/failure handling

**Behavior:**
- Builds a `DRSExperimentRequest` from the keys of the `defaultMap`
- Triggers network request via `getRemoteWithPreDefinedRequest(callback) -> getRemoteData(request, callback)`

**getRemoteData Details:**
- If `defaultMap` is empty, immediately calls `onSuccess()` and returns
- Updates HTTP headers (adds guest-id/user-id/custom headers via `updateHeaderMaps`)
- Makes the API call (`RequestType.FETCH_EXPERIMENTS` → `IApi.getDRSExperiments`)
- On success:
  - If `shouldRefreshDRSOnForeground` is `true`, updates caching headers (cache window/last modified) from the response
  - Parses response to `ExperimentDetails` list and updates `experimentMap`
  - Removes experiments not present in `defaultMap`
  - Persists `experimentMap` to SharedPreferences
  - Calls `onSuccess()` on the callback (Main thread)
- On error or exception:
  - Processes error (including 304 handling) and calls `onFailure()` with the appropriate `Throwable` (Main thread)

---

#### 9. getExperimentVariants

Returns a copy of all currently loaded experiment variants.

**Parameters:** None

**Returns:** `HashMap<String, ExperimentDetails>` where:
- **Key**: `experiment_key` (experiment identifier)
- **Value**: `ExperimentDetails` object containing:
  - `experimentId`: Unique experiment identifier
  - `experiment_key`: Experiment endpoint path
  - `variantName`: Name of the assigned variant
  - `variables`: `JsonObject` containing all experiment variables/values

**Behavior:**
- Creates a new `HashMap` copy of the mediator's `experimentMap`
- No network calls - purely returns cached data
- Data source: `experimentMap` populated from:
  - Initial load from SharedPreferences (on startup)
  - Updates from successful API responses (`refreshExperiment`/`fetchExperiments`)
- Returns empty `HashMap` if no experiments have been loaded yet

---

#### 10. setExperimentsToStorage

Persists experiment data to local storage and updates the in-memory cache.

**Parameters:**
- `experiments`: `HashMap<String, ExperimentDetails>` containing experiment data to persist

**Behavior:**
- Converts the provided `HashMap` to a `ConcurrentHashMap` for thread safety
- Persists the experiments data to SharedPreferences via the mediator
- Updates the mediator's `experimentMap` with the new data
- No network calls - purely local storage operation
- Used for manually setting experiment data (e.g., from external sources or testing)

**Notes:**
- No return value (void method)
- No fallback mechanism - if persistence fails, the error is logged but not propagated
- Immediate effect - data is immediately available in memory after calling this method
- Storage failure handling - if SharedPreferences write fails, the in-memory map is still updated, so experiments remain available for the current session

---

#### 11. getExperimentsFromStorage

Retrieves experiment data from local storage (SharedPreferences).

**Parameters:** None

**Returns:** `HashMap<String, ExperimentDetails>` - the persisted experiment data

**Behavior:**
- Reads from SharedPreferences using the `DRS_EXPERIMENTS_PREF_KEY`
- No network calls - purely local storage operation
- Used to retrieve previously saved experiment data

**Fallback Order:**
- If SharedPreferences is empty or corrupted, returns empty `HashMap`
- If JSON deserialization fails, returns empty `HashMap`
- No exception thrown - gracefully handles storage issues
- Data source priority: Only reads from local storage, does not check in-memory cache
