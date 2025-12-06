package com.application.ascend_android.core.config

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class TimeoutConfigTest {

    private lateinit var timeoutConfig: TimeoutConfig

    @BeforeEach
    fun setUp() {
        timeoutConfig = TimeoutConfig(
            callTimeout = 10000L,
            shouldEnableLogging = true
        )
    }

    @Test
    fun `should create TimeoutConfig with default values`() {
        // When
        val defaultTimeoutConfig = TimeoutConfig(callTimeout = 5000L)

        // Then
        assertEquals(5000L, defaultTimeoutConfig.callTimeout)
        assertFalse(defaultTimeoutConfig.shouldEnableLogging)
    }

    @Test
    fun `should create TimeoutConfig with custom values`() {
        // When
        val customTimeoutConfig = TimeoutConfig(
            callTimeout = 15000L,
            shouldEnableLogging = true
        )

        // Then
        assertEquals(15000L, customTimeoutConfig.callTimeout)
        assertTrue(customTimeoutConfig.shouldEnableLogging)
    }

    @Test
    fun `should return correct callTimeout`() {
        // When
        val result = timeoutConfig.callTimeout()

        // Then
        assertEquals(10000L, result)
    }

    @Test
    fun `should return correct shouldEnableLogging`() {
        // When
        val result = timeoutConfig.shouldEnableLogging()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should implement ITimeoutConfig interface`() {
        // Then
        assertTrue(timeoutConfig is ITimeoutConfig)
    }

    @Test
    fun `should have correct data class properties`() {
        // When
        val timeoutConfig1 = TimeoutConfig(5000L, true)
        val timeoutConfig2 = TimeoutConfig(5000L, true)
        val timeoutConfig3 = TimeoutConfig(10000L, true)

        // Then
        assertEquals(timeoutConfig1, timeoutConfig2)
        assertNotEquals(timeoutConfig1, timeoutConfig3)
        assertEquals(timeoutConfig1.hashCode(), timeoutConfig2.hashCode())
    }

    @Test
    fun `should have correct toString representation`() {
        // When
        val result = timeoutConfig.toString()

        // Then
        assertTrue(result.contains("TimeoutConfig"))
        assertTrue(result.contains("callTimeout=10000"))
        assertTrue(result.contains("shouldEnableLogging=true"))
    }

    @Test
    fun `should handle zero timeout`() {
        // When
        val zeroTimeoutConfig = TimeoutConfig(
            callTimeout = 0L,
            shouldEnableLogging = false
        )

        // Then
        assertEquals(0L, zeroTimeoutConfig.callTimeout())
        assertFalse(zeroTimeoutConfig.shouldEnableLogging())
    }

    @Test
    fun `should handle negative timeout`() {
        // When
        val negativeTimeoutConfig = TimeoutConfig(
            callTimeout = -1000L,
            shouldEnableLogging = true
        )

        // Then
        assertEquals(-1000L, negativeTimeoutConfig.callTimeout())
        assertTrue(negativeTimeoutConfig.shouldEnableLogging())
    }

    @Test
    fun `should handle large timeout values`() {
        // When
        val largeTimeoutConfig = TimeoutConfig(
            callTimeout = Long.MAX_VALUE,
            shouldEnableLogging = false
        )

        // Then
        assertEquals(Long.MAX_VALUE, largeTimeoutConfig.callTimeout())
        assertFalse(largeTimeoutConfig.shouldEnableLogging())
    }

    @Test
    fun `should test copy functionality`() {
        // When
        val copiedConfig = timeoutConfig.copy(
            callTimeout = 20000L,
            shouldEnableLogging = false
        )

        // Then
        assertEquals(20000L, copiedConfig.callTimeout())
        assertFalse(copiedConfig.shouldEnableLogging())
        
        // Original should remain unchanged
        assertEquals(10000L, timeoutConfig.callTimeout())
        assertTrue(timeoutConfig.shouldEnableLogging())
    }

    @Test
    fun `should test component destructuring`() {
        // When
        val (callTimeout, shouldEnableLogging) = timeoutConfig

        // Then
        assertEquals(10000L, callTimeout)
        assertTrue(shouldEnableLogging)
    }

    @Test
    fun `should work with HttpConfig integration`() {
        // Given
        val httpConfig = HttpConfig(
            apiBaseUrl = "http://test.com",
            timeOutConfig = timeoutConfig
        )

        // When
        val retrievedTimeoutConfig = httpConfig.timeOutConfig()

        // Then
        assertEquals(timeoutConfig, retrievedTimeoutConfig)
        assertEquals(10000L, retrievedTimeoutConfig.callTimeout())
        assertTrue(retrievedTimeoutConfig.shouldEnableLogging())
    }

    @Test
    fun `should handle different logging states`() {
        // Test with logging enabled
        val loggingEnabledConfig = TimeoutConfig(5000L, true)
        assertTrue(loggingEnabledConfig.shouldEnableLogging())

        // Test with logging disabled
        val loggingDisabledConfig = TimeoutConfig(5000L, false)
        assertFalse(loggingDisabledConfig.shouldEnableLogging())
    }

    @Test
    fun `should maintain immutability`() {
        // Given
        val originalTimeout = timeoutConfig.callTimeout
        val originalLogging = timeoutConfig.shouldEnableLogging

        // When - call methods multiple times
        repeat(10) {
            timeoutConfig.callTimeout()
            timeoutConfig.shouldEnableLogging()
        }

        // Then - values should remain unchanged
        assertEquals(originalTimeout, timeoutConfig.callTimeout)
        assertEquals(originalLogging, timeoutConfig.shouldEnableLogging)
    }
}
