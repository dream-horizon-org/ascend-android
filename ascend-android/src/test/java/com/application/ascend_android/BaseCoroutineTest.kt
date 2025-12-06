package com.application.ascend_android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseCoroutineTest {
    
    @BeforeEach
    fun setUpCoroutines() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDownCoroutines() {
        Dispatchers.resetMain()
    }
}

