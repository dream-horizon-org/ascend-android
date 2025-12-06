package com.application.ascend_android.experiment

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*
import com.google.gson.JsonObject

class ExperimentResponseTest {

    @Test
    fun `should create DRSExperimentResponse with null data`() {
        // When
        val response = DRSExperimentResponse()

        // Then
        assertNull(response.data)
    }

    @Test
    fun `should set and get data`() {
        // Given
        val response = DRSExperimentResponse()
        val experimentDetails = listOf(ExperimentDetails())

        // When
        response.data = experimentDetails

        // Then
        assertEquals(experimentDetails, response.data)
    }

    @Test
    fun `should handle empty data list`() {
        // Given
        val response = DRSExperimentResponse()
        val emptyList = emptyList<ExperimentDetails>()

        // When
        response.data = emptyList

        // Then
        assertEquals(emptyList, response.data)
        assertTrue(response.data!!.isEmpty())
    }

    @Test
    fun `should handle multiple experiment details`() {
        // Given
        val response = DRSExperimentResponse()
        val experiment1 = ExperimentDetails().apply { apiPath = "test/experiment1" }
        val experiment2 = ExperimentDetails().apply { apiPath = "test/experiment2" }
        val experimentList = listOf(experiment1, experiment2)

        // When
        response.data = experimentList

        // Then
        assertEquals(2, response.data!!.size)
        assertEquals("test/experiment1", response.data!![0].apiPath)
        assertEquals("test/experiment2", response.data!![1].apiPath)
    }

    @Test
    fun `should have correct annotations`() {
        // Given
        val response = DRSExperimentResponse()

        // When
        val dataField = response.javaClass.getDeclaredField("data")

        // Then
        assertTrue(dataField.isAnnotationPresent(SerializedName::class.java))
        assertTrue(dataField.isAnnotationPresent(Expose::class.java))
        assertEquals("data", dataField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should be mutable`() {
        // Given
        val response = DRSExperimentResponse()
        val initialData = listOf(ExperimentDetails())
        val newData = listOf(ExperimentDetails().apply { apiPath = "new/experiment" })

        // When
        response.data = initialData
        response.data = newData

        // Then
        assertEquals(newData, response.data)
        assertNotEquals(initialData, response.data)
    }

    @Test
    fun `should handle null assignment`() {
        // Given
        val response = DRSExperimentResponse()
        val initialData = listOf(ExperimentDetails())

        // When
        response.data = initialData
        response.data = null

        // Then
        assertNull(response.data)
    }

    @Test
    fun `should work with Gson serialization`() {
        // Given
        val response = DRSExperimentResponse()
        val experiment = ExperimentDetails().apply {
            apiPath = "test/experiment"
            experimentId = "exp-123"
        }
        response.data = listOf(experiment)

        // When
        val json = com.google.gson.Gson().toJson(response)
        val deserializedResponse = com.google.gson.Gson().fromJson(json, DRSExperimentResponse::class.java)

        // Then
        assertNotNull(deserializedResponse.data)
        assertEquals(1, deserializedResponse.data!!.size)
        assertEquals("test/experiment", deserializedResponse.data!![0].apiPath)
        assertEquals("exp-123", deserializedResponse.data!![0].experimentId)
    }

    @Test
    fun `should handle large data list`() {
        // Given
        val response = DRSExperimentResponse()
        val largeList = (1..1000).map { i ->
            ExperimentDetails().apply {
                apiPath = "test/experiment$i"
                experimentId = "exp-$i"
            }
        }

        // When
        response.data = largeList

        // Then
        assertEquals(1000, response.data!!.size)
        assertEquals("test/experiment1", response.data!![0].apiPath)
        assertEquals("test/experiment1000", response.data!![999].apiPath)
    }
}
