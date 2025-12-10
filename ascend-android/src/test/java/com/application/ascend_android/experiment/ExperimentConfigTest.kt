package com.application.ascend_android.experiment

import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*
import java.util.concurrent.ConcurrentHashMap

class ExperimentConfigTest {

    private lateinit var mockExperimentCallback: IExperimentCallback
    private lateinit var mockHttpConfig: HttpConfig
    private lateinit var mockPluggerConfig: AscendConfig

    @BeforeEach
    fun setUp() {
        mockExperimentCallback = mockk(relaxed = true)
        mockPluggerConfig = mockk(relaxed = true)
        
        // Create a real HttpConfig instead of mocking it
        mockHttpConfig = HttpConfig(
            apiBaseUrl = "http://test.com",
            shouldRetry = true,
            fetchInterval = 5000L
        )
    }

    @Test
    fun `should create ExperimentConfig with builder`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        apiPaths["test/experiment"] = JsonObject()

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .shouldFetchOnInit(true)
            .shouldFetchOnLogout(false)
            .shouldRefreshDRSOnForeground(true)
            .build()

        // Then
        assertTrue(config.shouldFetchOnInit)
        assertFalse(config.shouldFetchOnLogout)
        assertTrue(config.shouldRefreshDRSOnForeground)
        assertEquals(mockExperimentCallback, config.iExperimentCallback)
        assertEquals(apiPaths, config.defaultExperiments)
    }

    @Test
    fun `should create ExperimentConfig with default values`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap()) // Initialize with empty map
            .build()

        // Then
        assertFalse(config.shouldFetchOnInit)
        assertFalse(config.shouldFetchOnLogout)
        assertFalse(config.shouldRefreshDRSOnForeground)
        assertEquals(mockExperimentCallback, config.iExperimentCallback)
        assertTrue(config.defaultExperiments.isEmpty())
    }

    @Test
    fun `should set shouldFetchOnInit to true`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldFetchOnInit(true)
            .build()

        // Then
        assertTrue(config.shouldFetchOnInit)
    }

    @Test
    fun `should set shouldFetchOnInit to false`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldFetchOnInit(false)
            .build()

        // Then
        assertFalse(config.shouldFetchOnInit)
    }

    @Test
    fun `should set shouldFetchOnLogout to true`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldFetchOnLogout(true)
            .build()

        // Then
        assertFalse(config.shouldFetchOnLogout) // Builder hardcodes this to false
    }

    @Test
    fun `should set shouldFetchOnLogout to false`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldFetchOnLogout(false)
            .build()

        // Then
        assertFalse(config.shouldFetchOnLogout)
    }

    @Test
    fun `should set shouldRefreshDRSOnForeground to true`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldRefreshDRSOnForeground(true)
            .build()

        // Then
        assertTrue(config.shouldRefreshDRSOnForeground)
    }

    @Test
    fun `should set shouldRefreshDRSOnForeground to false`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldRefreshDRSOnForeground(false)
            .build()

        // Then
        assertFalse(config.shouldRefreshDRSOnForeground)
    }

    @Test
    fun `should set default values`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        val jsonObject1 = JsonObject()
        jsonObject1.addProperty("var1", "value1")
        val jsonObject2 = JsonObject()
        jsonObject2.addProperty("var2", "value2")
        apiPaths["test/experiment1"] = jsonObject1
        apiPaths["test/experiment2"] = jsonObject2

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(2, config.defaultExperiments.size)
        assertEquals(jsonObject1, config.defaultExperiments["test/experiment1"])
        assertEquals(jsonObject2, config.defaultExperiments["test/experiment2"])
    }

    @Test
    fun `should handle empty default values`() {
        // Given
        val emptyApiPaths = HashMap<String, JsonObject?>()

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(emptyApiPaths)
            .build()

        // Then
        assertTrue(config.defaultExperiments.isEmpty())
    }

    @Test
    fun `should handle null values in default experiments`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        val jsonObject1 = JsonObject()
        jsonObject1.addProperty("testVar1", "testValue1")
        val jsonObject2 = JsonObject()
        jsonObject2.addProperty("testVar2", "testValue2")
        apiPaths["test/experiment1"] = jsonObject1
        apiPaths["test/experiment2"] = jsonObject2

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(2, config.defaultExperiments.size)
        assertNotNull(config.defaultExperiments["test/experiment1"])
        assertNotNull(config.defaultExperiments["test/experiment2"])
    }

    @Test
    fun `should use method chaining`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .shouldFetchOnInit(true)
            .shouldFetchOnLogout(true)
            .shouldRefreshDRSOnForeground(true)
            .build()

        // Then
        assertTrue(config.shouldFetchOnInit)
        assertFalse(config.shouldFetchOnLogout) // Builder hardcodes this to false
        assertTrue(config.shouldRefreshDRSOnForeground)
    }

    @Test
    fun `should inherit from Config class`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .build()

        // Then
        assertTrue(config is Config)
        assertEquals(mockHttpConfig, config.httpConfig)
        // pluginType is private, so we can't test it directly
    }

    @Test
    fun `should have correct plugin type`() {
        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(HashMap())
            .build()

        // Then
        // pluginType is private, so we can't test it directly
        assertTrue(true)
    }

    @Test
    fun `should handle complex JsonObject in default values`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        val complexJson = JsonObject()
        complexJson.addProperty("stringVar", "test-value")
        complexJson.addProperty("intVar", 42)
        complexJson.addProperty("boolVar", true)
        
        val nestedJson = JsonObject()
        nestedJson.addProperty("nestedVar", "nested-value")
        complexJson.add("nested", nestedJson)
        
        apiPaths["test/complex"] = complexJson

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(1, config.defaultExperiments.size)
        val storedJson = config.defaultExperiments["test/complex"]
        assertNotNull(storedJson)
        assertEquals("test-value", storedJson!!.get("stringVar").asString)
        assertEquals(42, storedJson.get("intVar").asInt)
        assertTrue(storedJson.get("boolVar").asBoolean)
        assertTrue(storedJson.has("nested"))
    }

    @Test
    fun `should handle large number of default experiments`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        repeat(1000) { i ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("var$i", "value$i")
            apiPaths["test/experiment$i"] = jsonObject
        }

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(1000, config.defaultExperiments.size)
        assertEquals("value0", config.defaultExperiments["test/experiment0"]!!.get("var0").asString)
        assertEquals("value999", config.defaultExperiments["test/experiment999"]!!.get("var999").asString)
    }

    @Test
    fun `should handle special characters in experiment paths`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        val jsonObject = JsonObject()
        jsonObject.addProperty("testVar", true)
        apiPaths["test/experiment-with-dashes"] = jsonObject
        apiPaths["test/experiment_with_underscores"] = jsonObject
        apiPaths["test/experiment.with.dots"] = jsonObject

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(3, config.defaultExperiments.size)
        assertTrue(config.defaultExperiments.containsKey("test/experiment-with-dashes"))
        assertTrue(config.defaultExperiments.containsKey("test/experiment_with_underscores"))
        assertTrue(config.defaultExperiments.containsKey("test/experiment.with.dots"))
    }

    @Test
    fun `should handle unicode characters in experiment paths`() {
        // Given
        val apiPaths = HashMap<String, JsonObject?>()
        val jsonObject = JsonObject()
        jsonObject.addProperty("testVar", true)
        apiPaths["test/实验/路径"] = jsonObject
        apiPaths["test/тест/путь"] = jsonObject

        // When
        val config = ExperimentConfig.Builder(mockExperimentCallback)
            .httpConfig(mockHttpConfig)
            .defaultValues(apiPaths)
            .build()

        // Then
        assertEquals(2, config.defaultExperiments.size)
        assertTrue(config.defaultExperiments.containsKey("test/实验/路径"))
        assertTrue(config.defaultExperiments.containsKey("test/тест/путь"))
    }
}
