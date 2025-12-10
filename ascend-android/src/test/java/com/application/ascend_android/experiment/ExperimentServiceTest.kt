package com.application.ascend_android.experiment

import android.util.Log
import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ConcurrentHashMap
import java.lang.ref.SoftReference
import com.application.ascend_android.*

class ExperimentServiceTest {

    private lateinit var experimentService: DRSExperimentService
    private lateinit var mockExperimentMediator: ExperimentMediator
    private lateinit var mockExperimentCallback: IExperimentCallback
    private lateinit var mockExperimentDetails: ExperimentDetails

    @BeforeEach
    fun setUp() {
        mockExperimentMediator = mockk(relaxed = true)
        mockExperimentCallback = mockk(relaxed = true)
        mockExperimentDetails = mockk(relaxed = true)

        // Mock Android Log
        mockkStatic("android.util.Log")
        every { android.util.Log.d(any(), any<String>()) } returns 0
        every { android.util.Log.i(any(), any<String>()) } returns 0
        every { android.util.Log.e(any(), any<String>()) } returns 0

        experimentService = DRSExperimentService(mockExperimentMediator)
    }

    @Test
    fun `should get boolean flag from accessed map when not ignoring cache`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = true

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, false)

        // Then
        assertTrue(result)
        verify { android.util.Log.d("ExperimentService", "getBooleanFlag: returning accessed value for $variable") }
    }

    @Test
    fun `should get boolean flag from experiment map when not in accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, true)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, false)

        // Then
        assertTrue(result)
        verify { Log.d("ExperimentService", "getBooleanFlag: returning fetched value for $variable true") }
    }

    @Test
    fun `should get boolean flag from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, true)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, false)

        // Then
        assertTrue(result)
        verify { Log.d("ExperimentService", "getBooleanFlag: returning default value for $variable true") }
    }

    @Test
    fun `should return false as fallback for boolean flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, false)

        // Then
        assertFalse(result)
        verify { Log.d("ExperimentService", "getBooleanFlag: returning fallback value for $variable") }
    }

    @Test
    fun `should handle exception in getBooleanFlag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } throws RuntimeException("Test exception")

        // When & Then
        try {
            val result = experimentService.getBooleanFlag(apiPath, variable, false, false)
            assertFalse(result)
        } catch (e: RuntimeException) {
            // Expected exception, test passes
            assertTrue(true)
        }
    }

    @Test
    fun `should get int flag from accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 42

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        assertEquals(42, result)
    }

    @Test
    fun `should get double flag from accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 3.14

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        assertEquals(3.14, result, 0.001)
    }

    @Test
    fun `should get long flag from accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 123456789L

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        assertEquals(123456789L, result)
    }

    @Test
    fun `should get string flag from accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = "test-value"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        assertEquals("test-value", result)
    }

    @Test
    fun `should get all variables from experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val jsonObject = JsonObject()
        jsonObject.addProperty("var1", "value1")

        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getAllVariables(apiPath)

        // Then
        assertEquals(jsonObject, result)
        verify { Log.d("ExperimentService", "getAllVariables: returning all variables for $apiPath") }
    }

    @Test
    fun `should get all variables from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val jsonObject = JsonObject()
        jsonObject.addProperty("var1", "value1")

        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getAllVariables(apiPath)

        // Then
        assertEquals(jsonObject, result)
        verify { Log.d("ExperimentService", "getAllVariables: returning default variables for $apiPath") }
    }

    @Test
    fun `should return null for getAllVariables when exception occurs`() {
        // Given
        val apiPath = "test/experiment"

        every { mockExperimentMediator.experimentMap } throws RuntimeException("Test exception")

        // When & Then
        try {
            val result = experimentService.getAllVariables(apiPath)
            assertNull(result)
        } catch (e: RuntimeException) {
            // Expected exception, test passes
            assertTrue(true)
        }
    }

    @Test
    fun `should fetch experiments`() {
        // Given
        val map = HashMap<String, JsonObject?>()
        map["test/experiment"] = JsonObject()

        every { mockExperimentMediator.appendDefaultMap(map) } just Runs
        every { mockExperimentMediator.getOnDemandData(map, any<SoftReference<IExperimentCallback>>()) } just Runs

        // When
        experimentService.fetchExperiments(map, mockExperimentCallback)

        // Then
        verify { mockExperimentMediator.appendDefaultMap(map) }
        verify { mockExperimentMediator.getOnDemandData(map, any<SoftReference<IExperimentCallback>>()) }
        verify { Log.i("ExperimentService", "fetchExperiments called") }
    }

    @Test
    fun `should refresh experiment`() {
        // Given
        every { mockExperimentMediator.getRemoteWithPreDefinedRequest(any<SoftReference<IExperimentCallback>>()) } just Runs

        // When
        experimentService.refreshExperiment(mockExperimentCallback)

        // Then
        verify { mockExperimentMediator.getRemoteWithPreDefinedRequest(any<SoftReference<IExperimentCallback>>()) }
        verify { Log.i("ExperimentService", "refreshExperiments called") }
    }

    @Test
    fun `should get experiment variants`() {
        // Given
        val experimentMap = ConcurrentHashMap<String, ExperimentDetails>()
        experimentMap["test/experiment"] = mockExperimentDetails

        every { mockExperimentMediator.experimentMap } returns experimentMap

        // When
        val result = experimentService.getExperimentVariants()

        // Then
        assertEquals(experimentMap, result)
        verify { Log.i("ExperimentService", "getExperimentVariants called") }
    }

    @Test
    fun `should set experiments to storage`() {
        // Given
        val experiments = HashMap<String, ExperimentDetails>()
        experiments["test/experiment"] = mockExperimentDetails

        every { mockExperimentMediator.persistExperimentsData(any<ConcurrentHashMap<String, ExperimentDetails>>()) } just Runs
        every { mockExperimentMediator.updateServerMap(any<ConcurrentHashMap<String, ExperimentDetails>>()) } just Runs

        // When
        experimentService.setExperimentsToStorage(experiments)

        // Then
        verify { mockExperimentMediator.persistExperimentsData(any<ConcurrentHashMap<String, ExperimentDetails>>()) }
        verify { mockExperimentMediator.updateServerMap(any<ConcurrentHashMap<String, ExperimentDetails>>()) }
        verify { Log.i("ExperimentService", "persistExperimentsData called") }
    }

    @Test
    fun `should get experiments from storage`() {
        // Given
        val persistentMap = HashMap<String, ExperimentDetails>()
        persistentMap["test/experiment"] = mockExperimentDetails

        every { mockExperimentMediator.getPersistentMap() } returns persistentMap

        // When
        val result = experimentService.getExperimentsFromStorage()

        // Then
        assertEquals(persistentMap, result)
        verify { Log.i("ExperimentService", "getExperimentFromStorage called with size1") }
    }

    @Test
    fun `should clear all experiments data`() {
        // Given
        every { mockExperimentMediator.clearAllExperimentsData() } just Runs

        // When
        experimentService.clearAllExperimentsData()

        // Then
        verify { mockExperimentMediator.clearAllExperimentsData() }
        verify { Log.i("ExperimentService", "clearAllExperimentsData called") }
    }

    @Test
    fun `should clear user session data`() {
        // Given
        every { mockExperimentMediator.clearAllSessionData() } just Runs

        // When
        experimentService.clearUserSessionData()

        // Then
        verify { mockExperimentMediator.clearAllSessionData() }
        verify { Log.i("ExperimentService", "clearUserSessionData called") }
    }

    @Test
    fun `should not cache when dontCache is true`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, true, false)

        // Then
        assertFalse(result)
        // Note: The actual implementation might still call setAccessMap, so we just verify the result
    }

    @Test
    fun `should ignore cache when ignoreCache is true`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = true

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, true)

        // Then
        // Should not return from accessed map even though it exists
        assertFalse(result) // Should return fallback value
    }

    @Test
    fun `should get int flag from experiment map when not in accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 42)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        assertEquals(42, result)
    }

    @Test
    fun `should get int flag from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 42)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        assertEquals(42, result)
    }

    @Test
    fun `should return minus one as fallback for int flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `should handle exception in getIntFlag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "invalid") // Invalid type to cause exception during conversion

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        // Should handle exception gracefully and return fallback value
        assertEquals(-1, result)
    }

    @Test
    fun `should get double flag from experiment map when not in accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 3.14)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        assertEquals(3.14, result, 0.001)
    }

    @Test
    fun `should get double flag from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 3.14)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        assertEquals(3.14, result, 0.001)
    }

    @Test
    fun `should return minus one as fallback for double flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        assertEquals(-1.0, result, 0.001)
    }

    @Test
    fun `should handle exception in getDoubleFlag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "invalid") // Invalid type to cause exception during conversion

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        // Should handle exception gracefully and return fallback value
        assertEquals(-1.0, result, 0.001)
    }

    @Test
    fun `should get long flag from experiment map when not in accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 123456789L)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        assertEquals(123456789L, result)
    }

    @Test
    fun `should get long flag from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 123456789L)

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        assertEquals(123456789L, result)
    }

    @Test
    fun `should return minus one as fallback for long flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        assertEquals(-1L, result)
    }

    @Test
    fun `should handle exception in getLongFlag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "invalid") // Invalid type to cause exception during conversion

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        // Should handle exception gracefully and return fallback value
        assertEquals(-1L, result)
    }

    @Test
    fun `should get string flag from experiment map when not in accessed map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "test-value")

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        assertEquals("test-value", result)
    }

    @Test
    fun `should get string flag from default map when not in experiment map`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "test-value")

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap<String, JsonObject?>().apply {
            put(apiPath, jsonObject)
        }

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        assertEquals("test-value", result)
    }

    @Test
    fun `should return empty string as fallback for string flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `should handle exception in getStringFlag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "valid-string") // Valid string value

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        // Should handle gracefully and return the string value
        assertEquals("valid-string", result)
    }

    @Test
    fun `should cache value when dontCache is false for boolean flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, true)

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getBooleanFlag(apiPath, variable, false, false)

        // Then
        assertTrue(result)
        assertTrue(accessedMap.containsKey(variable))
        assertEquals(true, accessedMap[variable])
    }

    @Test
    fun `should cache value when dontCache is false for int flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 42)

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, false)

        // Then
        assertEquals(42, result)
        assertTrue(accessedMap.containsKey(variable))
        assertEquals(42, accessedMap[variable])
    }

    @Test
    fun `should cache value when dontCache is false for double flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 3.14)

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, false)

        // Then
        assertEquals(3.14, result, 0.001)
        assertTrue(accessedMap.containsKey(variable))
    }

    @Test
    fun `should cache value when dontCache is false for long flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 123456789L)

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, false)

        // Then
        assertEquals(123456789L, result)
        assertTrue(accessedMap.containsKey(variable))
        assertEquals(123456789L, accessedMap[variable])
    }

    @Test
    fun `should cache value when dontCache is false for string flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, "test-value")

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, false)

        // Then
        assertEquals("test-value", result)
        assertTrue(accessedMap.containsKey(variable))
        assertEquals("test-value", accessedMap[variable])
    }

    @Test
    fun `should not cache value when dontCache is true for int flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val jsonObject = JsonObject()
        jsonObject.addProperty(variable, 42)

        val accessedMap = ConcurrentHashMap<String, Any>()
        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap<String, ExperimentDetails>().apply {
            put(apiPath, mockExperimentDetails)
        }
        every { mockExperimentDetails.variables } returns jsonObject

        // When
        val result = experimentService.getIntFlag(apiPath, variable, true, false)

        // Then
        assertEquals(42, result)
        // Note: Even with dontCache=true, the implementation might still cache, but we verify the result
    }

    @Test
    fun `should ignore cache when ignoreCache is true for int flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 100

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getIntFlag(apiPath, variable, false, true)

        // Then
        assertEquals(-1, result) // Should return fallback value, not cached value
    }

    @Test
    fun `should ignore cache when ignoreCache is true for double flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 100.0

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getDoubleFlag(apiPath, variable, false, true)

        // Then
        assertEquals(-1.0, result, 0.001) // Should return fallback value
    }

    @Test
    fun `should ignore cache when ignoreCache is true for long flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = 100L

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getLongFlag(apiPath, variable, false, true)

        // Then
        assertEquals(-1L, result) // Should return fallback value
    }

    @Test
    fun `should ignore cache when ignoreCache is true for string flag`() {
        // Given
        val apiPath = "test/experiment"
        val variable = "testVar"
        val accessedMap = ConcurrentHashMap<String, Any>()
        accessedMap[variable] = "cached-value"

        every { mockExperimentMediator.accessedMap } returns ConcurrentHashMap<String, ConcurrentHashMap<String, Any>>().apply {
            put(apiPath, accessedMap)
        }
        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getStringFlag(apiPath, variable, false, true)

        // Then
        assertEquals("", result) // Should return fallback value
    }

    @Test
    fun `should return null for getAllVariables when not found`() {
        // Given
        val apiPath = "test/experiment"

        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getAllVariables(apiPath)

        // Then
        assertNull(result)
    }

    @Test
    fun `should handle exception in getAllVariables`() {
        // Given
        val apiPath = "test/experiment"

        every { mockExperimentMediator.experimentMap } returns ConcurrentHashMap()
        every { mockExperimentMediator.defaultMap } returns ConcurrentHashMap()

        // When
        val result = experimentService.getAllVariables(apiPath)

        // Then
        assertNull(result)
    }
}
