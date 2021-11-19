package com.ssafy.smartstoredb.ui.main.order.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.response.OrderDetailResponse
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.main.order.adapter.ShoppingListAdapter
import com.ssafy.smartstoredb.model.dto.OrderDetail
import com.ssafy.smartstoredb.data.service.OrderService
import com.ssafy.smartstoredb.model.dto.Order
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.viewmodel.MainViewModel
import com.ssafy.smartstoredb.util.CommonUtils

//장바구니 Fragment
private const val TAG = "ShoppingListFragment_싸피"
class ShoppingListFragment : Fragment(){
    private lateinit var shoppingListRecyclerView: RecyclerView
    private var shoppingListAdapter : ShoppingListAdapter? = null
    private lateinit var mainActivity: MainActivity
    private lateinit var btnShop : Button
    private lateinit var btnTakeout : Button
    private lateinit var btnOrder : Button
    private lateinit var shoppingCount: TextView
    private lateinit var shoppingMoney: TextView
    private var isShop : Boolean = true
    private lateinit var viewModel : MainViewModel
    var distanceFromStore = 10.0

    var list = mutableListOf<ShoppingCart>()
    var orderId: Int = 0
    var liveList: LiveData<List<OrderDetailResponse>>?= null

    lateinit var builder: AlertDialog.Builder
    var ad: AlertDialog?=null

    var cart = mutableListOf<ShoppingCart>()
    var totalCnt = 0
    var totalPrice = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
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
        val view = inflater.inflate(R.layout.fragment_shopping_list,null)
        shoppingListRecyclerView =view.findViewById(R.id.recyclerViewShoppingList)
        btnShop = view.findViewById(R.id.btnShop)
        btnTakeout = view.findViewById(R.id.btnTakeout)
        btnOrder = view.findViewById(R.id.btnOrder)
        shoppingCount = view.findViewById(R.id.textShoppingCount)
        shoppingMoney = view.findViewById(R.id.textShoppingMoney)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)
        shoppingListAdapter = ShoppingListAdapter(viewModel.getShoppingCart())
        shoppingListRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
            adapter = shoppingListAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        btnShop.setOnClickListener {
            btnShop.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnTakeout.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = true
        }
        btnTakeout.setOnClickListener {
            btnTakeout.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_color)
            btnShop.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_non_color)
            isShop = false
        }

        cart = viewModel.getShoppingCart()
        totalCnt = 0
        totalPrice = 0
        for(i in 0 until cart.size) {
            totalPrice += cart[i].totalPrice
            totalCnt += cart[i].menuCnt
        }
        shoppingCount.text = "총 ${totalCnt}개"
        shoppingMoney.text = "${CommonUtils.makeComma(totalPrice)}"

        viewModel.distance.observe(viewLifecycleOwner) {
            distanceFromStore = it
        }

        btnOrder.setOnClickListener {
            if(isShop) {
//                if (ApplicationClass.sharedPreferencesUtil.getTable() == "")
//                    showDialogForOrderInShop()
//                else
                    completedOrder()
            }
            else {
                //거리가 200이상이라면
                if(true) showDialogForOrderTakeoutOver200m()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        ad?.dismiss()
    }

    private fun showDialogForOrderInShop() {
        builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )
        builder.setCancelable(true)
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }
//        builder.create().show()

        ad = builder.create()
        ad!!.show()

    }
    private fun showDialogForOrderTakeoutOver200m() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "현재 고객님의 위치가 매장과 200m 이상 떨어져 있습니다.\n정말 주문하시겠습니까?"
        )
        builder.setCancelable(true)
        builder.setPositiveButton("확인") { _, _ ->
            completedOrder()
        }
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel() }
        builder.create().show()
    }

    private fun completedOrder(){

        var order = Order()
        var detail = arrayListOf<OrderDetail>()
        var user = ApplicationClass.sharedPreferencesUtil.getUser()

        for (shoppingCart in list) {
            detail.add(OrderDetail(shoppingCart.menuId, shoppingCart.menuCnt))
        }

        order.userId = user.id
//        order.orderTable = ApplicationClass.sharedPreferencesUtil.getTable().toString()
        order.orderTable = "order_table 1"
        order.orderTime = System.currentTimeMillis().toString()
        order.complited = "N"
        order.details = detail

        Log.d(TAG, "completedOrder: $order")

        OrderService().makeOrder(order)

        viewModel.clearShoppingCart()
        list.clear()
        ApplicationClass.sharedPreferencesUtil.deleteTable()

        Toast.makeText(context,"주문이 완료되었습니다.",Toast.LENGTH_SHORT).show()

        mainActivity.hideBottomNav(false)
        mainActivity.openFragment(6)
    }

    private fun getUserData():String{
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        return user.id
    }


    private fun getNfcData(intent: Intent) {
        Log.d(TAG, "getNfcData: ${intent.action}")
        if (intent.action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d(TAG, "getNfcData: action_ndef_Discoveered")
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                Log.d(TAG, "getNfcData: rawmsg!=null")
                val message = arrayOfNulls<NdefMessage>(rawMsgs.size)

                for (i in rawMsgs.indices) {
                    Log.d(TAG, "getNfcData: for i")
                    message[i] = rawMsgs[i] as NdefMessage
                }

                //실제 저장되어 있는 데이터 추출
                var record_data = message[0]!!.records[0]
                val record_type = record_data.type
                val type = String(record_type)
                if (type.equals("T")) {
                    Log.d(TAG, "getNfcData: type t")
                    val data = message[0]!!.records[0].payload
                    //가져온 데이터를 TaxtView에 반영
                    Log.d(TAG, "getNfcData: $data")
                    Toast.makeText(mainActivity, "$data", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    companion
    object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            ShoppingListFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}