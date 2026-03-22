package io.noties.adapt.ui.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

// TODO: validate new activity, that it would use created instead of previous started
class TopMostActivityListener : Application.ActivityLifecycleCallbacks {

    val topMostActivity: Activity? get() = startedActivity ?: createdActivity

    private var createdActivity: Activity? = null
    private var startedActivity: Activity? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        createdActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (createdActivity == activity) {
            createdActivity = null
        }
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivity = activity
    }

    override fun onActivityStopped(activity: Activity) {
        if (startedActivity == activity) {
            startedActivity = null
        }
    }

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}