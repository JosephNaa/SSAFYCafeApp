package com.ssafy.smartstoredb.data.remote.api

import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstoredb.model.dto.Order
import retrofit2.Call
import retrofit2.http.*

interface OrderApi {
    // order 객체를 저장하고 추가된 Order의 id를 반환한다.
    @POST("rest/order")
    fun makeOrder(@Body body: Order): Call<Int>

    // {orderId}에 해당하는 주문의 상세 내역을 목록 형태로 반환한다.
    // 사용자 정보 화면의 주문 내역 조회에서 사용된다.
    @GET("rest/order/{orderId}")
    fun getOrderDetail(@Path("orderId") orderId: Int): Call<List<OrderDetailResponse>>

    // {id}에 해당하는 사용자의 최근 1개월간 주문 내역을 반환한다.
    // 반환 정보는 1차 주문번호 내림차순, 2차 주문 상세 내림차순으로 정렬된다.
    @GET("rest/order/byUser")
    fun getLastMonthOrder(@Query("id") id: String): Call<List<LatestOrderResponse>>
}