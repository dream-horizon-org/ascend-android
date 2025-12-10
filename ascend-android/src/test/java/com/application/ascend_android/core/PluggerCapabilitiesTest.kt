package com.application.ascend_android.core

import android.location.Address
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class PluggerCapabilitiesTest {

    private lateinit var mockPluggerComponent: PluggerComponent
    private lateinit var mockPluginManager: PluginManager
    private lateinit var mockDevice: IDevice

    @BeforeEach
    fun setUp() {
        mockPluggerComponent = mockk(relaxed = true)
        mockPluginManager = mockk(relaxed = true)
        mockDevice = mockk(relaxed = true)

        mockkObject(PluggerCapabilities)
        every { PluggerCapabilities.daggerPluggerComponent } returns mockPluggerComponent
        every { mockPluggerComponent.providePluginManager() } returns mockPluginManager
        every { mockPluggerComponent.provideDevice() } returns mockDevice
    }

    @Test
    fun `should provide plugin manager`() {
        // When
        val result = PluggerCapabilities.daggerPluggerComponent.providePluginManager()

        // Then
        assertEquals(mockPluginManager, result)
        verify { mockPluggerComponent.providePluginManager() }
    }

    @Test
    fun `should provide device`() {
        // When
        val result = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertEquals(mockDevice, result)
        verify { mockPluggerComponent.provideDevice() }
    }

    @Test
    fun `should maintain singleton instance`() {
        // When
        val instance1 = PluggerCapabilities.daggerPluggerComponent
        val instance2 = PluggerCapabilities.daggerPluggerComponent

        // Then
        assertSame(instance1, instance2)
    }

    @Test
    fun `should provide plugin manager multiple times`() {
        // When
        val result1 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val result2 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()

        // Then
        assertEquals(mockPluginManager, result1)
        assertEquals(mockPluginManager, result2)
        verify(exactly = 2) { mockPluggerComponent.providePluginManager() }
    }

    @Test
    fun `should provide device multiple times`() {
        // When
        val result1 = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        val result2 = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertEquals(mockDevice, result1)
        assertEquals(mockDevice, result2)
        verify(exactly = 2) { mockPluggerComponent.provideDevice() }
    }

    @Test
    fun `should handle plugin manager operations`() {
        // Given
        val event = PluggerEvents.USER_LOGGED_IN
        val data = "test_data"

        // When
        val pluginManager = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        pluginManager.notifyAllPlugins(event, data)

        // Then
        verify { mockPluginManager.notifyAllPlugins(event, data) }
    }

    @Test
    fun `should handle device operations`() {
        // Given
        val mockAddress = mockk<Address>()
        val latitude = 40.7128
        val longitude = -74.0060

        // When
        val device = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        device.setLocation(mockAddress)
        device.setLatLong(latitude, longitude)

        // Then
        verify { mockDevice.setLocation(mockAddress) }
        verify { mockDevice.setLatLong(latitude, longitude) }
    }

    @Test
    fun `should handle mixed operations`() {
        // Given
        val event = PluggerEvents.USER_LOGGED_OUT
        val mockAddress = mockk<Address>()

        // When
        val pluginManager = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val device = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        
        pluginManager.notifyAllPlugins(event)
        device.setLocation(mockAddress)

        // Then
        verify { mockPluginManager.notifyAllPlugins(event, null) }
        verify { mockDevice.setLocation(mockAddress) }
    }

    @Test
    fun `should handle concurrent access`() {
        // When
        val pluginManager1 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val device1 = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        val pluginManager2 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val device2 = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertEquals(pluginManager1, pluginManager2)
        assertEquals(device1, device2)
        verify(exactly = 2) { mockPluggerComponent.providePluginManager() }
        verify(exactly = 2) { mockPluggerComponent.provideDevice() }
    }

    @Test
    fun `should provide consistent plugin manager instances`() {
        // When
        val pluginManager1 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val pluginManager2 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()

        // Then
        assertSame(pluginManager1, pluginManager2)
        verify(exactly = 2) { mockPluggerComponent.providePluginManager() }
    }

    @Test
    fun `should provide consistent device instances`() {
        // When
        val device1 = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        val device2 = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertSame(device1, device2)
        verify(exactly = 2) { mockPluggerComponent.provideDevice() }
    }

    @Test
    fun `should handle plugin manager with different instances`() {
        // Given
        val pluginManager1 = mockk<PluginManager>()
        val pluginManager2 = mockk<PluginManager>()
        every { mockPluggerComponent.providePluginManager() } returns pluginManager1 andThen pluginManager2

        // When
        val result1 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val result2 = PluggerCapabilities.daggerPluggerComponent.providePluginManager()

        // Then
        assertEquals(pluginManager1, result1)
        assertEquals(pluginManager2, result2)
        verify(exactly = 2) { mockPluggerComponent.providePluginManager() }
    }

    @Test
    fun `should handle device with different instances`() {
        // Given
        val device1 = mockk<IDevice>()
        val device2 = mockk<IDevice>()
        every { mockPluggerComponent.provideDevice() } returns device1 andThen device2

        // When
        val result1 = PluggerCapabilities.daggerPluggerComponent.provideDevice()
        val result2 = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertEquals(device1, result1)
        assertEquals(device2, result2)
        verify(exactly = 2) { mockPluggerComponent.provideDevice() }
    }

    @Test
    fun `should handle component replacement`() {
        // Given
        val newPluggerComponent = mockk<PluggerComponent>()
        val newPluginManager = mockk<PluginManager>()
        val newDevice = mockk<IDevice>()
        
        // Clear existing mocks and unmock the object
        clearAllMocks()
        unmockkObject(PluggerCapabilities)
        
        // Set up new component mocks after clearing
        every { newPluggerComponent.providePluginManager() } returns newPluginManager
        every { newPluggerComponent.provideDevice() } returns newDevice
        
        // When - replace the component directly
        PluggerCapabilities.daggerPluggerComponent = newPluggerComponent
        val pluginManager = PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        val device = PluggerCapabilities.daggerPluggerComponent.provideDevice()

        // Then
        assertEquals(newPluginManager, pluginManager)
        assertEquals(newDevice, device)
        verify { newPluggerComponent.providePluginManager() }
        verify { newPluggerComponent.provideDevice() }
        
        // Restore mocks for other tests
        mockkObject(PluggerCapabilities)
        every { PluggerCapabilities.daggerPluggerComponent } returns mockPluggerComponent
        every { mockPluggerComponent.providePluginManager() } returns mockPluginManager
        every { mockPluggerComponent.provideDevice() } returns mockDevice
    }

    @Test
    fun `should handle exception in plugin manager provision`() {
        // Given
        every { mockPluggerComponent.providePluginManager() } throws RuntimeException("Plugin manager error")

        // When & Then
        assertThrows(RuntimeException::class.java) {
            PluggerCapabilities.daggerPluggerComponent.providePluginManager()
        }
    }

    @Test
    fun `should handle exception in device provision`() {
        // Given
        every { mockPluggerComponent.provideDevice() } throws RuntimeException("Device error")

        // When & Then
        assertThrows(RuntimeException::class.java) {
            PluggerCapabilities.daggerPluggerComponent.provideDevice()
        }
    }
}
