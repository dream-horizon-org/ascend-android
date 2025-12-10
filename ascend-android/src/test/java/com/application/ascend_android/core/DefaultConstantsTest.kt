package com.application.ascend_android.core

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.experiment.*

class DefaultConstantsTest {

    @Test
    fun `should have correct lastModifiedTimestamp constant`() {
        // When
        val result = lastModifiedTimestamp

        // Then
        assertEquals("last-modified", result)
    }

    @Test
    fun `should have correct lastCachedResponseTimestamp constant`() {
        // When
        val result = lastCachedResponseTimestamp

        // Then
        assertEquals("last-cached-response-timestamp", result)
    }

    @Test
    fun `should have correct cacheWindow constant`() {
        // When
        val result = cacheWindow

        // Then
        assertEquals("cache-window", result)
    }

    @Test
    fun `should have non-empty constants`() {
        // Then
        assertTrue(lastModifiedTimestamp.isNotEmpty())
        assertTrue(lastCachedResponseTimestamp.isNotEmpty())
        assertTrue(cacheWindow.isNotEmpty())
    }

    @Test
    fun `should have unique constants`() {
        // Then
        assertNotEquals(lastModifiedTimestamp, lastCachedResponseTimestamp)
        assertNotEquals(lastModifiedTimestamp, cacheWindow)
        assertNotEquals(lastCachedResponseTimestamp, cacheWindow)
    }
}
