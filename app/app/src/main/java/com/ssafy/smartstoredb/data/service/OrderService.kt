package com.ssafy.smartstoredb.data.service

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstore.util.RetrofitUtil
import com.ssafy.smartstoredb.model.dto.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "OrderService_싸피"

class OrderService {
    // 주문 상세 내역 가져오는 API
    fun getOrderDetails(orderId: Int): LiveData<List<OrderDetailResponse>> {
        val responseLiveData: MutableLiveData<List<OrderDetailResponse>> = MutableLiveData()
        val orderDetailRequest: Call<List<OrderDetailResponse>> = RetrofitUtil.orderService.getOrderDetail(orderId)

        orderDetailRequest.enqueue(object : Callback<List<OrderDetailResponse>> {
            override fun onResponse(call: Call<List<OrderDetailResponse>>, response: Response<List<OrderDetailResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        responseLiveData.value = res
                    }
//                    Log.d(TAG, "onResponse: $res")
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<OrderDetailResponse>>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문 상세 내역 받아오는 중 통신오류")
            }
        })

        return responseLiveData
    }

    // 최근 한달간 주문내역 가져오는 API
    fun getLastMonthOrder(userId: String): LiveData<List<LatestOrderResponse>> {
        val responseLiveData: MutableLiveData<List<LatestOrderResponse>> = MutableLiveData()
        val latestOrderRequest: Call<List<LatestOrderResponse>> = RetrofitUtil.orderService.getLastMonthOrder(userId)

        latestOrderRequest.enqueue(object : Callback<List<LatestOrderResponse>> {
            override fun onResponse(call: Call<List<LatestOrderResponse>>, response: Response<List<LatestOrderResponse>>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        // 가공 필요 orderDate 를 기준으로 정렬, o_img 하나로 축약 필요
                        //orderId를 기준으로 새로운 리스트 만들어서 넘기기
                        responseLiveData.value = makeLatestOrderList(res)
                    }
//                    Log.d(TAG, "onResponse: $res")
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LatestOrderResponse>>, t: Throwable) {
                Log.d(TAG, t.message ?: "최근 주문 내역 받아오는 중 통신오류")
            }
        })

        return responseLiveData
    }

    // 최근 주문 목록에서 총가격, 주문 개수 구하여 List로 반환한다.
    // 반환되는 List의 경우 화면에서 보여주는 최근 주문 목록 List이다.
    private fun makeLatestOrderList(latestOrderList: List<LatestOrderResponse>): List<LatestOrderResponse>{
        val hm = HashMap<Int, LatestOrderResponse>()
        latestOrderList.forEach { order ->
            if(hm.containsKey(order.orderId)){
                val tmp = hm[order.orderId]!!
                tmp.orderCnt += order.orderCnt
                tmp.totalPrice  += order.productPrice * order.orderCnt
                hm[order.orderId] = tmp
            } else {
                order.totalPrice = order.productPrice * order.orderCnt
                hm[order.orderId] = order
            }
        }
        val list = ArrayList<LatestOrderResponse>(hm.values)
        list.sortWith { o1, o2 -> o2.orderDate.compareTo(o1.orderDate) }
        return list
    }

    // 주문 API
    fun makeOrder(order: Order): Int{
        val makeOrderRequest: Call<Int> = RetrofitUtil.orderService.makeOrder(order)
        var result = 0
        makeOrderRequest.enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                val res = response.body()
                if(response.code() == 200){
                    if (res != null) {
                        result = res
                    }
//                    Log.d(TAG, "onResponse: $res")
                } else {
                    Log.d(TAG, "onResponse: Error Code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.d(TAG, t.message ?: "주문 중 통신오류")
            }
        })
        return result
    }

}