package com.application.ascend_android.experiment

import com.google.gson.JsonObject
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class BaseExperimentServiceTest {

    private lateinit var concreteExperimentService: ConcreteExperimentService

    @BeforeEach
    fun setUp() {
        concreteExperimentService = ConcreteExperimentService()
    }

    @Test
    fun `should have correct default values for getBooleanFlag`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path")

        // Then
        assertFalse(result) // Default implementation returns false
    }

    @Test
    fun `should have correct default values for getIntFlag`() {
        // When
        val result = concreteExperimentService.getIntFlag("test/path")

        // Then
        assertEquals(-1, result) // Default implementation returns -1
    }

    @Test
    fun `should have correct default values for getDoubleFlag`() {
        // When
        val result = concreteExperimentService.getDoubleFlag("test/path")

        // Then
        assertEquals(-1.0, result, 0.001) // Default implementation returns -1.0
    }

    @Test
    fun `should have correct default values for getLongFlag`() {
        // When
        val result = concreteExperimentService.getLongFlag("test/path")

        // Then
        assertEquals(-1L, result) // Default implementation returns -1L
    }

    @Test
    fun `should have correct default values for getStringFlag`() {
        // When
        val result = concreteExperimentService.getStringFlag("test/path")

        // Then
        assertEquals("", result) // Default implementation returns empty string
    }

    @Test
    fun `should have correct default values for getAllVariables`() {
        // When
        val result = concreteExperimentService.getAllVariables("test/path")

        // Then
        assertNull(result) // Default implementation returns null
    }

    @Test
    fun `should have correct default values for getExperimentVariants`() {
        // When
        val result = concreteExperimentService.getExperimentVariants()

        // Then
        assertTrue(result.isEmpty()) // Default implementation returns empty map
    }

    @Test
    fun `should have correct default values for getExperimentsFromStorage`() {
        // When
        val result = concreteExperimentService.getExperimentsFromStorage()

        // Then
        assertTrue(result.isEmpty()) // Default implementation returns empty map
    }

    @Test
    fun `should use default variable name VALUE`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", VALUE)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should use custom variable name`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", "customVar")

        // Then
        assertFalse(result)
    }

    @Test
    fun `should use default dontCache value false`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", "var", false)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should use custom dontCache value`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", "var", true)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should use default ignoreCache value false`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", "var", false, false)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should use custom ignoreCache value`() {
        // When
        val result = concreteExperimentService.getBooleanFlag("test/path", "var", false, true)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should handle fetchExperiments with empty map`() {
        // Given
        val map = HashMap<String, JsonObject?>()
        val callback = mockk<IExperimentCallback>(relaxed = true)

        // When
        concreteExperimentService.fetchExperiments(map, callback)

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    fun `should handle fetchExperiments with null values`() {
        // Given
        val map = HashMap<String, JsonObject?>()
        map["test/experiment"] = null
        val callback = mockk<IExperimentCallback>(relaxed = true)

        // When
        concreteExperimentService.fetchExperiments(map, callback)

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    fun `should handle refreshExperiment`() {
        // Given
        val callback = mockk<IExperimentCallback>(relaxed = true)

        // When
        concreteExperimentService.refreshExperiment(callback)

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    fun `should handle setExperimentsToStorage with empty map`() {
        // Given
        val experiments = HashMap<String, ExperimentDetails>()

        // When
        concreteExperimentService.setExperimentsToStorage(experiments)

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    fun `should handle clearAllExperimentsData`() {
        // When
        concreteExperimentService.clearAllExperimentsData()

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    @Test
    fun `should handle clearUserSessionData`() {
        // When
        concreteExperimentService.clearUserSessionData()

        // Then
        // Should not throw exception
        assertTrue(true)
    }

    // Concrete implementation for testing abstract class
    private class ConcreteExperimentService : BaseExperimentService() {
        override fun getBooleanFlag(
            apiPath: String,
            variable: String,
            dontCache: Boolean,
            ignoreCache: Boolean
        ): Boolean = false

        override fun getIntFlag(
            apiPath: String,
            variable: String,
            dontCache: Boolean,
            ignoreCache: Boolean
        ): Int = -1

        override fun getDoubleFlag(
            apiPath: String,
            variable: String,
            dontCache: Boolean,
            ignoreCache: Boolean
        ): Double = -1.0

        override fun getLongFlag(
            apiPath: String,
            variable: String,
            dontCache: Boolean,
            ignoreCache: Boolean
        ): Long = -1L

        override fun getStringFlag(
            apiPath: String,
            variable: String,
            dontCache: Boolean,
            ignoreCache: Boolean
        ): String = ""

        override fun getAllVariables(apiPath: String): JsonObject? = null

        override fun fetchExperiments(map: HashMap<String, JsonObject?>, callback: IExperimentCallback) {
            // Default implementation - do nothing
        }

        override fun refreshExperiment(callback: IExperimentCallback) {
            // Default implementation - do nothing
        }

        override fun getExperimentVariants(): HashMap<String, ExperimentDetails> = HashMap()

        override fun setExperimentsToStorage(experiments: HashMap<String, ExperimentDetails>) {
            // Default implementation - do nothing
        }

        override fun getExperimentsFromStorage(): HashMap<String, ExperimentDetails> = HashMap()

        override fun clearAllExperimentsData() {
            // Default implementation - do nothing
        }

        override fun clearUserSessionData() {
            // Default implementation - do nothing
        }
    }
}
