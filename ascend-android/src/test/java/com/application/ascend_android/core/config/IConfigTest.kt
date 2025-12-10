package com.application.ascend_android.core.config

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class IConfigTest {

    @Test
    fun `should have correct NetworkRetrialPolicy enum values`() {
        // Then
        assertEquals("LINEAR", NetworkRetrialPolicy.LINEAR.name)
        assertEquals("INCREMENTAL", NetworkRetrialPolicy.INCREMENTAL.name)
        assertEquals("QUADRATIC", NetworkRetrialPolicy.QUADRATIC.name)
    }

    @Test
    fun `should have correct RequestType enum values`() {
        // Then
        assertEquals("FETCH_EXPERIMENTS", RequestType.FETCH_EXPERIMENTS.name)
        assertEquals("FETCH_FEATURE_FLAGS", RequestType.FETCH_FEATURE_FLAGS.name)
        assertEquals("FETCH_FEATURE_FLAGS_POST", RequestType.FETCH_FEATURE_FLAGS_POST.name)
        assertEquals("POST_EVENTS", RequestType.POST_EVENTS.name)
        assertEquals("EVENTS_CLIENT_CONFIG", RequestType.EVENTS_CLIENT_CONFIG.name)
        assertEquals("CHECK_SESSION", RequestType.CHECK_SESSION.name)
        assertEquals("MARK_CONSENT", RequestType.MARK_CONSENT.name)
        assertEquals("AUTHORIZE", RequestType.AUTHORIZE.name)
        assertEquals("GET_TOKENS", RequestType.GET_TOKENS.name)
    }

    @Test
    fun `should have correct PluggerEvents enum values`() {
        // Then
        assertEquals("Plugger_User_Logged_In", PluggerEvents.USER_LOGGED_IN.value)
        assertEquals("Plugger_User_Logged_Out", PluggerEvents.USER_LOGGED_OUT.value)
    }

    @Test
    fun `should have correct Plugins enum values`() {
        // Then
        assertEquals("EventsPlugin", Plugins.EVENTS.pluginName)
        assertEquals("DRSPlugin", Plugins.EXPERIMENTS.pluginName)
    }

    @Test
    fun `should test IConfigProvider interface`() {
        // Given
        val mockConfigProvider = mockk<IConfigProvider>()
        val mockHttpConfig = mockk<IHttpConfigProvider>()
        val pluginType = Plugins.EVENTS

        every { mockConfigProvider.httpConfig() } returns mockHttpConfig
        every { mockConfigProvider.pluginType() } returns pluginType

        // When
        val httpResult = mockConfigProvider.httpConfig()
        val pluginResult = mockConfigProvider.pluginType()

        // Then
        assertEquals(mockHttpConfig, httpResult)
        assertEquals(pluginType, pluginResult)
    }

    @Test
    fun `should test IHttpConfigProvider interface`() {
        // Given
        val mockHttpConfig = mockk<IHttpConfigProvider>()
        val mockHeaderMap = mapOf("Content-Type" to "application/json")
        val baseUrl = "https://api.example.com"
        val fetchInterval = 5000L
        val mockTimeoutConfig = mockk<ITimeoutConfig>()
        val mockRetryConfig = mockk<IRetryConfig>()

        every { mockHttpConfig.getHeaderMap() } returns mockHeaderMap
        every { mockHttpConfig.shouldRetry() } returns true
        every { mockHttpConfig.getBaseUrl() } returns baseUrl
        every { mockHttpConfig.fetchInterval() } returns fetchInterval
        every { mockHttpConfig.timeOutConfig() } returns mockTimeoutConfig
        every { mockHttpConfig.retrialConfig() } returns mockRetryConfig
        every { mockHttpConfig.updateHeaderMap(any(), any()) } just Runs
        every { mockHttpConfig.removeKeyFromHeaderMap(any()) } just Runs

        // When
        val headerResult = mockHttpConfig.getHeaderMap()
        val retryResult = mockHttpConfig.shouldRetry()
        val urlResult = mockHttpConfig.getBaseUrl()
        val intervalResult = mockHttpConfig.fetchInterval()
        val timeoutResult = mockHttpConfig.timeOutConfig()
        val retrialResult = mockHttpConfig.retrialConfig()

        // Then
        assertEquals(mockHeaderMap, headerResult)
        assertTrue(retryResult)
        assertEquals(baseUrl, urlResult)
        assertEquals(fetchInterval, intervalResult)
        assertEquals(mockTimeoutConfig, timeoutResult)
        assertEquals(mockRetryConfig, retrialResult)

        // Verify methods can be called
        assertDoesNotThrow {
            mockHttpConfig.updateHeaderMap("key", "value")
            mockHttpConfig.removeKeyFromHeaderMap("key")
        }
    }

    @Test
    fun `should test ITimeoutConfig interface`() {
        // Given
        val mockTimeoutConfig = mockk<ITimeoutConfig>()
        val timeout = 10000L
        val enableLogging = true

        every { mockTimeoutConfig.callTimeout() } returns timeout
        every { mockTimeoutConfig.shouldEnableLogging() } returns enableLogging

        // When
        val timeoutResult = mockTimeoutConfig.callTimeout()
        val loggingResult = mockTimeoutConfig.shouldEnableLogging()

        // Then
        assertEquals(timeout, timeoutResult)
        assertEquals(enableLogging, loggingResult)
    }

    @Test
    fun `should test IRetryConfig interface`() {
        // Given
        val mockRetryConfig = mockk<IRetryConfig>()
        val mockDelay = mockk<IDelay>()
        val maxLimit = 3
        val omittedCodes = hashSetOf(404, 500)

        every { mockRetryConfig.maxLimit() } returns maxLimit
        every { mockRetryConfig.delay() } returns mockDelay
        every { mockRetryConfig.omittedRetrialCodes() } returns omittedCodes

        // When
        val limitResult = mockRetryConfig.maxLimit()
        val delayResult = mockRetryConfig.delay()
        val codesResult = mockRetryConfig.omittedRetrialCodes()

        // Then
        assertEquals(maxLimit, limitResult)
        assertEquals(mockDelay, delayResult)
        assertEquals(omittedCodes, codesResult)
    }

    @Test
    fun `should test IDelay interface`() {
        // Given
        val mockDelay = mockk<IDelay>()
        val delayTime = 1000L
        val retryPolicy = NetworkRetrialPolicy.LINEAR

        every { mockDelay.delayTime() } returns delayTime
        every { mockDelay.retrialPolicy() } returns retryPolicy

        // When
        val timeResult = mockDelay.delayTime()
        val policyResult = mockDelay.retrialPolicy()

        // Then
        assertEquals(delayTime, timeResult)
        assertEquals(retryPolicy, policyResult)
    }

    @Test
    fun `should test IClientConfigProvider interface`() {
        // Given
        val mockClientConfig = mockk<IClientConfigProvider>()
        val apiKey = "test_api_key"

        every { mockClientConfig.clientApiKey() } returns apiKey

        // When
        val keyResult = mockClientConfig.clientApiKey()

        // Then
        assertEquals(apiKey, keyResult)
    }

    @Test
    fun `should test IPlugin interface`() {
        // Given
        val mockPlugin = mockk<IPlugin>()
        val mockContext = mockk<android.content.Context>()
        val mockConfigProvider = mockk<IConfigProvider>()
        val event = PluggerEvents.USER_LOGGED_IN
        val data = "test_data"

        every { mockPlugin.init(mockContext, mockConfigProvider) } just Runs
        every { mockPlugin.onNotify(event, data) } just Runs

        // When
        mockPlugin.init(mockContext, mockConfigProvider)
        mockPlugin.onNotify(event, data)

        // Then
        verify { mockPlugin.init(mockContext, mockConfigProvider) }
        verify { mockPlugin.onNotify(event, data) }
    }

    @Test
    fun `should test IPluginConfig interface`() {
        // Given
        val mockPluginConfig = mockk<IPluginConfig>()
        val mockFunction = mockk<kotlin.reflect.KFunction0<Any>>()
        val pluginName = "TestPlugin"
        val mockConfigProvider = mockk<IConfigProvider>()

        every { mockPluginConfig.pluginType() } returns mockFunction
        every { mockPluginConfig.pluginName() } returns pluginName
        every { mockPluginConfig.configProvider() } returns mockConfigProvider

        // When
        val functionResult = mockPluginConfig.pluginType()
        val nameResult = mockPluginConfig.pluginName()
        val configResult = mockPluginConfig.configProvider()

        // Then
        assertEquals(mockFunction, functionResult)
        assertEquals(pluginName, nameResult)
        assertEquals(mockConfigProvider, configResult)
    }

    @Test
    fun `should test IPluggerConfig interface`() {
        // Given
        val mockPluggerConfig = mockk<IPluggerConfig>()
        val mockHttpConfig = mockk<IHttpConfigProvider>()
        val mockClientConfig = mockk<IClientConfigProvider>()
        val plugins = arrayListOf<IPluginConfig>()

        every { mockPluggerConfig.httpConfig() } returns mockHttpConfig
        every { mockPluggerConfig.plugins() } returns plugins
        every { mockPluggerConfig.pluggerClientConfig() } returns mockClientConfig

        // When
        val httpResult = mockPluggerConfig.httpConfig()
        val pluginsResult = mockPluggerConfig.plugins()
        val clientResult = mockPluggerConfig.pluggerClientConfig()

        // Then
        assertEquals(mockHttpConfig, httpResult)
        assertEquals(plugins, pluginsResult)
        assertEquals(mockClientConfig, clientResult)
    }
}
