package com.application.ascend_android

import android.content.Context

class PluggerCapabilities {

    companion object{

        lateinit var daggerPluggerComponent : PluggerComponent

        fun init(context : Context, pluggerConfig: AscendConfig){

            daggerPluggerComponent = DaggerPluggerComponent.builder()
                .pluggerModule(
                    PluggerModule(
                        context,
                        pluggerConfig
                    )
                )
                .build()

        }

    }
}

