package com.ssafy.smartstoredb.data.service

import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstore.util.RetrofitUtil
import com.ssafy.smartstoredb.model.dto.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentService {
    // 코멘트 추가. response 받는 건 딱히없지 않나? 쓸모도 없을 것 같은데...
    fun addComment(comment: Comment, callback: RetrofitCallback<Boolean>)  {
        val menuInfoRequest: Call<Boolean> = RetrofitUtil.commentService.insert(comment)
        menuInfoRequest.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
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

    // 코멘트 업데이트.
    fun updateComment(comment: Comment, callback: RetrofitCallback<Boolean>)  {
        val menuInfoRequest: Call<Boolean> = RetrofitUtil.commentService.update(comment)
        menuInfoRequest.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
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

    // 코멘트 삭제
    fun removeComment(commentId: Int, callback: RetrofitCallback<Boolean>)  {
        val menuInfoRequest: Call<Boolean> = RetrofitUtil.commentService.delete(commentId)
        menuInfoRequest.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val res = response.body()
                if(response.code() == 200){
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
}