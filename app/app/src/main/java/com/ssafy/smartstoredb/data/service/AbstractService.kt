package com.ssafy.smartstoredb.data.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ssafy.smartstoredb.data.db.DBHelper

abstract class AbstractService(val context:Context) {

    fun getWritableDatabase(): SQLiteDatabase {
        return DBHelper(context).writableDatabase
    }

    fun getReadableDatabase(): SQLiteDatabase {
        return DBHelper(context).readableDatabase
    }

}