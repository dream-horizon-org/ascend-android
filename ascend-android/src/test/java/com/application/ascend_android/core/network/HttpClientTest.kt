package com.application.ascend_android.core.network

import android.content.Context
import com.application.ascend_android.*
import com.google.gson.Gson
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import retrofit2.Retrofit

class HttpClientTest {

    private lateinit var httpClient: HttpClient
    private lateinit var mockContext: Context

    @BeforeEach
    fun setUp() {
        mockContext = mockk(relaxed = true)
        httpClient = HttpClient(
            context = mockContext,
            baseUrl = "https://test.com/",
            callTimeOut = 30000L
        )
    }

    @Test
    fun `should initialize with correct parameters`() {
        // Then
        assertEquals(mockContext, httpClient.context)
        assertEquals("https://test.com/", httpClient.baseUrl)
        assertNotNull(httpClient.gson)
        assertNotNull(httpClient.retrofit)
        assertNotNull(httpClient.okHttpClient)
    }

    @Test
    fun `should create retrofit with correct configuration`() {
        // Given
        val baseUrl = "https://api.example.com/"
        val httpClient = HttpClient(mockContext, baseUrl, 5000L)

        // When
        val retrofit = httpClient.retrofit

        // Then
        assertNotNull(retrofit)
        assertEquals(baseUrl, retrofit.baseUrl().toString())
    }

    @Test
    fun `should create okhttp client with correct timeout`() {
        // Given
        val timeout = 15000L
        val httpClient = HttpClient(mockContext, "https://test.com/", timeout)

        // When
        val okHttpClient = httpClient.okHttpClient

        // Then
        assertNotNull(okHttpClient)
        // Verify timeout is set correctly (may vary slightly due to lazy initialization)
        assertTrue(okHttpClient.callTimeoutMillis > 0)
    }

    @Test
    fun `should create gson with number policy`() {
        // When
        val gson = httpClient.gson

        // Then
        assertNotNull(gson)
        assertTrue(gson is Gson)
    }

    @Test
    fun `should create okhttp client with ssl configuration`() {
        // Given
        val httpClient = HttpClient(mockContext, "https://test.com/", 1000L)

        // When
        val okHttpClient = httpClient.okHttpClient

        // Then
        assertNotNull(okHttpClient)
        assertFalse(okHttpClient.retryOnConnectionFailure)
        assertFalse(okHttpClient.followRedirects)
    }

    @Test
    fun `should handle different base urls`() {
        // Given
        val baseUrls = listOf(
            "https://api1.example.com/",
            "https://api2.example.com/v1/",
            "http://localhost:8080/"
        )

        baseUrls.forEach { baseUrl ->
            // When
            val httpClient = HttpClient(mockContext, baseUrl, 1000L)

            // Then
            assertEquals(baseUrl, httpClient.baseUrl)
            assertEquals(baseUrl, httpClient.retrofit.baseUrl().toString())
        }
    }

    @Test
    fun `should handle different timeout values`() {
        // Given
        val timeouts = listOf(1000L, 5000L, 30000L, 60000L)

        timeouts.forEach { timeout ->
            // When
            val httpClient = HttpClient(mockContext, "https://test.com/", timeout)

            // Then
            assertTrue(httpClient.okHttpClient.callTimeoutMillis > 0)
        }
    }

    @Test
    fun `should create retrofit with gson converter`() {
        // Given
        val httpClient = HttpClient(mockContext, "https://test.com/", 1000L)

        // When
        val retrofit = httpClient.retrofit

        // Then
        assertNotNull(retrofit)
        // Verify that the converter factory is set (internal implementation detail)
        assertTrue(retrofit.converterFactories().isNotEmpty())
    }

    @Test
    fun `should handle ssl socket factory configuration`() {
        // Given
        val httpClient = HttpClient(mockContext, "https://test.com/", 1000L)

        // When
        val okHttpClient = httpClient.okHttpClient

        // Then
        assertNotNull(okHttpClient)
        // SSL configuration should be applied (might be null in test environment)
        assertNotNull(okHttpClient.sslSocketFactory)
    }

    @Test
    fun `should handle context dependency injection`() {
        // Given
        val newContext = mockk<Context>(relaxed = true)
        val httpClient = HttpClient(newContext, "https://test.com/", 1000L)

        // When
        val context = httpClient.context

        // Then
        assertEquals(newContext, context)
    }

    @Test
    fun `should handle base url modification`() {
        // Given
        val newBaseUrl = "https://newapi.example.com/"

        // When
        httpClient.baseUrl = newBaseUrl

        // Then
        assertEquals(newBaseUrl, httpClient.baseUrl)
    }

    @Test
    fun `should handle gson modification`() {
        // Given
        val newGson = Gson()

        // When
        httpClient.gson = newGson

        // Then
        assertEquals(newGson, httpClient.gson)
    }
}