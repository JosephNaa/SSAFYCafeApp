package com.ssafy.smartstoredb.util

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.ssafy.smartstoredb.R

class MyRemoteViewsService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return MyRemoteViewsFactory(this.applicationContext)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val updateViews = RemoteViews(this.packageName, R.layout.my_app_widget)
//        updateViews.setOnClickPendingIntent(R.id.btnRefresh, buildStartIntent(this))

        val componentName = ComponentName(this, MyAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.updateAppWidget(componentName, updateViews)

        return super.onStartCommand(intent, flags, startId)
    }

}