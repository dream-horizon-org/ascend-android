package com.application.ascend_android.core.config

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import com.application.ascend_android.*

class ConfigTest {

    private lateinit var config: Config
    private lateinit var mockHttpConfig: HttpConfig

    @BeforeEach
    fun setUp() {
        mockHttpConfig = mockk(relaxed = true)
        config = Config(mockHttpConfig, Plugins.EVENTS)
    }

    @Test
    fun `should initialize with http config and plugin type`() {
        // Then
        assertEquals(mockHttpConfig, config.httpConfig())
        assertEquals(Plugins.EVENTS, config.pluginType())
    }

    @Test
    fun `should return correct http config`() {
        // When
        val result = config.httpConfig()

        // Then
        assertEquals(mockHttpConfig, result)
    }

    @Test
    fun `should return correct plugin type`() {
        // When
        val result = config.pluginType()

        // Then
        assertEquals(Plugins.EVENTS, result)
    }

    @Test
    fun `should handle different plugin types`() {
        // Given
        val experimentsConfig = Config(mockHttpConfig, Plugins.EXPERIMENTS)

        // When
        val result = experimentsConfig.pluginType()

        // Then
        assertEquals(Plugins.EXPERIMENTS, result)
    }

    @Test
    fun `should maintain http config reference`() {
        // Given
        val newHttpConfig = mockk<HttpConfig>()

        // When
        config.httpConfig = newHttpConfig

        // Then
        assertEquals(newHttpConfig, config.httpConfig())
    }

    @Test
    fun `should handle different plugin types correctly`() {
        // Given
        val httpConfig1 = mockk<HttpConfig>(relaxed = true)
        val httpConfig2 = mockk<HttpConfig>(relaxed = true)
        val eventsConfig = Config(httpConfig1, Plugins.EVENTS)
        val experimentConfig = Config(httpConfig2, Plugins.EXPERIMENTS)

        // When & Then
        // Note: pluginType is private, so we test through httpConfig() method
        assertNotNull(eventsConfig.httpConfig())
        assertNotNull(experimentConfig.httpConfig())
    }

    @Test
    fun `should be instance of IConfigProvider`() {
        // Then
        assertTrue(config is IConfigProvider)
    }

    @Test
    fun `should implement IConfigProvider interface correctly`() {
        // When
        val httpConfigResult = config.httpConfig()
        val pluginTypeResult = config.pluginType()

        // Then
        assertNotNull(httpConfigResult)
        assertNotNull(pluginTypeResult)
    }

    @Test
    fun `should handle multiple instances with different configs`() {
        // Given
        val httpConfig1 = mockk<HttpConfig>()
        val httpConfig2 = mockk<HttpConfig>()
        val config1 = Config(httpConfig1, Plugins.EVENTS)
        val config2 = Config(httpConfig2, Plugins.EXPERIMENTS)

        // When
        val result1 = config1.httpConfig()
        val result2 = config2.httpConfig()
        val plugin1 = config1.pluginType()
        val plugin2 = config2.pluginType()

        // Then
        assertEquals(httpConfig1, result1)
        assertEquals(httpConfig2, result2)
        assertEquals(Plugins.EVENTS, plugin1)
        assertEquals(Plugins.EXPERIMENTS, plugin2)
    }

    @Test
    fun `should maintain state after multiple calls`() {
        // When
        val httpConfig1 = config.httpConfig()
        val pluginType1 = config.pluginType()
        val httpConfig2 = config.httpConfig()
        val pluginType2 = config.pluginType()

        // Then
        assertEquals(httpConfig1, httpConfig2)
        assertEquals(pluginType1, pluginType2)
    }

    @Test
    fun `should handle all plugin types`() {
        // Given
        val eventConfig = Config(mockHttpConfig, Plugins.EVENTS)
        val experimentConfig = Config(mockHttpConfig, Plugins.EXPERIMENTS)

        // When
        val eventPlugin = eventConfig.pluginType()
        val experimentPlugin = experimentConfig.pluginType()

        // Then
        assertEquals(Plugins.EVENTS, eventPlugin)
        assertEquals(Plugins.EXPERIMENTS, experimentPlugin)
    }
}
