package com.application.ascend_android.core

import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class CoroutineUtilsTest : BaseCoroutineTest() {

    @BeforeEach
    fun setUp() {
        mockkObject(CoroutineUtils)
    }

    @Test
    fun `should test launchCoroutine method exists`() = runTest {
        // Given
        var executed = false
        val block: suspend CoroutineScope.() -> Unit = { executed = true }

        // When & Then
        // Test that the method exists and can be called
        assertDoesNotThrow {
            // Since launchCoroutine is a companion object method, we'll check the companion object
            val companionClass = CoroutineUtils::class.java.getDeclaredClasses().firstOrNull { it.simpleName == "Companion" }
            assertNotNull(companionClass)
            assertTrue(companionClass!!.declaredMethods.any { it.name == "launchCoroutine" })
        }
    }

    @Test
    fun `should test CoroutineUtils class structure`() {
        // Given
        val coroutineUtilsClass = CoroutineUtils::class.java

        // When & Then
        assertNotNull(coroutineUtilsClass)
        assertTrue(coroutineUtilsClass.declaredMethods.isNotEmpty() || coroutineUtilsClass.declaredClasses.isNotEmpty())
    }

    @Test
    fun `should test CoroutineUtils has launchCoroutine method`() {
        // Given
        val coroutineUtilsClass = CoroutineUtils::class.java
        val companionClass = coroutineUtilsClass.getDeclaredClasses().firstOrNull { it.simpleName == "Companion" }

        // When
        val hasLaunchCoroutine = companionClass?.declaredMethods?.any { it.name == "launchCoroutine" } ?: false

        // Then
        assertTrue(hasLaunchCoroutine)
    }

    @Test
    fun `should test CoroutineUtils has launchRunBlocking method`() {
        // Given
        val coroutineUtilsClass = CoroutineUtils::class.java
        val companionClass = coroutineUtilsClass.getDeclaredClasses().firstOrNull { it.simpleName == "Companion" }

        // When
        val hasLaunchRunBlocking = companionClass?.declaredMethods?.any { it.name == "launchRunBlocking" } ?: false

        // Then
        assertTrue(hasLaunchRunBlocking)
    }

    @Test
    fun `should test CoroutineUtils is class`() {
        // Given
        val coroutineUtilsClass = CoroutineUtils::class.java

        // When & Then
        assertTrue(coroutineUtilsClass.name.contains("CoroutineUtils"))
    }
}
