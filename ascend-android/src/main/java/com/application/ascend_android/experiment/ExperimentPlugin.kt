package com.application.ascend_android

import android.content.Context

class DRSPlugin : IPlugin {

    private lateinit var daggerDRSComponent: DRSComponent

    private lateinit var daggerCoreComponent: CoreComponent

    private lateinit var experimentConfig: ExperimentConfig

    private lateinit var baseExperimentService: BaseExperimentService

    private fun callExperimentIfNeeded(context: Context) {
        if (experimentConfig.shouldFetchOnInit && checkIfUserOrStableIdsArePresent())
            daggerDRSComponent.provideExperimentService()
                .refreshExperiment(experimentConfig.iExperimentCallback)
    }

    private fun checkIfUserOrStableIdsArePresent() =
        (AscendUser.stableId.isNotEmpty() || AscendUser.userId.isNotEmpty())

    private fun injectExperimentDependencies(context: Context) {
        daggerCoreComponent = CoreCapabilities.init(context, experimentConfig)

        daggerCoreComponent.inject(daggerCoreComponent.provideCoreClient())
        daggerDRSComponent = DaggerDRSComponent.builder()
            .drsModule(DRSModule(daggerCoreComponent.provideCoreClient(), experimentConfig)).build()
        baseExperimentService = daggerDRSComponent.provideExperimentService()
    }


    private fun initExperimentConfig(config: IConfigProvider) {
        if (config is ExperimentConfig) {
            experimentConfig = config
        }
    }


    fun getExperimentService(): BaseExperimentService {
        return baseExperimentService
    }


    override fun init(context: Context, config: IConfigProvider) {
        initExperimentConfig(config)
        injectExperimentDependencies(context)
        callExperimentIfNeeded(context)
    }

    override fun onNotify(pluggerEvents: PluggerEvents, data: Any?) {
        when (pluggerEvents) {
            PluggerEvents.USER_LOGGED_IN -> {
            }

            PluggerEvents.USER_LOGGED_OUT -> {
                getExperimentService().clearAllExperimentsData()
                getExperimentService().clearUserSessionData()
            }

        }
    }


}

interface IExperimentCallback {
    fun onSuccess()
    fun onFailure(throwable: Throwable)

}






