package com.ssafy.smartstoredb.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.ApplicationClass
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.activity.MainActivity
import com.ssafy.smartstoredb.adapter.ShoppingListAdapter
import com.ssafy.smartstoredb.dto.OrderDetail
import com.ssafy.smartstoredb.dto.UserOrderDetail
import com.ssafy.smartstoredb.service.OrderService
import com.ssafy.smartstoredb.service.UserService

//장바구니 Fragment
class ShoppingListFragment : Fragment(){
    private lateinit var shoppingListRecyclerView: RecyclerView
    private lateinit var shoppingListAdapter : ShoppingListAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var btnShop : Button
    private lateinit var btnTakeout : Button
    private lateinit var btnOrder : Button
    private var isShop : Boolean = true
    private lateinit var list : ArrayList<OrderDetail>
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = OrderService(mainActivity).findShoppingList() as ArrayList<OrderDetail>
        shoppingListAdapter = ShoppingListAdapter(mainActivity,list)
        var cnt = 0;
        var totalprice = 0;
        for(item in list){
            cnt+=item.quantity
            totalprice += item.quantity*item.unitPrice
        }
        view.findViewById<TextView>(R.id.textShoppingCount).text = "총 ${cnt.toString()} 개"
        view.findViewById<TextView>(R.id.textShoppingMoney).text = "총 ${totalprice.toString()} 원"
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
        btnOrder.setOnClickListener {
            var id = getUserData()
            OrderService(mainActivity).makeorder(id,list,"table","N")
            completedOrder()
            mainActivity.openFragment(6)
//            if(isShop) showDialogForOrderInShop()
//            else {
//                //거리가 200이상이라면
//                if(true) showDialogForOrderTakeoutOver200m()
//            }
        }
    }
    private fun getUserData():String{
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        return user.id
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogForOrderInShop() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage(
            "Table NFC를 찍어주세요.\n"
        )
        builder.setCancelable(true)
        builder.setNegativeButton("취소"
        ) { dialog, _ -> dialog.cancel()
            completedOrder()}
        builder.create().show()
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
        Toast.makeText(context,"주문이 완료되었습니다.",Toast.LENGTH_SHORT).show()
    }

}