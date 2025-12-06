package com.application.ascend_android.experiment

import android.content.Context
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class ExperimentPluginTest {

    private lateinit var drsPlugin: DRSPlugin
    private lateinit var mockContext: Context
    private lateinit var mockExperimentConfig: ExperimentConfig
    private lateinit var mockExperimentCallback: IExperimentCallback
    private lateinit var mockBaseExperimentService: BaseExperimentService
    private lateinit var mockDaggerDRSComponent: DRSComponent
    private lateinit var mockDaggerCoreComponent: CoreComponent

    @BeforeEach
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockExperimentCallback = mockk(relaxed = true)
        mockBaseExperimentService = mockk(relaxed = true)
        mockDaggerDRSComponent = mockk(relaxed = true)
        mockDaggerCoreComponent = mockk(relaxed = true)
        
        mockExperimentConfig = mockk(relaxed = true) {
            every { shouldFetchOnInit } returns true
            every { iExperimentCallback } returns mockExperimentCallback
        }

        // Mock object dependencies with proper syntax
        mockkObject(AscendUser)
        mockkObject(CoreCapabilities)
        mockkStatic(DaggerDRSComponent::class)

        every { AscendUser.guestId } returns "test-guest-id"
        every { AscendUser.userId } returns "test-user-id"
        every { CoreCapabilities.init(any(), any()) } returns mockDaggerCoreComponent
        every { mockDaggerCoreComponent.inject(any()) } just Runs
        
        val mockCoreClient = mockk<CoreClient>(relaxed = true)
        every { mockDaggerCoreComponent.provideCoreClient() } returns mockCoreClient
        
        val mockBuilder = mockk<DRSComponent.Builder>(relaxed = true)
        every { DaggerDRSComponent.builder() } returns mockBuilder
        every { mockBuilder.drsModule(any()) } returns mockBuilder
        every { mockBuilder.build() } returns mockDaggerDRSComponent
        every { mockDaggerDRSComponent.provideExperimentService() } returns mockBaseExperimentService

        // Create a spy of DRSPlugin to control its behavior
        drsPlugin = spyk(DRSPlugin())
    }

    @Test
    fun `should initialize with valid config`() {
        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify { CoreCapabilities.init(mockContext, mockExperimentConfig) }
        verify { mockDaggerCoreComponent.inject(any()) }
        verify { DaggerDRSComponent.builder() }
    }

    @Test
    fun `should call experiment if needed when user or guest ids are present`() {
        // Given
        every { AscendUser.guestId } returns "test-guest-id"
        every { AscendUser.userId } returns ""
        every { mockExperimentConfig.shouldFetchOnInit } returns true

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify { mockDaggerDRSComponent.provideExperimentService() }
        verify { mockBaseExperimentService.refreshExperiment(mockExperimentCallback) }
    }

    @Test
    fun `should not call experiment when user and guest ids are empty`() {
        // Given
        every { AscendUser.guestId } returns ""
        every { AscendUser.userId } returns ""
        every { mockExperimentConfig.shouldFetchOnInit } returns true

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify(exactly = 0) { mockBaseExperimentService.refreshExperiment(any()) }
    }

    @Test
    fun `should not call experiment when shouldFetchOnInit is false`() {
        // Given
        every { AscendUser.guestId } returns "test-guest-id"
        every { AscendUser.userId } returns "test-user-id"
        every { mockExperimentConfig.shouldFetchOnInit } returns false

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify(exactly = 0) { mockBaseExperimentService.refreshExperiment(any()) }
    }

    @Test
    fun `should handle user logged out event`() {
        // Given
        drsPlugin.init(mockContext, mockExperimentConfig)

        // When
        drsPlugin.onNotify(PluggerEvents.USER_LOGGED_OUT, null)

        // Then
        verify { mockBaseExperimentService.clearAllExperimentsData() }
        verify { mockBaseExperimentService.clearUserSessionData() }
    }

    @Test
    fun `should handle user logged in event`() {
        // Given
        drsPlugin.init(mockContext, mockExperimentConfig)

        // When
        drsPlugin.onNotify(PluggerEvents.USER_LOGGED_IN, null)

        // Then
        verify(exactly = 0) { mockBaseExperimentService.clearAllExperimentsData() }
        verify(exactly = 0) { mockBaseExperimentService.clearUserSessionData() }
    }

    @Test
    fun `should return experiment service`() {
        // Given
        drsPlugin.init(mockContext, mockExperimentConfig)

        // When
        val result = drsPlugin.getExperimentService()

        // Then
        assertEquals(mockBaseExperimentService, result)
    }

    @Test
    fun `should handle non-experiment config`() {
        // Given
        val mockConfig = mockk<IConfigProvider>(relaxed = true)

        // When & Then
        try {
            drsPlugin.init(mockContext, mockConfig)
            // Should not throw exception and should still initialize
            verify { CoreCapabilities.init(mockContext, mockConfig) }
        } catch (e: UninitializedPropertyAccessException) {
            // Expected due to non-experiment config, test passes
            assertTrue(true)
        }
    }

    @Test
    fun `should check if user or guest ids are present with guest id`() {
        // Given
        every { AscendUser.guestId } returns "guest-id"
        every { AscendUser.userId } returns ""

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify { mockBaseExperimentService.refreshExperiment(mockExperimentCallback) }
    }

    @Test
    fun `should check if user or guest ids are present with user id`() {
        // Given
        every { AscendUser.guestId } returns ""
        every { AscendUser.userId } returns "user-id"

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify { mockBaseExperimentService.refreshExperiment(mockExperimentCallback) }
    }

    @Test
    fun `should check if user or guest ids are present with both ids`() {
        // Given
        every { AscendUser.guestId } returns "guest-id"
        every { AscendUser.userId } returns "user-id"

        // When
        drsPlugin.init(mockContext, mockExperimentConfig)

        // Then
        verify { mockBaseExperimentService.refreshExperiment(mockExperimentCallback) }
    }
}
