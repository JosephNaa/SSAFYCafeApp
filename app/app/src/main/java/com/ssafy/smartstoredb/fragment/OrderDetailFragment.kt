package com.ssafy.smartstoredb.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.activity.MainActivity
import com.ssafy.smartstoredb.adapter.OrderDetailListAdapter
import com.ssafy.smartstoredb.databinding.FragmentOrderDetailBinding
import com.ssafy.smartstoredb.dto.Order
import com.ssafy.smartstoredb.service.OrderService
import com.ssafy.smartstoredb.service.UserService
import com.ssafy.smartstoredb.util.CommonUtils

// 주문상세화면, My탭  - 주문내역 선택시 팝업
private const val TAG = "OrderDetailFragment_싸피"
class OrderDetailFragment : Fragment(){
    private lateinit var orderDetailListAdapter: OrderDetailListAdapter
    private lateinit var mainActivity: MainActivity

    private var orderId = -1
    private lateinit var orderWithDetails:Order

    private lateinit var binding:FragmentOrderDetailBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

        arguments?.let {
            orderId = it.getInt("orderId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initAdapter()

    }

    private fun initData(){
        orderWithDetails = OrderService(mainActivity).getOrderWithDetails(orderId)
    }

    private fun initView(){
        binding.tvOrderDate.text = orderWithDetails.orderTime
        binding.tvTotalPrice.text = CommonUtils.makeComma(orderWithDetails.totalPrice)
    }

    private fun initAdapter(){
        orderDetailListAdapter = OrderDetailListAdapter(mainActivity, orderWithDetails.details)
        binding.recyclerViewOrderDetailList.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = orderDetailListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    companion
    object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            OrderDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}