package com.ssafy.smartstoredb.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

private const val TAG = "DBHelper_싸피"

class DBHelper : SQLiteOpenHelper {
    private lateinit var mContext: Context

    constructor(context:Context?) : this( context, "smartstore.db", null, 1) {
        this.mContext = context!!
    }

    private constructor(context: Context?,
                name: String?,
                factory: SQLiteDatabase.CursorFactory?,
                version: Int) : super(context, name, factory, version)

    private lateinit var db: SQLiteDatabase

    lateinit var query:String
    private fun loadAssets(){
        try{
            BufferedReader(InputStreamReader(mContext.assets.open("create_table.sql"))).useLines {
                query = it.fold(""){ acc, text ->
                    "$acc\n$text"
               }
            }
            query.trim()
        }catch(e: IOException){
            Log.e(TAG, "onCreate: assets 파일 로딩 실패", e)
        }
    }

    // 테이블 생성 쿼리
    override fun onCreate(db: SQLiteDatabase) {
        loadAssets()

        val splitArray =  query.split(";")

        db.beginTransaction()
        splitArray.forEach {
            if(it.length > 1){
                Log.d(TAG, "onCreate: ${it}")
                db.execSQL(it.trim())
            }
        }
        db.setTransactionSuccessful()
        db.endTransaction()
    }

    //  upgrade 가 필요한 경우 기존 테이블 drop 후 onCreate로 새롭게 생성
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val query = """
            drop table t_user;
            drop table t_product;
            drop table t_order;
            drop table t_order_detail;                                                 
            drop table t_stamp ;
            drop table t_comment;
        """.trimIndent()

        val splitArray =  query.split(";")

        splitArray.forEach {
            if(it.trim().length > 1){
                Log.d(TAG, "onCreate: ${it}")
                db.execSQL(it.trim())
            }
        }
        Log.d(TAG, "onOpen: database 삭제 완료")

        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        this.db = db!!
        Log.d(TAG, "onOpen: database 준비 완료")
    }

    override fun close() {
        super.close()
        Log.d(TAG, "onOpen: database close!!!!!")
    }
}
