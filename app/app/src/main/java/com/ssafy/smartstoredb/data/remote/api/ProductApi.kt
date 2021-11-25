package com.ssafy.smartstoredb.data.remote.api

import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstoredb.model.dto.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {
    // 전체 상품의 목록을 반환한다
    @GET("rest/product")
    // fun getProductList(): Call<List<Product>>
    suspend fun getProductList(): Response<List<Product>>

    // {productId}에 해당하는 상품의 정보를 comment와 함께 반환한다.
    // comment 조회시 사용
    @GET("rest/product/{productId}")
    fun getProductWithComments(@Path("productId") productId: Int): Call<List<MenuDetailWithCommentResponse>>
}