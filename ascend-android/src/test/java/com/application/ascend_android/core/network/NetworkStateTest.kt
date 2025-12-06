package com.application.ascend_android.core.network

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*
import retrofit2.Response

class NetworkStateTest {

    @Test
    fun `should create Success state with data`() {
        // Given
        val testData = "test_data"

        // When
        val successState = NetworkState.Success(testData)

        // Then
        assertTrue(successState is NetworkState.Success)
        assertEquals(testData, successState.data)
    }

    @Test
    fun `should create Error state with response`() {
        // Given
        val mockResponse = mockk<Response<String>>()

        // When
        val errorState = NetworkState.Error(mockResponse)

        // Then
        assertTrue(errorState is NetworkState.Error)
        assertEquals(mockResponse, errorState.response)
    }

    @Test
    fun `should create APIException state with throwable`() {
        // Given
        val testException = RuntimeException("API error")

        // When
        val apiExceptionState = NetworkState.APIException<Any>(testException)

        // Then
        assertTrue(apiExceptionState is NetworkState.APIException)
        assertEquals(testException, apiExceptionState.throwable)
    }

    @Test
    fun `should handle Success state with null data`() {
        // When
        val successState = NetworkState.Success(null)

        // Then
        assertTrue(successState is NetworkState.Success)
        assertNull(successState.data)
    }

    @Test
    fun `should handle Success state with complex data`() {
        // Given
        val complexData = mapOf("key1" to "value1", "key2" to 123)

        // When
        val successState = NetworkState.Success(complexData)

        // Then
        assertTrue(successState is NetworkState.Success)
        assertEquals(complexData, successState.data)
    }

    @Test
    fun `should handle Error state with different response types`() {
        // Given
        val stringResponse = mockk<Response<String>>()
        val intResponse = mockk<Response<Int>>()

        // When
        val stringErrorState = NetworkState.Error<String>(stringResponse)
        val intErrorState = NetworkState.Error<Int>(intResponse)

        // Then
        assertTrue(stringErrorState is NetworkState.Error)
        assertTrue(intErrorState is NetworkState.Error)
        assertEquals(stringResponse, stringErrorState.response)
        assertEquals(intResponse, intErrorState.response)
    }

    @Test
    fun `should handle APIException state with different exception types`() {
        // Given
        val httpException = java.net.HttpRetryException("HTTP error", 500)
        val socketException = java.net.SocketTimeoutException("Socket timeout")

        // When
        val httpApiState = NetworkState.APIException<Any>(httpException)
        val socketApiState = NetworkState.APIException<Any>(socketException)

        // Then
        assertTrue(httpApiState is NetworkState.APIException)
        assertTrue(socketApiState is NetworkState.APIException)
        assertEquals(httpException, httpApiState.throwable)
        assertEquals(socketException, socketApiState.throwable)
    }

    @Test
    fun `should be able to check state types`() {
        // Given
        val successState = NetworkState.Success("data")
        val errorState = NetworkState.Error<String>(mockk<Response<String>>())
        val apiState = NetworkState.APIException<String>(RuntimeException())

        // When & Then
        assertTrue(successState is NetworkState.Success<*>)
        assertFalse(successState is NetworkState.Error<*>)
        assertFalse(successState is NetworkState.APIException<*>)

        assertTrue(errorState is NetworkState.Error<*>)
        assertFalse(errorState is NetworkState.Success<*>)
        assertFalse(errorState is NetworkState.APIException<*>)

        assertTrue(apiState is NetworkState.APIException<*>)
        assertFalse(apiState is NetworkState.Success<*>)
        assertFalse(apiState is NetworkState.Error<*>)
    }

    @Test
    fun `should handle when expressions with NetworkState`() {
        // Given
        val successState = NetworkState.Success("success_data")
        val errorState = NetworkState.Error<String>(mockk<Response<String>>())
        val apiState = NetworkState.APIException<String>(RuntimeException("api_error"))

        // When
        val successResult = when (successState) {
            is NetworkState.Success<*> -> successState.data
            is NetworkState.Error<*> -> "error"
            is NetworkState.APIException<*> -> "api_error"
        }

        val errorResult = when (errorState) {
            is NetworkState.Success<*> -> successState.data
            is NetworkState.Error<*> -> "error"
            is NetworkState.APIException<*> -> "api_error"
        }

        val apiResult = when (apiState) {
            is NetworkState.Success<*> -> successState.data
            is NetworkState.Error<*> -> "error"
            is NetworkState.APIException<*> -> "api_error"
        }

        // Then
        assertEquals("success_data", successResult)
        assertEquals("error", errorResult)
        assertEquals("api_error", apiResult)
    }

    @Test
    fun `should maintain data integrity in Success state`() {
        // Given
        val originalData = listOf(1, 2, 3, 4, 5)
        val successState = NetworkState.Success(originalData)

        // When
        val retrievedData = successState.data

        // Then
        assertEquals(originalData, retrievedData)
        assertSame(originalData, retrievedData)
    }

    @Test
    fun `should maintain response integrity in Error state`() {
        // Given
        val originalResponse = mockk<Response<String>>()
        val errorState = NetworkState.Error<String>(originalResponse)

        // When
        val retrievedResponse = errorState.response

        // Then
        assertEquals(originalResponse, retrievedResponse)
        assertSame(originalResponse, retrievedResponse)
    }

    @Test
    fun `should maintain throwable integrity in APIException state`() {
        // Given
        val originalException = java.net.ConnectException("Connection failed")
        val apiState = NetworkState.APIException<Any>(originalException)

        // When
        val retrievedException = apiState.throwable

        // Then
        assertEquals(originalException, retrievedException)
        assertSame(originalException, retrievedException)
        assertEquals("Connection failed", retrievedException.message)
    }

    @Test
    fun `should handle generic type parameters`() {
        // Given
        val stringSuccess = NetworkState.Success<String>("string_data")
        val intSuccess = NetworkState.Success<Int>(42)
        val listSuccess = NetworkState.Success<List<String>>(listOf("a", "b", "c"))

        // When
        val stringData = stringSuccess.data
        val intData = intSuccess.data
        val listData = listSuccess.data

        // Then
        assertEquals("string_data", stringData)
        assertEquals(42, intData)
        assertEquals(listOf("a", "b", "c"), listData)
    }

    @Test
    fun `should be serializable for testing purposes`() {
        // Given
        val successState = NetworkState.Success("test")
        val errorState = NetworkState.Error<String>(mockk<Response<String>>())

        // When & Then
        assertDoesNotThrow {
            // Test that states can be used in collections
            val states = listOf(successState, errorState)
            assertEquals(2, states.size)
        }
    }
}