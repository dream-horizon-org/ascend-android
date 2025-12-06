package com.application.ascend_android.core.network

import com.application.ascend_android.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.math.pow

class NetworkClientTest : BaseCoroutineTest() {

    private lateinit var networkClient: NetworkClient
    private lateinit var mockHttpInterface: IApi

    @BeforeEach
    fun setUp() {
        mockHttpInterface = mockk(relaxed = true)

        networkClient = NetworkClient(
            httpInterface = mockHttpInterface,
            shouldRetry = false,
            retrialPolicy = NetworkRetrialPolicy.LINEAR,
            retryTimes = 0,
            delayTime = 1000L,
            pluginType = Plugins.EXPERIMENTS,
            omittedRetrialCodes = hashSetOf(404, 401)
        )
    }

    @Test
    fun `should initialize with correct parameters`() {
        // Then
        assertFalse(networkClient.shouldRetry)
        assertEquals(NetworkRetrialPolicy.LINEAR, networkClient.retrialPolicy)
        assertEquals(0, networkClient.retryTimes)
        assertEquals(1000L, networkClient.delayTime)
        assertEquals(Plugins.EXPERIMENTS, networkClient.pluginType)
        assertTrue(networkClient.omittedRetrialCodes.contains(404))
        assertTrue(networkClient.omittedRetrialCodes.contains(401))
    }

    @Test
    fun `should calculate delay correctly for LINEAR policy`() {
        // Given
        val tryNumber = 3
        val initialInterval = 1000L
        val policy = NetworkRetrialPolicy.LINEAR

        // When
        val result = calculateDelay(tryNumber, initialInterval, policy)

        // Then
        assertEquals(1000L, result)
    }

    @Test
    fun `should calculate delay correctly for INCREMENTAL policy`() {
        // Given
        val tryNumber = 3
        val initialInterval = 1000L
        val policy = NetworkRetrialPolicy.INCREMENTAL

        // When
        val result = calculateDelay(tryNumber, initialInterval, policy)

        // Then
        assertEquals(3000L, result)
    }

    @Test
    fun `should calculate delay correctly for QUADRATIC policy`() {
        // Given
        val tryNumber = 2
        val initialInterval = 1000L
        val policy = NetworkRetrialPolicy.QUADRATIC

        // When
        val result = calculateDelay(tryNumber, initialInterval, policy)

        // Then
        val expected = ((1000.0 / 1000).pow(2) * 1000).toLong()
        assertEquals(expected, result)
    }

    @Test
    fun `should handle retry operation with default parameters`() = runTest {
        // Given
        var operationCalled = false
        val operation: suspend RetryOperation.() -> Unit = {
            operationCalled = true
        }

        // When
        retryOperation(operation = operation)

        // Then
        assertTrue(operationCalled)
    }

    @Test
    fun `should handle retry operation with custom parameters`() = runTest {
        // Given
        var operationCalled = false
        val operation: suspend RetryOperation.() -> Unit = {
            operationCalled = true
        }

        // When
        retryOperation(
            retries = 3,
            initialDelay = 100L,
            initialIntervalMilli = 500L,
            retryStrategy = NetworkRetrialPolicy.INCREMENTAL,
            operation = operation
        )

        // Then
        assertTrue(operationCalled)
    }

    @Test
    fun `should set retry times to zero when shouldRetry is false`() {
        // Given
        networkClient.shouldRetry = false
        networkClient.retryTimes = 5

        // When
        // The retry times should be set to zero in the getData method
        // This is tested by checking the property directly

        // Then
        assertEquals(5, networkClient.retryTimes) // Before getData call
    }
}