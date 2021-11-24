package com.ssafy.smartstoredb.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.data.service.MyWidgetService
import com.ssafy.smartstoredb.ui.main.MainActivity

/**
 * Implementation of App Widget functionality.
 */
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
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (ACTION_TOAST.equals(intent?.action)) {
            var clickedPosition = intent?.getIntExtra(EXTRA_ITEM_POSITION, 0)
            Toast.makeText(context, "clicked $clickedPosition", Toast.LENGTH_SHORT).show()
        }
        super.onReceive(context, intent)
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val widgetText = context.getString(R.string.appwidget_text)

        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
            .let { intent ->
                PendingIntent.getActivity(context, 0, intent, 0)
            }

    val serviceIntent = Intent(context, MyWidgetService::class.java)
        val clickIntent = Intent(context, MyAppWidgetProvider::class.java)
        clickIntent.setAction(ACTION_TOAST)
        val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.my_app_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
        views.setRemoteAdapter(R.id.listView_notice, serviceIntent)
        views.setEmptyView(R.id.listView_notice, R.id.example_widget_empty_view)
        views.setPendingIntentTemplate(R.id.listView_notice, clickPendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

