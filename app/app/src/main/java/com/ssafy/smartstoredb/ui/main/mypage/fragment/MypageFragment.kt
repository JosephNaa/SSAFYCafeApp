package com.ssafy.smartstoredb.ui.main.mypage.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.databinding.FragmentMypageBinding
import com.ssafy.smartstoredb.model.UserOrderDetail
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.main.order.adapter.OrderAdapter

// MyPage 탭
class MypageFragment : Fragment() {
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var list: ArrayList<UserOrderDetail>
    private lateinit var userLevel: String
    private var levelNum = 0
    private var levelGap = 0

    private lateinit var binding: FragmentMypageBinding
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var id = getUserData()
        initData(id)
        initAdapter()
    }

    private fun initData(id: String) {
        list = UserService(mainActivity).getOrderList(id)
    }

    private fun initAdapter() {
        orderAdapter = OrderAdapter(mainActivity, list)
        orderAdapter.setItemClickListener(object : OrderAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, orderid: Int) {
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
    }

    private fun getUserData(): String {
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        setLevel(user.stamps)
        var value = 100.0
        val x = user.stamps.toDouble() / levelNum.toDouble()
        if (levelGap != 0) value = ((x / levelGap.toDouble()) * 100.0)

        binding.textUserName.text = user.name
        binding.textUserLevel.text = "$userLevel ${levelNum}단계"
        binding.textUserNextLevel.text = "${x.toInt()} / ${levelGap}"
        binding.textLevelRest.text = "다음 레벨까지 ${levelGap - x.toInt()}잔 남았습니다."
        binding.proBarUserLevel.setProgress(value.toInt())

        return user.id
    }

    private fun setLevel(stamp: Int) {
        when (stamp) {
            in 0..50 -> {
                userLevel = "씨앗"
                levelNum = seed(stamp)
                levelGap = 10
            }
            in 51..125 -> {
                userLevel = "꽃"
                levelNum = flower(stamp)
                levelGap = 15
            }
            in 126..225 -> {
                userLevel = "열매"
                levelNum = fruit(stamp)
                levelGap = 20
            }
            in 226..350 -> {
                userLevel = "커피"
                levelNum = coffee(stamp)
                levelGap = 25
            }
            else -> userLevel = "나무"
        }
    }

    private fun seed(stamps: Int): Int =
        when (stamps) {
            in 0..10 -> 1
            in 11..20 -> 2
            in 21..30 -> 3
            in 31..40 -> 4
            else -> 5
        }

    private fun flower(stamps: Int): Int =
        when (stamps) {
            in 51..65 -> 1
            in 66..80 -> 2
            in 81..95 -> 3
            in 96..110 -> 4
            else -> 5
        }

    private fun fruit(stamps: Int): Int =
        when (stamps) {
            in 126..145 -> 1
            in 146..165 -> 2
            in 166..185 -> 3
            in 186..205 -> 4
            else -> 5
        }

    private fun coffee(stamps: Int): Int =
        when (stamps) {
            in 226..250 -> 1
            in 251..275 -> 2
            in 276..300 -> 3
            in 301..325 -> 4
            else -> 5
        }

}