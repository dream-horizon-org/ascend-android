package com.application.ascend_android

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DRSModule::class])
interface DRSComponent : BaseDRSComponent<DRSPlugin> {

    @Component.Builder
    interface Builder {
        fun build(): DRSComponent
        fun drsModule(drsModule: DRSModule): Builder
    }


    fun provideExperimentService(): BaseExperimentService
}