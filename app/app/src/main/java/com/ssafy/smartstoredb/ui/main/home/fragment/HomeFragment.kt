package com.ssafy.smartstoredb.ui.main.home.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.data.service.OrderService
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.ui.main.home.adapter.LatestOrderAdapter
import com.ssafy.smartstoredb.ui.main.home.adapter.NoticeAdapter
import com.ssafy.smartstoredb.databinding.FragmentHomeBinding
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.main.SP_NAME
import me.ibrahimsn.library.LiveSharedPreferences

// Home 탭
private const val TAG = "HomeFragment_싸피"
class HomeFragment : Fragment(){
    lateinit var latestOrderAdapter : LatestOrderAdapter

    private var noticeAdapter: NoticeAdapter = NoticeAdapter()
    private lateinit var mainActivity: MainActivity

    private lateinit var list: List<LatestOrderResponse>

    private lateinit var binding:FragmentHomeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = initUserName()
//        initData(id)
        initAdapter()

        OrderService().getLastMonthOrder(id).observe(viewLifecycleOwner) {
            latestOrderAdapter.updateAdapter(it)
        }
    }

    fun initData(id: String) {
        UserService().getOrderList(id, LatestOrderListCallback())

    }

    inner class LatestOrderListCallback: RetrofitCallback<List<LatestOrderResponse>> {


        override fun onSuccess(code: Int, responseData: List<LatestOrderResponse>) {
            if(responseData.isNotEmpty()) {

                Log.d(TAG, "onSuccess: ${responseData}")

                list = responseData
                initAdapter()
            }
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "통신오류")
        }

    }

    fun initAdapter() {

        val sharedPreferences = binding.root.context.getSharedPreferences(SP_NAME, FirebaseMessagingService.MODE_PRIVATE)
        val liveSharedPreferences = LiveSharedPreferences(sharedPreferences)
        

        
        noticeAdapter = NoticeAdapter()
        binding.recyclerViewNoticeOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
            noticeAdapter.noticeData = readSharedPreference("fcm")
//            noticeAdapter.notifyDataSetChanged()
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        liveSharedPreferences.getString("fcm", "default").observe(requireActivity(), Observer { value ->
            Log.d(TAG, "liveSP: $value")
            noticeAdapter.update(readSharedPreference("fcm"))
        })

        latestOrderAdapter = LatestOrderAdapter(mainActivity, mutableListOf())
        //메인화면에서 최근 목록 클릭시 장바구니로 이동
        latestOrderAdapter.setItemClickListener(object : LatestOrderAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int, orderId: Int) {
                Log.d(TAG, "onClick: $orderId")
                mainActivity!!.openFragment(1, "orderId", orderId)
            }
        })
        binding.recyclerViewLatestOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = latestOrderAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initUserName(): String {
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = "${user.name} 님"
        return user.id
    }

    private fun readSharedPreference(key:String): ArrayList<String>{
        val sp = binding.root.context.getSharedPreferences(SP_NAME, FirebaseMessagingService.MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(key, "") ?: ""
        val type = object :TypeToken<ArrayList<String>>() {}.type
        val obj: ArrayList<String> = gson.fromJson(json, type) ?: ArrayList()
        return obj
    }
}