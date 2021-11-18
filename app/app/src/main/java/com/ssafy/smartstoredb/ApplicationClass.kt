package com.ssafy.smartstoredb

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.ssafy.smartstoredb.db.DBHelper
import com.ssafy.smartstoredb.util.SharedPreferencesUtil
import java.text.DecimalFormat

private const val TAG = "ApplicationClass_싸피"
class ApplicationClass : Application() {
    companion object{
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
    }

    private lateinit var db:SQLiteDatabase

    override fun onCreate() {
        super.onCreate()
        db = DBHelper(this).readableDatabase

        Log.d(TAG, "onCreate: DB Initialization : ${db.isOpen}")

        //shared preference 초기화
        sharedPreferencesUtil = SharedPreferencesUtil(applicationContext)

    }

}