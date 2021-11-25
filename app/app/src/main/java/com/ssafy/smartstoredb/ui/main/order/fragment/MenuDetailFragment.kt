package com.ssafy.smartstoredb.ui.main.order.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.adapter.CommentListener
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstore.util.RetrofitCallback
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.data.service.CommentService
import com.ssafy.smartstoredb.ui.main.order.adapter.CommentAdapter
import com.ssafy.smartstoredb.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstoredb.model.dto.Product
import com.ssafy.smartstoredb.data.service.OrderService
import com.ssafy.smartstoredb.data.service.ProductService
import com.ssafy.smartstoredb.data.service.UserService
import com.ssafy.smartstoredb.model.dto.Comment
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.ui.viewmodel.MainViewModel
import com.ssafy.smartstoredb.util.CommonUtils
import kotlin.math.round

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"
class MenuDetailFragment : Fragment(){
    private lateinit var mainActivity: MainActivity
    private var commentAdapter = CommentAdapter(emptyList())
    private var productId = -1
    private lateinit var product: Product
    private lateinit var viewModel : MainViewModel

    private var menuId = 0
    private var menuImg = ""
    private var menuName = ""
    private var menuCnt = 1
    private var menuPrice = 0
    private var totalPrice = 0
    private var menuType = ""

    private lateinit var binding:FragmentMenuDetailBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)

        arguments?.let {
            productId = it.getInt("productId", -1)
            Log.d(TAG, "onCreate: $productId")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(mainActivity).get(MainViewModel::class.java)

        initData()

        initListener()
    }

    private fun initAdapter() {
        Log.d(TAG, "initAdapter: ")
        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함
            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }

        commentAdapter.setItemClickListener(object: CommentAdapter.ItemClickListener {
            override fun onClick(
                view: View,
                position: Int,
                comment: MenuDetailWithCommentResponse,
                flag: Int
            ) {
                when(flag) {
                    2 -> {
                        Log.d(TAG, "onClick: $comment")
                        var cmt = Comment(comment.commentId, getUserData(), productId, comment.productRating.toFloat(), comment.commentContent.toString())
                        UserService().updateComment(cmt, UpdateCallback())
                    }
                    3 -> {
                        var cmt = Comment(comment.commentId, getUserData(), productId, comment.productRating.toFloat(), comment.commentContent.toString())
                        UserService().deleteComment(cmt, DeleteCallback())
                    }
                }

            }

        })

        menuCnt = 1
    }

    //MutableLiveData<List<MenuDetailWithCommentResponse>>
    private fun initData(){
        ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
    }

    inner class UpdateCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, flag: Boolean) {

            if (flag) {
                Toast.makeText(context,"수정 완료", Toast.LENGTH_SHORT).show()
                initData()
            }else{
                Toast.makeText(context,"수정 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    inner class DeleteCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, flag: Boolean) {

            if (flag) {
                Toast.makeText(context,"삭제 완료", Toast.LENGTH_SHORT).show()
                initData()
            }else{
                Toast.makeText(context,"삭제 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    // 초기 화면 설정
    private fun setScreen(menu: MenuDetailWithCommentResponse){
        Glide.with(this)
            .load("${ApplicationClass.MENU_IMGS_URL}${menu.productImg}")
            .into(binding.menuImage)

        binding.txtMenuName.text = menu.productName
        binding.txtMenuPrice.text = "${CommonUtils.makeComma(menu.productPrice)}"
        binding.txtRating.text = "${(round(menu.productRatingAvg*10) /10)}점"
        binding.ratingBar.rating = menu.productRatingAvg.toFloat()/2
        binding.textMenuCount.text = menuCnt.toString()

        Log.d(TAG, "setScreen: $menu")
        menuPrice = menu.productPrice
        menuId = productId
        menuImg = menu.productImg
        menuName = menu.productName
        if(menuName.contains("coffee")) {
            menuType = "coffee"
        } else {
            menuType = "cookie"
        }

    }

    private fun initListener(){
        binding.btnAddList.setOnClickListener {
            // 장바구니 담기
            if(getUserData() != null) {

                totalPrice = menuCnt * menuPrice

                viewModel.addShoppingItem(ShoppingCart(menuId, menuImg, menuName, menuCnt, menuPrice, totalPrice, menuType))
//                Toast.makeText(context,"상품이 장바구니에 담겼습니다.",Toast.LENGTH_SHORT).show()
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_main, OrderFragment())
                    .addToBackStack(null)
                    .commit()
                mainActivity.hideBottomNav(false)

//                Toast.makeText(context, "상품이 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCreateComment.setOnClickListener {
            showDialogRatingStar()
        }
        binding.btnAddCount.setOnClickListener {
            menuCnt++
            binding.textMenuCount.text = menuCnt.toString()
        }

        binding.btnMinusCount.setOnClickListener {
            if(menuCnt > 1) {
                menuCnt--
                binding.textMenuCount.text = menuCnt.toString()
            } else {
                menuCnt = 1
                binding.textMenuCount.text = menuCnt.toString()
            }
        }

        binding.btnCreateComment.setOnClickListener {
            showDialogRatingStar()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNav(false)
    }

    private fun showDialogRatingStar() {
        val dlg = Dialog(requireContext())
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(R.layout.dialog_menu_comment)
        dlg.setCancelable(false)

        var btnCancel = dlg.findViewById<TextView>(R.id.rating_cancel)
        var btnOk = dlg.findViewById<TextView>(R.id.rating_ok)
        btnCancel.setOnClickListener { dlg.dismiss() }

        var rating = dlg.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment)

        btnOk.setOnClickListener {
            val user = ApplicationClass.sharedPreferencesUtil.getUser()
            val comment = Comment(-1, user.id, productId, rating.rating, binding.comment.text.toString())
            UserService().insertComment(comment, CommentCallback()).let {
                ProductService().getProductWithComments(productId, ProductWithCommentInsertCallback())
            }

            binding.comment.text = null

            dlg.dismiss()
        }

        dlg.show()
    }

    inner class CommentCallback: RetrofitCallback<Boolean> {
        override fun onSuccess( code: Int, flag: Boolean) {

            if (flag) {
                Toast.makeText(context,"등록 완료", Toast.LENGTH_SHORT).show()
                initData()
            }else{
                Toast.makeText(context,"등록 실패", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(key:String, value:Int) =
            MenuDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }


    inner class ProductWithCommentInsertCallback: RetrofitCallback<List<MenuDetailWithCommentResponse>> {
        override fun onSuccess(
            code: Int,
            responseData: List<MenuDetailWithCommentResponse>
        ) {
            if(responseData.isNotEmpty()) {

                Log.d(TAG, "onSuccess: ${responseData}")

                // comment 가 없을 경우 -> 들어온 response가 1개이고 해당 userId 가 null일 경우 빈 배열 Adapter 연결
                commentAdapter = if (responseData.size == 1 && responseData[0].userId == null) {
                    CommentAdapter(emptyList())
                } else {
                    Log.d(TAG, "onSuccess_responsedata: $responseData")
                    CommentAdapter(responseData)
                }
                initAdapter()

                commentAdapter.updateAdapter(responseData)

                // 화면 정보 갱신
                setScreen(responseData[0])
            }

        }

        override fun onError(t: Throwable) {
            Log.d(TAG, t.message ?: "물품 정보 받아오는 중 통신오류")
        }

        override fun onFailure(code: Int) {
            Log.d(TAG, "onResponse: Error Code $code")
        }
    }

    private fun getUserData():String{
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        return user.id
    }
}