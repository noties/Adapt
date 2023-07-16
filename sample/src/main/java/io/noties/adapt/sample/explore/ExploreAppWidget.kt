package io.noties.adapt.sample.explore

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle

// TODO: enable only for android 31?
class ExploreAppWidgetBroadcastReceiver: AppWidgetProvider() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        TODO("Not yet implemented")
//    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds
            .map { appWidgetManager.getAppWidgetInfo(it) }
            .forEach {

            }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        // TODO: app widget sizes is Android 31...
//        val sizes = newOptions.getParcelableArrayList(AppWidgetManager.APP)
    }
}