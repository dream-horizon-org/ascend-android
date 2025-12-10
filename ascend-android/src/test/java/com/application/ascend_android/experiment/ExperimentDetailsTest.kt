package com.application.ascend_android.experiment

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class ExperimentDetailsTest {

    @Test
    fun `should create ExperimentDetails with null fields`() {
        // When
        val experimentDetails = ExperimentDetails()

        // Then
        assertNull(experimentDetails.experimentId)
        assertNull(experimentDetails.apiPath)
        assertNull(experimentDetails.variantName)
        assertNull(experimentDetails.variables)
    }

    @Test
    fun `should set and get experimentId`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val experimentId = "exp-123"

        // When
        experimentDetails.experimentId = experimentId

        // Then
        assertEquals(experimentId, experimentDetails.experimentId)
    }

    @Test
    fun `should set and get apiPath`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val apiPath = "test/experiment"

        // When
        experimentDetails.apiPath = apiPath

        // Then
        assertEquals(apiPath, experimentDetails.apiPath)
    }

    @Test
    fun `should set and get variantName`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val variantName = "variant-a"

        // When
        experimentDetails.variantName = variantName

        // Then
        assertEquals(variantName, experimentDetails.variantName)
    }

    @Test
    fun `should set and get variables`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val variables = JsonObject()
        variables.addProperty("testVar", true)

        // When
        experimentDetails.variables = variables

        // Then
        assertEquals(variables, experimentDetails.variables)
    }

    @Test
    fun `should have correct annotations for experimentId`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        val experimentIdField = experimentDetails.javaClass.getDeclaredField("experimentId")

        // Then
        assertTrue(experimentIdField.isAnnotationPresent(SerializedName::class.java))
        assertTrue(experimentIdField.isAnnotationPresent(Expose::class.java))
        assertEquals("experimentId", experimentIdField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should have correct annotations for apiPath`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        val apiPathField = experimentDetails.javaClass.getDeclaredField("apiPath")

        // Then
        assertTrue(apiPathField.isAnnotationPresent(SerializedName::class.java))
        assertTrue(apiPathField.isAnnotationPresent(Expose::class.java))
        assertEquals("apiPath", apiPathField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should have correct annotations for variantName`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        val variantNameField = experimentDetails.javaClass.getDeclaredField("variantName")

        // Then
        assertTrue(variantNameField.isAnnotationPresent(SerializedName::class.java))
        assertTrue(variantNameField.isAnnotationPresent(Expose::class.java))
        assertEquals("variantName", variantNameField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should have correct annotations for variables`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        val variablesField = experimentDetails.javaClass.getDeclaredField("variables")

        // Then
        assertTrue(variablesField.isAnnotationPresent(SerializedName::class.java))
        assertTrue(variablesField.isAnnotationPresent(Expose::class.java))
        assertEquals("variables", variablesField.getAnnotation(SerializedName::class.java)?.value)
    }

    @Test
    fun `should work with Gson serialization`() {
        // Given
        val experimentDetails = ExperimentDetails()
        experimentDetails.experimentId = "exp-123"
        experimentDetails.apiPath = "test/experiment"
        experimentDetails.variantName = "variant-a"
        val variables = JsonObject()
        variables.addProperty("testVar", true)
        experimentDetails.variables = variables

        // When
        val json = com.google.gson.Gson().toJson(experimentDetails)
        val deserializedExperiment = com.google.gson.Gson().fromJson(json, ExperimentDetails::class.java)

        // Then
        assertEquals("exp-123", deserializedExperiment.experimentId)
        assertEquals("test/experiment", deserializedExperiment.apiPath)
        assertEquals("variant-a", deserializedExperiment.variantName)
        assertNotNull(deserializedExperiment.variables)
        assertTrue(deserializedExperiment.variables!!.get("testVar").asBoolean)
    }

    @Test
    fun `should handle empty strings`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        experimentDetails.experimentId = ""
        experimentDetails.apiPath = ""
        experimentDetails.variantName = ""

        // Then
        assertEquals("", experimentDetails.experimentId)
        assertEquals("", experimentDetails.apiPath)
        assertEquals("", experimentDetails.variantName)
    }

    @Test
    fun `should handle special characters in strings`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        experimentDetails.experimentId = "exp-123_with-special.chars"
        experimentDetails.apiPath = "test/experiment-with-dashes_and_underscores.with.dots"
        experimentDetails.variantName = "variant-with-special_chars.and.dots"

        // Then
        assertEquals("exp-123_with-special.chars", experimentDetails.experimentId)
        assertEquals("test/experiment-with-dashes_and_underscores.with.dots", experimentDetails.apiPath)
        assertEquals("variant-with-special_chars.and.dots", experimentDetails.variantName)
    }

    @Test
    fun `should handle complex JsonObject variables`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val variables = JsonObject()
        variables.addProperty("stringVar", "test-value")
        variables.addProperty("intVar", 42)
        variables.addProperty("doubleVar", 3.14)
        variables.addProperty("boolVar", true)

        // When
        experimentDetails.variables = variables

        // Then
        assertNotNull(experimentDetails.variables)
        assertEquals("test-value", experimentDetails.variables!!.get("stringVar").asString)
        assertEquals(42, experimentDetails.variables!!.get("intVar").asInt)
        assertEquals(3.14, experimentDetails.variables!!.get("doubleVar").asDouble, 0.001)
        assertTrue(experimentDetails.variables!!.get("boolVar").asBoolean)
    }

    @Test
    fun `should handle nested JsonObject variables`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val variables = JsonObject()
        val nestedObject = JsonObject()
        nestedObject.addProperty("nestedVar", "nested-value")
        variables.add("nested", nestedObject)

        // When
        experimentDetails.variables = variables

        // Then
        assertNotNull(experimentDetails.variables)
        assertTrue(experimentDetails.variables!!.has("nested"))
        val nested = experimentDetails.variables!!.getAsJsonObject("nested")
        assertEquals("nested-value", nested.get("nestedVar").asString)
    }

    @Test
    fun `should handle null assignment after setting values`() {
        // Given
        val experimentDetails = ExperimentDetails()
        experimentDetails.experimentId = "exp-123"
        experimentDetails.apiPath = "test/experiment"
        experimentDetails.variantName = "variant-a"
        experimentDetails.variables = JsonObject()

        // When
        experimentDetails.experimentId = null
        experimentDetails.apiPath = null
        experimentDetails.variantName = null
        experimentDetails.variables = null

        // Then
        assertNull(experimentDetails.experimentId)
        assertNull(experimentDetails.apiPath)
        assertNull(experimentDetails.variantName)
        assertNull(experimentDetails.variables)
    }

    @Test
    fun `should handle very long strings`() {
        // Given
        val experimentDetails = ExperimentDetails()
        val longString = "a".repeat(1000)

        // When
        experimentDetails.experimentId = longString
        experimentDetails.apiPath = longString
        experimentDetails.variantName = longString

        // Then
        assertEquals(longString, experimentDetails.experimentId)
        assertEquals(longString, experimentDetails.apiPath)
        assertEquals(longString, experimentDetails.variantName)
    }

    @Test
    fun `should handle unicode characters`() {
        // Given
        val experimentDetails = ExperimentDetails()

        // When
        experimentDetails.experimentId = "exp-测试-123"
        experimentDetails.apiPath = "test/实验/路径"
        experimentDetails.variantName = "变体-α"

        // Then
        assertEquals("exp-测试-123", experimentDetails.experimentId)
        assertEquals("test/实验/路径", experimentDetails.apiPath)
        assertEquals("变体-α", experimentDetails.variantName)
    }
}
