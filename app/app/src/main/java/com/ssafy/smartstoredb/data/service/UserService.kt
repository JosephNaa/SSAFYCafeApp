package com.ssafy.smartstoredb.data.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import com.ssafy.smartstoredb.model.dto.Comment
import com.ssafy.smartstoredb.model.dto.User
import com.ssafy.smartstoredb.model.dto.UserLevel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "UserService_싸피"
class UserService {
    fun login(user:User, callback: RetrofitCallback<User>)  {
        RetrofitUtil.userService.login(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun join(user: User, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.userService.insert(user).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun isAvailable(id: String, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.userService.isUsedId(id).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun insertComment(comment: Comment, callback: RetrofitCallback<Boolean>){
        RetrofitUtil.commentService.insert(comment).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun updateComment(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.update(comment).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun deleteComment(comment: Comment, callback: RetrofitCallback<Boolean>) {
        RetrofitUtil.commentService.delete(comment.id).enqueue(object: Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun getOrderList(id: String, callback: RetrofitCallback<List<LatestOrderResponse>>) {
        RetrofitUtil.orderService.getLastMonthOrder(id).enqueue(object:
            Callback<List<LatestOrderResponse>> {
            override fun onResponse(
                call: Call<List<LatestOrderResponse>>,
                response: Response<List<LatestOrderResponse>>
            ) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<List<LatestOrderResponse>>, t: Throwable) {
                callback.onError(t)
            }

        })
    }

    fun getInfo(id: String, callback: RetrofitCallback<HashMap<String, Any>>)  {
        RetrofitUtil.userService.getInfo(id).enqueue(object : Callback<HashMap<String, Any>> {
            override fun onResponse(call: Call<HashMap<String, Any>>, response: Response<HashMap<String, Any>>) {
                val res = response.body()

                if(response.code() == 200){
                    if (res != null) {
                        callback.onSuccess(response.code(), res)
                    }
                } else {
                    callback.onFailure(response.code())
                }
            }

            override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                callback.onError(t)
            }
        })
    }

    fun getLevelInfo(userId:String, callback: RetrofitCallback<UserLevel>) {
        var result = UserLevel("", 0, 0, "")
        RetrofitUtil.userService.getInfo(userId).enqueue(object : Callback<HashMap<String, Any>> {
            override fun onResponse(
                call: Call<HashMap<String, Any>>,
                response: Response<HashMap<String, Any>>
            ) {
                val res = response.body()
                if (response.code() == 200) {
                    if (res != null) {
                        val grade = res["grade"] as Map<String, Any>
                        val stamp = res["user"] as Map<String, Any>
                        val title = grade["title"].toString()
                        val unit = grade["step"].toString().toFloat().toInt()
                        val max = (grade["to"].toString().toFloat().toInt()) + (stamp["stamps"].toString().toFloat().toInt())
                        val img = grade["img"].toString()
                        //Log.d(TAG, "onResponse: ${grade["img"]} ${grade["step"]} ${grade["to"]} ${grade["title"]} ${stamp["stamps"]}")
                        result.title = title
                        result.unit = unit
                        result.max = max
                        result.img = img
                        Log.d(TAG, "onResponse: $title $unit $max $img")
                        callback.onSuccess(response.code(), UserLevel(title,unit,max,img))
                    }
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<HashMap<String, Any>>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문 중 통신오류")
            }

        })
    }
}