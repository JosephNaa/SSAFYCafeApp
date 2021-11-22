package com.ssafy.smartstoredb.ui.main.mypage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.data.service.OrderService
import com.ssafy.smartstoredb.databinding.FragmentMypageBinding
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.model.dto.User
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.main.order.adapter.OrderAdapter
import java.util.*
import kotlin.collections.HashMap

// MyPage 탭
private const val TAG = "MypageFragment_싸피"
class MypageFragment : Fragment() {
    private lateinit var orderAdapter : OrderAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list : List<LatestOrderResponse>

    private lateinit var binding:FragmentMypageBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        val loginType = readSharedPreference()
        Toast.makeText(context, "" + loginType, Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var user = getUserData()
        initData(user.id)
    }

    private fun initData(id:String){
        val userLastOrderLiveData = OrderService().getLastMonthOrder(id)
        Log.d(TAG, "onViewCreated: ${userLastOrderLiveData.value}")
        userLastOrderLiveData.observe(viewLifecycleOwner, {
            list = it

            orderAdapter = OrderAdapter(mainActivity, list)
            orderAdapter.setItemClickListener(object : OrderAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int, orderid:Int) {
                    mainActivity.openFragment(2, "orderId", orderid)
                }
            })

            binding.recyclerViewOrder.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = orderAdapter
                //원래의 목록위치로 돌아오게함
                adapter!!.stateRestorationPolicy =
                    RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            binding.logout.setOnClickListener {
                mainActivity.openFragment(5)
            }

            Log.d(TAG, "onViewCreated: $it")
        }
        )

        UserService().getInfo(getUserData().id, getInfoCallback())
    }

    private fun getUserData(): User {
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        binding.textUserName.text = user.name
        return user
    }

    // 유저 정보 콜백
    inner class getInfoCallback: RetrofitCallback<HashMap<String, Any>> {
        override fun onSuccess(
            code: Int,
            responseData: HashMap<String, Any>
        ) {
            Toast.makeText(context, "유저 정보 불러오기 완료", Toast.LENGTH_SHORT).show()
            //Log.d(TAG, "onSuccess: ${responseData}")
            setMembershipLevel(responseData["grade"].toString())
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "코멘트 추가 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    private fun setMembershipLevel(str: String) {
        var grade = mutableMapOf<String, String>()

        var st = StringTokenizer(str, "=|,| |{|}")
        while(st.hasMoreTokens()) {
            grade[st.nextToken()] = st.nextToken()
        }

        var img = grade["img"].toString()
        var step = grade["step"]!!.toFloat().toInt()
        var to = grade["to"]!!.toFloat().toInt()
        var title = grade["title"].toString()

        // 등급 이미지 설정
        Glide.with(requireContext())
            .load("${ApplicationClass.IMGS_URL}${img}")
            .override(20, 20)
            .into(binding.imageLevel)

        // 단계 설정
        binding.textUserLevel.text = "$title ${step}단계"

        // 다음 레벨까지 n잔 남았습니다.
        binding.textLevelRest.text = "다음 레벨까지 ${to}잔 남았습니다."

        // 프로그레스바
        var max = 0
        when(title) {
            "씨앗" -> max = 10
            "꽃" -> max = 15
            "씨앗" -> max = 20
            "커피콩" -> max = 25
            else -> max = 1
        }
        binding.proBarUserLevel.max = max
        binding.proBarUserLevel.progress = max - to
        binding.textUserNextLevel.text = "${max - to}/$max"
    }

    // SP 읽기
    private fun readSharedPreference(): String{
        val sp = context?.getSharedPreferences("login_type", Context.MODE_PRIVATE)
        return sp?.getString("type", "") ?: ""
    }
}