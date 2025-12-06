package com.application.ascend_android.experiment

import com.google.gson.annotations.SerializedName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class ExperimentRequestTest {

    @Test
    fun `should create DRSExperimentRequest with empty apiPaths`() {
        // When
        val request = DRSExperimentRequest(emptyList())

        // Then
        assertTrue(request.apiPaths.isEmpty())
    }

    @Test
    fun `should create DRSExperimentRequest with single apiPath`() {
        // Given
        val apiPaths = listOf("test/experiment")

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(1, request.apiPaths.size)
        assertEquals("test/experiment", request.apiPaths[0])
    }

    @Test
    fun `should create DRSExperimentRequest with multiple apiPaths`() {
        // Given
        val apiPaths = listOf("test/experiment1", "test/experiment2", "test/experiment3")

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(3, request.apiPaths.size)
        assertEquals("test/experiment1", request.apiPaths[0])
        assertEquals("test/experiment2", request.apiPaths[1])
        assertEquals("test/experiment3", request.apiPaths[2])
    }

    @Test
    fun `should be immutable data class`() {
        // Given
        val apiPaths = listOf("test/experiment")
        val request = DRSExperimentRequest(apiPaths)

        // When
        val newApiPaths = listOf("new/experiment")
        val newRequest = request.copy(apiPaths = newApiPaths)

        // Then
        assertEquals(apiPaths, request.apiPaths) // Original unchanged
        assertEquals(newApiPaths, newRequest.apiPaths) // New has different values
    }

    @Test
    fun `should have correct SerializedName annotation`() {
        // Given
        val request = DRSExperimentRequest(emptyList())

        // When
        val apiPathsField = request.javaClass.getDeclaredField("apiPaths")

        // Then
        assertTrue(apiPathsField.isAnnotationPresent(SerializedName::class.java))
        assertEquals("apiPaths", apiPathsField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should work with Gson serialization`() {
        // Given
        val apiPaths = listOf("test/experiment1", "test/experiment2")
        val request = DRSExperimentRequest(apiPaths)

        // When
        val json = com.google.gson.Gson().toJson(request)
        val deserializedRequest = com.google.gson.Gson().fromJson(json, DRSExperimentRequest::class.java)

        // Then
        assertEquals(request.apiPaths, deserializedRequest.apiPaths)
        assertEquals(2, deserializedRequest.apiPaths.size)
        assertEquals("test/experiment1", deserializedRequest.apiPaths[0])
        assertEquals("test/experiment2", deserializedRequest.apiPaths[1])
    }

    @Test
    fun `should handle empty string apiPaths`() {
        // Given
        val apiPaths = listOf("", "test/experiment", "")

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(3, request.apiPaths.size)
        assertEquals("", request.apiPaths[0])
        assertEquals("test/experiment", request.apiPaths[1])
        assertEquals("", request.apiPaths[2])
    }

    @Test
    fun `should handle duplicate apiPaths`() {
        // Given
        val apiPaths = listOf("test/experiment", "test/experiment", "test/experiment")

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(3, request.apiPaths.size)
        assertEquals("test/experiment", request.apiPaths[0])
        assertEquals("test/experiment", request.apiPaths[1])
        assertEquals("test/experiment", request.apiPaths[2])
    }

    @Test
    fun `should handle large list of apiPaths`() {
        // Given
        val apiPaths = (1..1000).map { "test/experiment$it" }

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(1000, request.apiPaths.size)
        assertEquals("test/experiment1", request.apiPaths[0])
        assertEquals("test/experiment1000", request.apiPaths[999])
    }

    @Test
    fun `should support equals and hashCode`() {
        // Given
        val apiPaths1 = listOf("test/experiment1", "test/experiment2")
        val apiPaths2 = listOf("test/experiment1", "test/experiment2")
        val apiPaths3 = listOf("test/experiment1", "test/experiment3")

        val request1 = DRSExperimentRequest(apiPaths1)
        val request2 = DRSExperimentRequest(apiPaths2)
        val request3 = DRSExperimentRequest(apiPaths3)

        // When & Then
        assertEquals(request1, request2)
        assertNotEquals(request1, request3)
        assertEquals(request1.hashCode(), request2.hashCode())
        assertNotEquals(request1.hashCode(), request3.hashCode())
    }

    @Test
    fun `should support toString`() {
        // Given
        val apiPaths = listOf("test/experiment1", "test/experiment2")
        val request = DRSExperimentRequest(apiPaths)

        // When
        val toString = request.toString()

        // Then
        assertTrue(toString.contains("DRSExperimentRequest"))
        assertTrue(toString.contains("apiPaths"))
        assertTrue(toString.contains("test/experiment1"))
        assertTrue(toString.contains("test/experiment2"))
    }

    @Test
    fun `should handle special characters in apiPaths`() {
        // Given
        val apiPaths = listOf("test/experiment-with-dashes", "test/experiment_with_underscores", "test/experiment.with.dots")

        // When
        val request = DRSExperimentRequest(apiPaths)

        // Then
        assertEquals(3, request.apiPaths.size)
        assertEquals("test/experiment-with-dashes", request.apiPaths[0])
        assertEquals("test/experiment_with_underscores", request.apiPaths[1])
        assertEquals("test/experiment.with.dots", request.apiPaths[2])
    }
}
