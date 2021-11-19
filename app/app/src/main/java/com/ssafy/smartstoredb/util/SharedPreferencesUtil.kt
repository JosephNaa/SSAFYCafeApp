package com.ssafy.smartstoredb.util

import android.content.Context
import android.content.SharedPreferences
import com.ssafy.smartstoredb.model.dto.User

class SharedPreferencesUtil (context: Context) {
    val SHARED_PREFERENCES_NAME = "smartstore_preference"
    val COOKIES_KEY_NAME = "cookies"

    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    //사용자 정보 저장
    fun addUser(user: User){
        val editor = preferences.edit()
        editor.putString("id", user.id)
        editor.putString("name", user.name)
        editor.putInt("stamps", user.stamps)
        editor.apply()
    }

    fun getUser(): User {
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

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    fun addTable(id: String) {
        val editor = preferences.edit()
        editor.putString("tableId", id)
        editor.apply()
    }

    fun getTable(): String? {
        return preferences.getString("tableId", "")
    }

    fun deleteTable() {
        preferences.edit().remove("tableId").apply()
    }
}