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
//            val intent = Intent(context, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            context?.startActivity(intent)
//            var clickedPosition = intent?.getIntExtra(EXTRA_ITEM_POSITION, 0)
//            Toast.makeText(context, "clicked $clickedPosition", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
//        val serviceIntent = Intent(context, MyRemoteViewsService::class.java)
//        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))
//        val widget = RemoteViews(context.packageName, R.layout.my_app_widget)
//        widget.setRemoteAdapter(R.id.listView_notice, serviceIntent)
////        appWidgetManager.updateAppWidget(appWidgetIds, widget)
//
//        val toastIntent = Intent(context, MyAppWidgetProvider::class.java)
//        toastIntent.setAction(ACTION_TOAST)
//        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
//        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)))
//        val toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//        widget.setPendingIntentTemplate(R.id.listView_notice, toastPendingIntent)
//        appWidgetManager.updateAppWidget(appWidgetId, widget)

//        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
//            .let { intent ->
//                PendingIntent.getActivity(context, 0, intent, 0)
//            }
//
//        val serviceIntent = Intent(context, MyWidgetService::class.java)
//        val clickIntent = Intent(context, MyAppWidgetProvider::class.java)
//        clickIntent.setAction(ACTION_TOAST)
//        val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.my_app_widget)
//        views.setRemoteAdapter(R.id.listView_notice, serviceIntent)
//        views.setEmptyView(R.id.listView_notice, R.id.example_widget_empty_view)
//        views.setPendingIntentTemplate(R.id.btnRefresh, clickPendingIntent)


//        val intent = Intent(context, MyAppWidgetProvider::class.java).setAction(ACTION_TOAST)
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
//        views.setOnClickPendingIntent(R.id.btnRefresh, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

