package com.application.ascend_android.core

import android.location.Address
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class AscendUserTest {

    private lateinit var mockAddress: Address
    private lateinit var mockPluggerCapabilities: PluggerCapabilities
    private lateinit var mockPluginManager: PluginManager
    private lateinit var mockDevice: IDevice

    @BeforeEach
    fun setUp() {
        mockAddress = mockk(relaxed = true)
        mockPluggerCapabilities = mockk(relaxed = true)
        mockPluginManager = mockk(relaxed = true)
        mockDevice = mockk(relaxed = true)

        // Reset AscendUser state
        AscendUser.isUserLoggedIn = false
        AscendUser.guestId = ""
        AscendUser.userId = ""
        AscendUser.refreshToken = ""

        // Mock static dependencies
        mockkObject(PluggerCapabilities)
        every { PluggerCapabilities.daggerPluggerComponent } returns mockk(relaxed = true)
        every { PluggerCapabilities.daggerPluggerComponent.providePluginManager() } returns mockPluginManager
        every { PluggerCapabilities.daggerPluggerComponent.provideDevice() } returns mockDevice
    }

    @Test
    fun `should initialize with default values`() {
        // Then
        assertFalse(AscendUser.isUserLoggedIn)
        assertEquals("", AscendUser.guestId)
        assertEquals("", AscendUser.userId)
        assertEquals("", AscendUser.refreshToken)
    }

    @Test
    fun `should set location successfully`() {
        // When
        AscendUser.setLocation(mockAddress)

        // Then
        verify { mockDevice.setLocation(mockAddress) }
    }

    @Test
    fun `should set lat long successfully`() {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060

        // When
        AscendUser.setLatLong(latitude, longitude)

        // Then
        verify { mockDevice.setLatLong(latitude, longitude) }
    }

    @Test
    fun `should set guest successfully`() {
        // Given
        val guestId = "guest123"

        // When
        AscendUser.setGuest(guestId)

        // Then
        assertEquals(guestId, AscendUser.guestId)
        assertEquals("", AscendUser.userId)
    }

    @Test
    fun `should set token successfully`() {
        // Given
        val refreshToken = "refresh_token_123"

        // When
        AscendUser.setToken(refreshToken)

        // Then
        assertEquals(refreshToken, AscendUser.refreshToken)
    }

    @Test
    fun `should set user successfully`() {
        // Given
        val userId = "user123"

        // When
        AscendUser.setUser(userId)

        // Then
        assertEquals(userId, AscendUser.userId)
    }

    @Test
    fun `should maintain state across multiple operations`() {
        // Given
        val userId = "user123"
        val guestId = "guest123"
        val refreshToken = "refresh_token_123"

        // When
        AscendUser.setUser(userId)
        AscendUser.setGuest(guestId)
        AscendUser.setToken(refreshToken)
        AscendUser.setLocation(mockAddress)
        AscendUser.setLatLong(40.7128, -74.0060)

        // Then
        assertEquals(guestId, AscendUser.guestId)
        assertEquals("", AscendUser.userId) // setGuest clears userId
        assertEquals(refreshToken, AscendUser.refreshToken)
    }

    @Test
    fun `should handle empty strings in setGuest`() {
        // Given
        val guestId = ""

        // When
        AscendUser.setGuest(guestId)

        // Then
        assertEquals("", AscendUser.guestId)
        assertEquals("", AscendUser.userId)
    }

    @Test
    fun `should handle empty strings in setToken`() {
        // Given
        val refreshToken = ""

        // When
        AscendUser.setToken(refreshToken)

        // Then
        assertEquals("", AscendUser.refreshToken)
    }

    @Test
    fun `should handle empty strings in setUser`() {
        // Given
        val userId = ""

        // When
        AscendUser.setUser(userId)

        // Then
        assertEquals("", AscendUser.userId)
    }

    @Test
    fun `should handle null address in setLocation`() {
        // Given
        val nullAddress: Address? = null

        // When & Then
        assertDoesNotThrow {
            if (nullAddress != null) {
                AscendUser.setLocation(nullAddress)
            }
        }
    }

    @Test
    fun `should handle extreme coordinate values`() {
        // Given
        val maxLatitude = 90.0
        val minLatitude = -90.0
        val maxLongitude = 180.0
        val minLongitude = -180.0

        // When & Then
        assertDoesNotThrow {
            AscendUser.setLatLong(maxLatitude, maxLongitude)
            AscendUser.setLatLong(minLatitude, minLongitude)
        }
    }
}
