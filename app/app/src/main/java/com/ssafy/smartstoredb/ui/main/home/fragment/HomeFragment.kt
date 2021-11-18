package com.ssafy.smartstoredb.ui.main.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.main.home.adapter.LatestOrderAdapter
import com.ssafy.smartstoredb.ui.main.home.adapter.NoticeAdapter
import com.ssafy.smartstoredb.databinding.FragmentHomeBinding
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.main.SP_NAME

// Home 탭
class HomeFragment : Fragment(){
    private var latestOrderAdapter : LatestOrderAdapter = LatestOrderAdapter()
    private var noticeAdapter: NoticeAdapter = NoticeAdapter()
    private lateinit var mainActivity: MainActivity

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

        initUserName()
        initAdapter()
    }

    fun initAdapter() {
        noticeAdapter = NoticeAdapter()
        binding.recyclerViewNoticeOrder.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = noticeAdapter
            noticeAdapter.noticeData = readSharedPreference("fcm")
            noticeAdapter.notifyDataSetChanged()
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }


        latestOrderAdapter = LatestOrderAdapter()
        //메인화면에서 최근 목록 클릭시 장바구니로 이동
        latestOrderAdapter.setItemClickListener(object : LatestOrderAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                mainActivity!!.openFragment(1)
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

    private fun initUserName(){
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = user.name
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