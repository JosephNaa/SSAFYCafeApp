package com.ssafy.smartstoredb.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.ssafy.smartstoredb.db.DBHelper

abstract class AbstractService(val context:Context) {

    fun getWritableDatabase(): SQLiteDatabase {
        return DBHelper(context).writableDatabase
    }

    fun getReadableDatabase(): SQLiteDatabase {
        return DBHelper(context).readableDatabase
    }

}