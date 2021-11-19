package com.ssafy.smartstore.util

import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.data.remote.api.CommentApi
import com.ssafy.smartstoredb.data.remote.api.OrderApi
import com.ssafy.smartstoredb.data.remote.api.ProductApi
import com.ssafy.smartstoredb.data.remote.api.UserApi


class RetrofitUtil {
    companion object{
        val commentService = ApplicationClass.retrofit.create(CommentApi::class.java)
        val orderService = ApplicationClass.retrofit.create(OrderApi::class.java)
        val productService = ApplicationClass.retrofit.create(ProductApi::class.java)
        val userService = ApplicationClass.retrofit.create(UserApi::class.java)
    }
}