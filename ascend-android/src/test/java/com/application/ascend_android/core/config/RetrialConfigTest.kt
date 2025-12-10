package com.application.ascend_android.core.config

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class RetrialConfigTest {

    private lateinit var retrialConfig: RetrialConfig
    private lateinit var delay: Delay

    @BeforeEach
    fun setUp() {
        delay = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        retrialConfig = RetrialConfig(
            attempts = 3,
            delay = delay,
            omittedRetrialCodes = hashSetOf(404, 500)
        )
    }

    @Test
    fun `should create RetrialConfig with default values`() {
        // When
        val defaultRetrialConfig = RetrialConfig(attempts = 5)

        // Then
        assertEquals(5, defaultRetrialConfig.attempts)
        assertEquals(500L, defaultRetrialConfig.delay.time)
        assertEquals(RetryPolicy.LINEAR, defaultRetrialConfig.delay.policy)
        assertTrue(defaultRetrialConfig.omittedRetrialCodes.isEmpty())
    }

    @Test
    fun `should create RetrialConfig with custom values`() {
        // When
        val customDelay = Delay(time = 2000L, policy = RetryPolicy.QUADRATIC)
        val customRetrialConfig = RetrialConfig(
            attempts = 5,
            delay = customDelay,
            omittedRetrialCodes = hashSetOf(400, 401, 403)
        )

        // Then
        assertEquals(5, customRetrialConfig.attempts)
        assertEquals(customDelay, customRetrialConfig.delay)
        assertEquals(hashSetOf(400, 401, 403), customRetrialConfig.omittedRetrialCodes)
    }

    @Test
    fun `should return correct maxLimit`() {
        // When
        val result = retrialConfig.maxLimit()

        // Then
        assertEquals(3, result)
    }

    @Test
    fun `should return correct delay`() {
        // When
        val result = retrialConfig.delay()

        // Then
        assertEquals(delay, result)
        assertTrue(result is IDelay)
    }

    @Test
    fun `should return correct omittedRetrialCodes`() {
        // When
        val result = retrialConfig.omittedRetrialCodes()

        // Then
        assertEquals(hashSetOf(404, 500), result)
        assertTrue(result is HashSet<Int>)
    }

    @Test
    fun `should implement IRetryConfig interface`() {
        // Then
        assertTrue(retrialConfig is IRetryConfig)
    }

    @Test
    fun `should have correct data class properties`() {
        // When
        val retrialConfig1 = RetrialConfig(3, delay, hashSetOf(404, 500))
        val retrialConfig2 = RetrialConfig(3, delay, hashSetOf(404, 500))
        val retrialConfig3 = RetrialConfig(5, delay, hashSetOf(404, 500))

        // Then
        assertEquals(retrialConfig1, retrialConfig2)
        assertNotEquals(retrialConfig1, retrialConfig3)
        assertEquals(retrialConfig1.hashCode(), retrialConfig2.hashCode())
    }

    @Test
    fun `should have correct toString representation`() {
        // When
        val result = retrialConfig.toString()

        // Then
        assertTrue(result.contains("RetrialConfig"))
        assertTrue(result.contains("attempts=3"))
        assertTrue(result.contains("delay="))
        assertTrue(result.contains("omittedRetrialCodes="))
    }

    @Test
    fun `should handle zero attempts`() {
        // When
        val zeroAttemptsConfig = RetrialConfig(
            attempts = 0,
            delay = delay,
            omittedRetrialCodes = hashSetOf()
        )

        // Then
        assertEquals(0, zeroAttemptsConfig.maxLimit())
        assertTrue(zeroAttemptsConfig.omittedRetrialCodes().isEmpty())
    }

    @Test
    fun `should handle negative attempts`() {
        // When
        val negativeAttemptsConfig = RetrialConfig(
            attempts = -1,
            delay = delay,
            omittedRetrialCodes = hashSetOf()
        )

        // Then
        assertEquals(-1, negativeAttemptsConfig.maxLimit())
    }

    @Test
    fun `should handle large attempts`() {
        // When
        val largeAttemptsConfig = RetrialConfig(
            attempts = Int.MAX_VALUE,
            delay = delay,
            omittedRetrialCodes = hashSetOf()
        )

        // Then
        assertEquals(Int.MAX_VALUE, largeAttemptsConfig.maxLimit())
    }

    @Test
    fun `should handle empty omittedRetrialCodes`() {
        // When
        val emptyCodesConfig = RetrialConfig(
            attempts = 3,
            delay = delay,
            omittedRetrialCodes = hashSetOf()
        )

        // Then
        assertTrue(emptyCodesConfig.omittedRetrialCodes().isEmpty())
    }

    @Test
    fun `should handle multiple omittedRetrialCodes`() {
        // When
        val multipleCodesConfig = RetrialConfig(
            attempts = 3,
            delay = delay,
            omittedRetrialCodes = hashSetOf(400, 401, 403, 404, 500, 502, 503)
        )

        // Then
        assertEquals(7, multipleCodesConfig.omittedRetrialCodes().size)
        assertTrue(multipleCodesConfig.omittedRetrialCodes().contains(400))
        assertTrue(multipleCodesConfig.omittedRetrialCodes().contains(500))
        assertTrue(multipleCodesConfig.omittedRetrialCodes().contains(503))
    }

    @Test
    fun `should test copy functionality`() {
        // When
        val copiedConfig = retrialConfig.copy(
            attempts = 5,
            omittedRetrialCodes = hashSetOf(400, 500)
        )

        // Then
        assertEquals(5, copiedConfig.maxLimit())
        assertEquals(hashSetOf(400, 500), copiedConfig.omittedRetrialCodes())
        
        // Original should remain unchanged
        assertEquals(3, retrialConfig.maxLimit())
        assertEquals(hashSetOf(404, 500), retrialConfig.omittedRetrialCodes())
    }

    @Test
    fun `should test component destructuring`() {
        // When
        val (attempts, delayComponent, omittedCodes) = retrialConfig

        // Then
        assertEquals(3, attempts)
        assertEquals(delay, delayComponent)
        assertEquals(hashSetOf(404, 500), omittedCodes)
    }

    @Test
    fun `should work with HttpConfig integration`() {
        // Given
        val httpConfig = HttpConfig(
            apiBaseUrl = "http://test.com",
            retrialConfig = retrialConfig
        )

        // When
        val retrievedRetrialConfig = httpConfig.retrialConfig()

        // Then
        assertEquals(retrialConfig, retrievedRetrialConfig)
        assertEquals(3, retrievedRetrialConfig.maxLimit())
        assertEquals(delay, retrievedRetrialConfig.delay())
        assertEquals(hashSetOf(404, 500), retrievedRetrialConfig.omittedRetrialCodes())
    }

    @Test
    fun `should maintain immutability`() {
        // Given
        val originalAttempts = retrialConfig.attempts
        val originalDelay = retrialConfig.delay
        val originalCodes = retrialConfig.omittedRetrialCodes

        // When - call methods multiple times
        repeat(10) {
            retrialConfig.maxLimit()
            retrialConfig.delay()
            retrialConfig.omittedRetrialCodes()
        }

        // Then - values should remain unchanged
        assertEquals(originalAttempts, retrialConfig.attempts)
        assertEquals(originalDelay, retrialConfig.delay)
        assertEquals(originalCodes, retrialConfig.omittedRetrialCodes)
    }

    @Test
    fun `should handle different delay configurations`() {
        // Test with LINEAR policy
        val linearDelay = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        val linearConfig = RetrialConfig(3, linearDelay, hashSetOf())
        assertEquals(NetworkRetrialPolicy.LINEAR, linearConfig.delay().retrialPolicy())

        // Test with INCREMENTAL policy
        val incrementalDelay = Delay(time = 1000L, policy = RetryPolicy.INCREMENTAL)
        val incrementalConfig = RetrialConfig(3, incrementalDelay, hashSetOf())
        assertEquals(NetworkRetrialPolicy.INCREMENTAL, incrementalConfig.delay().retrialPolicy())

        // Test with QUADRATIC policy
        val quadraticDelay = Delay(time = 1000L, policy = RetryPolicy.QUADRATIC)
        val quadraticConfig = RetrialConfig(3, quadraticDelay, hashSetOf())
        assertEquals(NetworkRetrialPolicy.QUADRATIC, quadraticConfig.delay().retrialPolicy())
    }
}
