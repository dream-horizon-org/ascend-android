package com.application.ascend_android
import android.content.Context
class CoreCapabilities {

    companion object {
        lateinit var daggerCoreComponent: CoreComponent
        fun init(context: Context, config: IConfigProvider): CoreComponent {
            daggerCoreComponent = DaggerCoreComponent.builder().serviceModule(
                ServiceModule(context)
            ).databaseModule(DatabaseModule(context))
                .networkModule(NetworkModule(config, context)).build()

            return daggerCoreComponent

        }
    }


}