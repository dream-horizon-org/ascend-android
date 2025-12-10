package com.application.ascend_android.core

import android.content.Context
import com.google.gson.Gson
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.CoroutineScope
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class CoreClientTest : BaseCoroutineTest() {

    private lateinit var coreClient: CoreClient
    private lateinit var mockContext: Context
    private lateinit var mockDatabaseInjector: DatabaseInjector
    private lateinit var mockGson: Gson
    private lateinit var mockNetworkClient: NetworkClient
    private lateinit var mockConfigProvider: IConfigProvider

    @BeforeEach
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockDatabaseInjector = mockk(relaxed = true)
        mockGson = mockk(relaxed = true)
        mockNetworkClient = mockk(relaxed = true)
        mockConfigProvider = mockk(relaxed = true)

        coreClient = CoreClient()
        coreClient.context = mockContext
        coreClient.databaseInjector = mockDatabaseInjector
        coreClient.gson = mockGson
        coreClient.networkClient = mockNetworkClient
        coreClient.configProvider = mockConfigProvider
    }

    @Test
    fun `should initialize with injected dependencies`() {
        // Then
        assertNotNull(coreClient.context)
        assertNotNull(coreClient.databaseInjector)
        assertNotNull(coreClient.gson)
        assertNotNull(coreClient.networkClient)
        assertNotNull(coreClient.configProvider)
    }

    @Test
    fun `should get network data successfully`() = runTest {
        // Given
        val request = mockk<Request>()
        val expectedResponse = mockk<NetworkState<Any>>()
        coEvery { mockNetworkClient.getData(request) } returns expectedResponse

        // When
        val result = coreClient.getNetworkData(request)

        // Then
        assertEquals(expectedResponse, result)
        coVerify { mockNetworkClient.getData(request) }
    }

    @Test
    fun `should get local data successfully`() {
        // Given
        val key = "test_key"
        val expectedData = "test_data"
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.getString(key) } returns expectedData

        // When
        val result = coreClient.getLocalData(key)

        // Then
        assertEquals(expectedData, result)
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.getString(key) }
    }

    @Test
    fun `should get empty string when local data is null`() {
        // Given
        val key = "test_key"
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.getString(key) } returns null

        // When
        val result = coreClient.getLocalData(key)

        // Then
        assertEquals("", result)
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.getString(key) }
    }

    @Test
    fun `should save local data successfully`() {
        // Given
        val key = "test_key"
        val value = "test_value"
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.setString(key, value) } just Runs

        // When
        coreClient.saveLocalData(key, value)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.setString(key, value) }
    }

    @Test
    fun `should save local long successfully`() {
        // Given
        val key = "test_key"
        val value = 12345L
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.setLong(key, value) } just Runs

        // When
        coreClient.saveLocalLong(key, value)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.setLong(key, value) }
    }

    @Test
    fun `should get local long successfully`() {
        // Given
        val key = "test_key"
        val expectedValue = 12345L
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.getLong(key) } returns expectedValue

        // When
        val result = coreClient.getLocalLong(key)

        // Then
        assertEquals(expectedValue, result)
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.getLong(key) }
    }

    @Test
    fun `should remove local data successfully`() {
        // Given
        val key = "test_key"
        val mockSharedPrefTable = mockk<com.application.ascend_android.SharedPrefTable>()
        every { mockDatabaseInjector.getSharedPrefTable() } returns mockSharedPrefTable
        every { mockSharedPrefTable.remove(key) } just Runs

        // When
        coreClient.removeLocalData(key)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        verify { mockSharedPrefTable.remove(key) }
    }

    @Test
    fun `should handle network client injection`() {
        // Given
        val newNetworkClient = mockk<NetworkClient>()
        
        // When
        coreClient.networkClient = newNetworkClient

        // Then
        assertEquals(newNetworkClient, coreClient.networkClient)
    }

    @Test
    fun `should handle config provider injection`() {
        // Given
        val newConfigProvider = mockk<IConfigProvider>()
        
        // When
        coreClient.configProvider = newConfigProvider

        // Then
        assertEquals(newConfigProvider, coreClient.configProvider)
    }

    @Test
    fun `should handle database injector injection`() {
        // Given
        val newDatabaseInjector = mockk<DatabaseInjector>()
        
        // When
        coreClient.databaseInjector = newDatabaseInjector

        // Then
        assertEquals(newDatabaseInjector, coreClient.databaseInjector)
    }

    @Test
    fun `should handle gson injection`() {
        // Given
        val newGson = mockk<Gson>()
        
        // When
        coreClient.gson = newGson

        // Then
        assertEquals(newGson, coreClient.gson)
    }

    @Test
    fun `should handle context injection`() {
        // Given
        val newContext = mockk<Context>()
        
        // When
        coreClient.context = newContext

        // Then
        assertEquals(newContext, coreClient.context)
    }

    @Test
    fun `should insert event successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val mockEvent = mockk<DBEvents>()
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.insert(mockEvent) } just Runs

        // When
        coreClient.insert(mockCoroutineScope, mockEvent)

        // Then
        coVerify { mockEventDao.insert(mockEvent) }
    }

    @Test
    fun `should insert all events successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val mockEvents = listOf(mockk<DBEvents>(), mockk<DBEvents>())
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.insertAll(mockEvents) } just Runs

        // When
        coreClient.insertAll(mockCoroutineScope, mockEvents)

        // Then
        coVerify { mockEventDao.insertAll(mockEvents) }
    }

    @Test
    fun `should insert all events conditionally successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val mockEvents = listOf(mockk<DBEvents>(), mockk<DBEvents>())
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.insertAll(mockEvents) } just Runs

        // When
        coreClient.insertAllConditionally(mockCoroutineScope, mockEvents)

        // Then
        coVerify { mockEventDao.insertAll(mockEvents) }
    }

    @Test
    fun `should retrieve all events with filter successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val filter = "test_filter"
        val eventIds = arrayListOf("id1", "id2")
        val mockEvents = listOf(mockk<DBEvents>(), mockk<DBEvents>())
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.getAllEventsWithFilter(filter, eventIds) } returns mockEvents

        // When
        val result = coreClient.retrieveAll(mockCoroutineScope, filter, eventIds)

        // Then
        assertEquals(mockEvents, result)
        coVerify { mockEventDao.getAllEventsWithFilter(filter, eventIds) }
    }

    @Test
    fun `should return empty list when retrieve all events returns empty`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val filter = "test_filter"
        val eventIds = arrayListOf("id1", "id2")
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.getAllEventsWithFilter(filter, eventIds) } returns emptyList()

        // When
        val result = coreClient.retrieveAll(mockCoroutineScope, filter, eventIds)

        // Then
        assertTrue(result.isEmpty())
        coVerify { mockEventDao.getAllEventsWithFilter(filter, eventIds) }
    }

    @Test
    fun `should delete all events successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val expectedCount = 5
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.deleteAll() } returns expectedCount

        // When
        val result = coreClient.deleteAll(mockCoroutineScope)

        // Then
        assertEquals(expectedCount, result)
        coVerify { mockEventDao.deleteAll() }
    }

    @Test
    fun `should delete all events conditionally successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val eventIds = arrayListOf("id1", "id2")
        val filter = "test_filter"
        val status = 1
        val expectedCount = 3
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.deleterConditionally(eventIds, filter, status) } returns expectedCount

        // When
        val result = coreClient.deleteAll(mockCoroutineScope, eventIds, filter, status)

        // Then
        assertEquals(expectedCount, result)
        coVerify { mockEventDao.deleterConditionally(eventIds, filter, status) }
    }

    @Test
    fun `should delete events without status successfully`() = runTest {
        // Given
        val mockCoroutineScope = mockk<CoroutineScope>()
        val eventIds = arrayListOf("id1", "id2")
        val filter = "test_filter"
        val expectedCount = 2
        val mockDatabase = mockk<EventDatabase>()
        val mockEventDao = mockk<EventDao>()
        
        every { mockDatabaseInjector.getDataBase(mockCoroutineScope) } returns mockDatabase
        every { mockDatabase.eventDao() } returns mockEventDao
        coEvery { mockEventDao.deleterWithoutStatus(eventIds, filter) } returns expectedCount

        // When
        val result = coreClient.deleteWithoutStatus(mockCoroutineScope, eventIds, filter)

        // Then
        assertEquals(expectedCount, result)
        coVerify { mockEventDao.deleterWithoutStatus(eventIds, filter) }
    }

    @Test
    fun `should handle null shared pref table in getLocalData`() {
        // Given
        val key = "test_key"
        every { mockDatabaseInjector.getSharedPrefTable() } returns null

        // When
        val result = coreClient.getLocalData(key)

        // Then
        assertEquals("", result)
        verify { mockDatabaseInjector.getSharedPrefTable() }
    }

    @Test
    fun `should handle null shared pref table in getLocalLong`() {
        // Given
        val key = "test_key"
        every { mockDatabaseInjector.getSharedPrefTable() } returns null

        // When
        val result = coreClient.getLocalLong(key)

        // Then
        assertEquals(0L, result)
        verify { mockDatabaseInjector.getSharedPrefTable() }
    }

    @Test
    fun `should handle null shared pref table in saveLocalData`() {
        // Given
        val key = "test_key"
        val value = "test_value"
        every { mockDatabaseInjector.getSharedPrefTable() } returns null

        // When
        coreClient.saveLocalData(key, value)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        // Should not throw exception
    }

    @Test
    fun `should handle null shared pref table in saveLocalLong`() {
        // Given
        val key = "test_key"
        val value = 12345L
        every { mockDatabaseInjector.getSharedPrefTable() } returns null

        // When
        coreClient.saveLocalLong(key, value)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        // Should not throw exception
    }

    @Test
    fun `should handle null shared pref table in removeLocalData`() {
        // Given
        val key = "test_key"
        every { mockDatabaseInjector.getSharedPrefTable() } returns null

        // When
        coreClient.removeLocalData(key)

        // Then
        verify { mockDatabaseInjector.getSharedPrefTable() }
        // Should not throw exception
    }
}
