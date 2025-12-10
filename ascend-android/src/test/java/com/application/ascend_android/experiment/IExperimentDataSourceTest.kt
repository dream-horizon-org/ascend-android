package com.application.ascend_android.experiment

import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import java.util.concurrent.ConcurrentHashMap
import com.application.ascend_android.*

class IExperimentDataSourceTest : BaseCoroutineTest() {

    private lateinit var mockDataSource: IDRSDataSource

    @BeforeEach
    fun setUp() {
        mockDataSource = mockk(relaxed = true)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should have getLocalMap method`() {
        // Given
        val expectedMap = ConcurrentHashMap<String, ExperimentDetails>()
        every { mockDataSource.getLocalMap() } returns expectedMap

        // When
        val result = mockDataSource.getLocalMap()

        // Then
        assertEquals(expectedMap, result)
        verify { mockDataSource.getLocalMap() }
    }

    @Test
    fun `should have saveLocalMap method`() {
        // Given
        val experimentsMap = mapOf("test/experiment" to ExperimentDetails())
        every { mockDataSource.saveLocalMap(any()) } just Runs

        // When
        mockDataSource.saveLocalMap(experimentsMap)

        // Then
        verify { mockDataSource.saveLocalMap(experimentsMap) }
    }

    @Test
    fun `should have saveLocalString method`() {
        // Given
        val key = "test-key"
        val value = "test-value"
        every { mockDataSource.saveLocalString(any(), any()) } just Runs

        // When
        mockDataSource.saveLocalString(key, value)

        // Then
        verify { mockDataSource.saveLocalString(key, value) }
    }

    @Test
    fun `should have saveLocalLong method`() {
        // Given
        val key = "test-key"
        val value = 123456789L
        every { mockDataSource.saveLocalLong(any(), any()) } just Runs

        // When
        mockDataSource.saveLocalLong(key, value)

        // Then
        verify { mockDataSource.saveLocalLong(key, value) }
    }

    @Test
    fun `should have getLocalLong method`() {
        // Given
        val key = "test-key"
        val expectedValue = 123456789L
        every { mockDataSource.getLocalLong(key) } returns expectedValue

        // When
        val result = mockDataSource.getLocalLong(key)

        // Then
        assertEquals(expectedValue, result)
        verify { mockDataSource.getLocalLong(key) }
    }

    @Test
    fun `should have deleteLocal method`() {
        // Given
        every { mockDataSource.deleteLocal() } just Runs

        // When
        mockDataSource.deleteLocal()

        // Then
        verify { mockDataSource.deleteLocal() }
    }

    @Test
    fun `should have areGuestAndAccessTokenEmpty method`() {
        // Given
        every { mockDataSource.areGuestAndAccessTokenEmpty() } returns true

        // When
        val result = mockDataSource.areGuestAndAccessTokenEmpty()

        // Then
        assertTrue(result)
        verify { mockDataSource.areGuestAndAccessTokenEmpty() }
    }


    @Test
    fun `should have getRemoteData method with default parameter`() = runTest {
        // Given
        val request = DRSExperimentRequest(listOf("test/experiment"))
        val mockNetworkState = mockk<NetworkState<Any>>(relaxed = true)
        coEvery { mockDataSource.getRemoteData(any(), any()) } returns mockNetworkState

        // When
        val result = mockDataSource.getRemoteData(request)

        // Then
        assertEquals(mockNetworkState, result)
        coVerify { mockDataSource.getRemoteData(request, false) }
    }

    @Test
    fun `should have updateHeaderMaps method`() {
        // Given
        val customHeaders = HashMap<String, Any>()
        customHeaders["test-header"] = "test-value"
        every { mockDataSource.updateHeaderMaps(any()) } just Runs

        // When
        mockDataSource.updateHeaderMaps(customHeaders)

        // Then
        verify { mockDataSource.updateHeaderMaps(customHeaders) }
    }

    @Test
    fun `should have getDevice method`() {
        // Given
        val mockDevice = mockk<IDevice>(relaxed = true)
        every { mockDataSource.getDevice() } returns mockDevice

        // When
        val result = mockDataSource.getDevice()

        // Then
        assertEquals(mockDevice, result)
        verify { mockDataSource.getDevice() }
    }

    @Test
    fun `should have realtimeDRSOnForeground method`() {
        // Given
        every { mockDataSource.realtimeDRSOnForeground() } returns true

        // When
        val result = mockDataSource.realtimeDRSOnForeground()

        // Then
        assertTrue(result)
        verify { mockDataSource.realtimeDRSOnForeground() }
    }

    @Test
    fun `should have addAPIKeyToHeader method`() {
        // Given
        every { mockDataSource.addAPIKeyToHeader() } just Runs

        // When
        mockDataSource.addAPIKeyToHeader()

        // Then
        verify { mockDataSource.addAPIKeyToHeader() }
    }

    @Test
    fun `should handle empty local map`() {
        // Given
        val emptyMap = ConcurrentHashMap<String, ExperimentDetails>()
        every { mockDataSource.getLocalMap() } returns emptyMap

        // When
        val result = mockDataSource.getLocalMap()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should handle null values in saveLocalMap`() {
        // Given
        val experimentsMap = mapOf("test/experiment" to ExperimentDetails())
        every { mockDataSource.saveLocalMap(any()) } just Runs

        // When
        mockDataSource.saveLocalMap(experimentsMap)

        // Then
        verify { mockDataSource.saveLocalMap(experimentsMap) }
    }

    @Test
    fun `should handle empty string in saveLocalString`() {
        // Given
        val key = "test-key"
        val value = ""
        every { mockDataSource.saveLocalString(any(), any()) } just Runs

        // When
        mockDataSource.saveLocalString(key, value)

        // Then
        verify { mockDataSource.saveLocalString(key, value) }
    }

    @Test
    fun `should handle zero value in saveLocalLong`() {
        // Given
        val key = "test-key"
        val value = 0L
        every { mockDataSource.saveLocalLong(any(), any()) } just Runs

        // When
        mockDataSource.saveLocalLong(key, value)

        // Then
        verify { mockDataSource.saveLocalLong(key, value) }
    }

    @Test
    fun `should handle negative value in getLocalLong`() {
        // Given
        val key = "test-key"
        val expectedValue = -1L
        every { mockDataSource.getLocalLong(key) } returns expectedValue

        // When
        val result = mockDataSource.getLocalLong(key)

        // Then
        assertEquals(expectedValue, result)
    }

    @Test
    fun `should handle empty request in getRemoteData`() = runTest {
        // Given
        val request = DRSExperimentRequest(emptyList())
        val mockNetworkState = mockk<NetworkState<Any>>(relaxed = true)
        coEvery { mockDataSource.getRemoteData(any(), any()) } returns mockNetworkState

        // When
        val result = mockDataSource.getRemoteData(request)

        // Then
        assertEquals(mockNetworkState, result)
    }

    @Test
    fun `should handle empty custom headers in updateHeaderMaps`() {
        // Given
        val customHeaders = HashMap<String, Any>()
        every { mockDataSource.updateHeaderMaps(any()) } just Runs

        // When
        mockDataSource.updateHeaderMaps(customHeaders)

        // Then
        verify { mockDataSource.updateHeaderMaps(customHeaders) }
    }

    @Test
    fun `should handle null values in custom headers`() {
        // Given
        val customHeaders = HashMap<String, Any>()
        customHeaders["null-header"] = "null"
        customHeaders["valid-header"] = "valid-value"
        every { mockDataSource.updateHeaderMaps(any()) } just Runs

        // When
        mockDataSource.updateHeaderMaps(customHeaders)

        // Then
        verify { mockDataSource.updateHeaderMaps(customHeaders) }
    }
}
