package com.application.ascend_android

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DRSModule(private val coreClient: CoreClient, private val config: Config) {

    @Provides
    @Singleton
    fun provideModuleProvider(): IModuleProvider = ModuleProvider(coreClient, config)

    @Provides
    @Singleton
    fun provideDataSource(moduleProvider: IModuleProvider): IDRSDataSource =
        ExperimentRepository(moduleProvider)

    @Provides
    @Singleton
    internal fun provideExperimentMediator(
        drsDataSource: IDRSDataSource,
        moduleProvider: IModuleProvider
    ): ExperimentMediator = ExperimentMediator(drsDataSource, moduleProvider)


    @Provides
    @Singleton
    internal fun provideExperimentService(mediator: ExperimentMediator): BaseExperimentService =
        DRSExperimentService(mediator)
}