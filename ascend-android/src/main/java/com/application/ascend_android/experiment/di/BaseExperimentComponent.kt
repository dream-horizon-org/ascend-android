package com.application.ascend_android

interface BaseDRSComponent<T> {
    fun inject(target: T)
}