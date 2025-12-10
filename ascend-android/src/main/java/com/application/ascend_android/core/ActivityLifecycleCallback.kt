package com.application.ascend_android

import android.app.Activity
import android.app.Application
import android.os.Bundle

abstract class ApplicationCallback : Application.ActivityLifecycleCallbacks {

    abstract var activeActivities: Int
    private var isActivityChangingConfigurations = false
    private var firstLaunch = true


    override fun onActivityStarted(activity: Activity) {
        if (firstLaunch) {
            ++activeActivities
            firstLaunch = false
        }
        else {
            if (++activeActivities == 1 && !isActivityChangingConfigurations) {
                onApplicationInForeground()
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }


    override fun onActivityStopped(activity: Activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations
        if (--activeActivities == 0 && !isActivityChangingConfigurations) {
            onApplicationInBackground()
        }
    }

    abstract fun onApplicationInForeground()

    abstract fun onApplicationInBackground()
}

