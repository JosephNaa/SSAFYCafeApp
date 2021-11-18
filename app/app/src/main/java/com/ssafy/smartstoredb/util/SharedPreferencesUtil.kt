package com.ssafy.smartstoredb.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ssafy.smartstoredb.ApplicationClass
import com.ssafy.smartstoredb.dto.User

class SharedPreferencesUtil (context: Context) {
    val SHARED_PREFERENCES_NAME = "smartstore_preference"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun putUser(user:User){
        val editor = preferences.edit()
        editor.putString("id", user.id)
        editor.putString("name", user.name)
        editor.putInt("stamps", user.stamps)
        editor.apply()
    }

    fun getUser(): User{
        val id = preferences.getString("id", "")
        if (id != ""){
            val name = preferences.getString("name", "")
            val stamps = preferences.getInt("stamps", 0)
            return User(id!!, name!!, "", stamps)
        }else{
            return User()
        }
    }

    fun deleteUser(){
        //preference 지우기
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}