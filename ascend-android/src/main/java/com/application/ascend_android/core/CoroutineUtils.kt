package com.application.ascend_android

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CoroutineUtils {
    companion object {
        fun launchCoroutine(block: suspend CoroutineScope.() -> Unit) {
            val job = Dispatchers.IO
            CoroutineScope(job).launch {
                block.invoke(this)
            }.start()
        }

        fun launchRunBlocking(block: suspend CoroutineScope.() -> Unit) {
            val job = Dispatchers.IO
            runBlocking {
                CoroutineScope(job).launch {
                    block.invoke(this)
                }.start()
            }

        }


    }
}