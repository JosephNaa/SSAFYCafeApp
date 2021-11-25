package com.ssafy.smartstoredb.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.data.service.MyWidgetService
import com.ssafy.smartstoredb.ui.main.MainActivity

/**
 * Implementation of App Widget functionality.
 */
private const val TAG = "MyAppWidgetProvider_싸피"
class MyAppWidgetProvider : AppWidgetProvider() {

    val ACTION_TOAST = "actionToast"
    val EXTRA_ITEM_POSITION = "extraItemPosition"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        val serviceIntent = Intent(context, MyRemoteViewsService::class.java)
        val widget = RemoteViews(context.packageName, R.layout.my_app_widget)
        widget.setRemoteAdapter(R.id.listView_notice, serviceIntent)
        appWidgetManager.updateAppWidget(appWidgetIds, widget)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        Log.d(TAG, "onReceive: ")
        if (ACTION_TOAST == intent?.action) {
            val mgr = AppWidgetManager.getInstance(context)
            val remoteViews = RemoteViews(context?.packageName, R.layout.my_app_widget)
            val componentName = ComponentName(context!!, MyAppWidgetProvider::class.java)
            Log.d(TAG, "onReceive: click")
            mgr.updateAppWidget(componentName, remoteViews)
        }
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.my_app_widget)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

