package com.ssafy.smartstoredb.util

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.model.dto.WidgetItem
import com.ssafy.smartstoredb.ui.main.SP_NAME
import me.ibrahimsn.library.LiveSharedPreferences
import kotlin.math.acos

class MyRemoteViewsFactory(context: Context) : RemoteViewsService.RemoteViewsFactory {

    var context: Context?=context
    var list: ArrayList<WidgetItem> = arrayListOf()
    lateinit var fcmList: ArrayList<String>

    fun setData() {
        fcmList = readSharedPreference("fcm")

        list.clear()
        fcmList.forEachIndexed { index, fcm ->
            list.add(WidgetItem(index, fcm))
        }

    }

    override fun onCreate() {
        setData()
    }

    override fun onDataSetChanged() {
        setData()
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        var listViewWidget = RemoteViews(context?.packageName, R.layout.list_item_widget)
        listViewWidget.setTextViewText(R.id.text1, list.get(position).content)

        var dataIntent = Intent()
        dataIntent.putExtra("item_id", list.get(position)._id)
        dataIntent.putExtra("item_data", list.get(position).content)
        listViewWidget.setOnClickFillInIntent(R.id.text1, dataIntent)

        return listViewWidget
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
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