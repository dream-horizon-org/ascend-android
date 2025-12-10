package com.application.ascend_android.core.config

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class DelayTest {

    private lateinit var delay: Delay

    @BeforeEach
    fun setUp() {
        delay = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
    }

    @Test
    fun `should create Delay with default values`() {
        // When
        val defaultDelay = Delay()

        // Then
        assertEquals(500L, defaultDelay.time)
        assertEquals(RetryPolicy.LINEAR, defaultDelay.policy)
    }

    @Test
    fun `should create Delay with custom values`() {
        // When
        val customDelay = Delay(time = 2000L, policy = RetryPolicy.QUADRATIC)

        // Then
        assertEquals(2000L, customDelay.time)
        assertEquals(RetryPolicy.QUADRATIC, customDelay.policy)
    }

    @Test
    fun `should return correct delayTime`() {
        // When
        val result = delay.delayTime()

        // Then
        assertEquals(1000L, result)
    }

    @Test
    fun `should return correct retrialPolicy for LINEAR`() {
        // When
        val linearDelay = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        val result = linearDelay.retrialPolicy()

        // Then
        assertEquals(NetworkRetrialPolicy.LINEAR, result)
    }

    @Test
    fun `should return correct retrialPolicy for INCREMENTAL`() {
        // When
        val incrementalDelay = Delay(time = 1000L, policy = RetryPolicy.INCREMENTAL)
        val result = incrementalDelay.retrialPolicy()

        // Then
        assertEquals(NetworkRetrialPolicy.INCREMENTAL, result)
    }

    @Test
    fun `should return correct retrialPolicy for QUADRATIC`() {
        // When
        val quadraticDelay = Delay(time = 1000L, policy = RetryPolicy.QUADRATIC)
        val result = quadraticDelay.retrialPolicy()

        // Then
        assertEquals(NetworkRetrialPolicy.QUADRATIC, result)
    }

    @Test
    fun `should implement IDelay interface`() {
        // Then
        assertTrue(delay is IDelay)
    }

    @Test
    fun `should have correct data class properties`() {
        // When
        val delay1 = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        val delay2 = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        val delay3 = Delay(time = 2000L, policy = RetryPolicy.LINEAR)

        // Then
        assertEquals(delay1, delay2)
        assertNotEquals(delay1, delay3)
        assertEquals(delay1.hashCode(), delay2.hashCode())
    }

    @Test
    fun `should have correct toString representation`() {
        // When
        val result = delay.toString()

        // Then
        assertTrue(result.contains("Delay"))
        assertTrue(result.contains("time=1000"))
        assertTrue(result.contains("policy=LINEAR"))
    }

    @Test
    fun `should handle zero time`() {
        // When
        val zeroTimeDelay = Delay(time = 0L, policy = RetryPolicy.LINEAR)

        // Then
        assertEquals(0L, zeroTimeDelay.delayTime())
        assertEquals(NetworkRetrialPolicy.LINEAR, zeroTimeDelay.retrialPolicy())
    }

    @Test
    fun `should handle negative time`() {
        // When
        val negativeTimeDelay = Delay(time = -1000L, policy = RetryPolicy.LINEAR)

        // Then
        assertEquals(-1000L, negativeTimeDelay.delayTime())
        assertEquals(NetworkRetrialPolicy.LINEAR, negativeTimeDelay.retrialPolicy())
    }

    @Test
    fun `should handle large time values`() {
        // When
        val largeTimeDelay = Delay(time = Long.MAX_VALUE, policy = RetryPolicy.QUADRATIC)

        // Then
        assertEquals(Long.MAX_VALUE, largeTimeDelay.delayTime())
        assertEquals(NetworkRetrialPolicy.QUADRATIC, largeTimeDelay.retrialPolicy())
    }

    @Test
    fun `should test copy functionality`() {
        // When
        val copiedDelay = delay.copy(
            time = 2000L,
            policy = RetryPolicy.QUADRATIC
        )

        // Then
        assertEquals(2000L, copiedDelay.delayTime())
        assertEquals(NetworkRetrialPolicy.QUADRATIC, copiedDelay.retrialPolicy())
        
        // Original should remain unchanged
        assertEquals(1000L, delay.delayTime())
        assertEquals(NetworkRetrialPolicy.LINEAR, delay.retrialPolicy())
    }

    @Test
    fun `should test component destructuring`() {
        // When
        val (time, policy) = delay

        // Then
        assertEquals(1000L, time)
        assertEquals(RetryPolicy.LINEAR, policy)
    }

    @Test
    fun `should work with RetrialConfig integration`() {
        // Given
        val retrialConfig = RetrialConfig(
            attempts = 3,
            delay = delay
        )

        // When
        val retrievedDelay = retrialConfig.delay()

        // Then
        assertEquals(delay, retrievedDelay)
        assertEquals(1000L, retrievedDelay.delayTime())
        assertEquals(NetworkRetrialPolicy.LINEAR, retrievedDelay.retrialPolicy())
    }

    @Test
    fun `should maintain immutability`() {
        // Given
        val originalTime = delay.time
        val originalPolicy = delay.policy

        // When - call methods multiple times
        repeat(10) {
            delay.delayTime()
            delay.retrialPolicy()
        }

        // Then - values should remain unchanged
        assertEquals(originalTime, delay.time)
        assertEquals(originalPolicy, delay.policy)
    }

    @Test
    fun `should handle all retry policy types`() {
        // Test LINEAR policy
        val linearDelay = Delay(time = 500L, policy = RetryPolicy.LINEAR)
        assertEquals(NetworkRetrialPolicy.LINEAR, linearDelay.retrialPolicy())

        // Test INCREMENTAL policy
        val incrementalDelay = Delay(time = 500L, policy = RetryPolicy.INCREMENTAL)
        assertEquals(NetworkRetrialPolicy.INCREMENTAL, incrementalDelay.retrialPolicy())

        // Test QUADRATIC policy
        val quadraticDelay = Delay(time = 500L, policy = RetryPolicy.QUADRATIC)
        assertEquals(NetworkRetrialPolicy.QUADRATIC, quadraticDelay.retrialPolicy())
    }

    @Test
    fun `should handle different time values with same policy`() {
        // Test different time values with LINEAR policy
        val shortDelay = Delay(time = 100L, policy = RetryPolicy.LINEAR)
        val mediumDelay = Delay(time = 1000L, policy = RetryPolicy.LINEAR)
        val longDelay = Delay(time = 10000L, policy = RetryPolicy.LINEAR)

        assertEquals(100L, shortDelay.delayTime())
        assertEquals(1000L, mediumDelay.delayTime())
        assertEquals(10000L, longDelay.delayTime())

        // All should have the same policy
        assertEquals(NetworkRetrialPolicy.LINEAR, shortDelay.retrialPolicy())
        assertEquals(NetworkRetrialPolicy.LINEAR, mediumDelay.retrialPolicy())
        assertEquals(NetworkRetrialPolicy.LINEAR, longDelay.retrialPolicy())
    }

    @Test
    fun `should handle same time with different policies`() {
        // Test same time with different policies
        val time = 1000L
        val linearDelay = Delay(time = time, policy = RetryPolicy.LINEAR)
        val incrementalDelay = Delay(time = time, policy = RetryPolicy.INCREMENTAL)
        val quadraticDelay = Delay(time = time, policy = RetryPolicy.QUADRATIC)

        // All should have the same time
        assertEquals(time, linearDelay.delayTime())
        assertEquals(time, incrementalDelay.delayTime())
        assertEquals(time, quadraticDelay.delayTime())

        // But different policies
        assertEquals(NetworkRetrialPolicy.LINEAR, linearDelay.retrialPolicy())
        assertEquals(NetworkRetrialPolicy.INCREMENTAL, incrementalDelay.retrialPolicy())
        assertEquals(NetworkRetrialPolicy.QUADRATIC, quadraticDelay.retrialPolicy())
    }
}
