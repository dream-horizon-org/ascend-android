package com.application.ascend_android

interface BaseComponent<T> {
    fun inject(target : T)
}