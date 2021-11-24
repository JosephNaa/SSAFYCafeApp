package com.ssafy.smartstoredb.data.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.main.SP_NAME

class MyWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): MyWidgetItemFactory? {
        return intent?.let { MyWidgetItemFactory(applicationContext, it) }
    }

    inner class MyWidgetItemFactory : RemoteViewsFactory {
        var context : Context?=null
        var appWidgetId : Int?=null
        var list: ArrayList<String>?=null

        constructor(context: Context, intent: Intent) {
            this.context = context
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
            this.list = readSharedPreference("fcm")
        }

        override fun onCreate() {
            SystemClock.sleep(3000)
        }

        override fun onDataSetChanged() {
            TODO("Not yet implemented")
        }

        override fun onDestroy() {
            TODO("Not yet implemented")
        }

        override fun getCount(): Int {
            return list!!.size
        }

        override fun getViewAt(position: Int): RemoteViews {
            var views = RemoteViews(context?.packageName, R.layout.list_item_notice)
            views.setTextViewText(R.id.textNoticeContent, list!![position])

            val fillIntent = Intent()
            fillIntent.putExtra("extraItemPosition", position)
            views.setOnClickFillInIntent(R.id.textNoticeContent, fillIntent)

            SystemClock.sleep(500)
            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        private fun readSharedPreference(key:String): ArrayList<String>{
            val sp = context?.getSharedPreferences(SP_NAME, FirebaseMessagingService.MODE_PRIVATE)
            val gson = Gson()
            val json = sp?.getString(key, "") ?: ""
            val type = object : TypeToken<ArrayList<String>>() {}.type
            val obj: ArrayList<String> = gson.fromJson(json, type) ?: ArrayList()
            return obj
        }
    }


}