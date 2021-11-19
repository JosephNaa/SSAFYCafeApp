package com.ssafy.smartstoredb.ui.main.order.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.ui.main.order.adapter.CommentAdapter
import com.ssafy.smartstoredb.databinding.FragmentMenuDetailBinding
import com.ssafy.smartstoredb.model.dto.Product
import com.ssafy.smartstoredb.data.service.OrderService
import com.ssafy.smartstoredb.data.service.ProductService
import com.ssafy.smartstoredb.ui.main.MainActivity
import com.ssafy.smartstoredb.util.CommonUtils

//메뉴 상세 화면 . Order탭 - 특정 메뉴 선택시 열림
private const val TAG = "MenuDetailFragment_싸피"

class MenuDetailFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var product: Product
    private var productId = -1
    private var cnt = 1
    private var changerating = 0f
    private lateinit var binding: FragmentMenuDetailBinding

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
        binding = FragmentMenuDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initAdapter()
        initListener()



    }

    private fun getUserData(): String {
        var user = ApplicationClass.sharedPreferencesUtil.getUser()
        return user.id
    }

    private fun initData() {
        product = ProductService(requireContext()).getProductWithComments(productId)
//        Log.d(TAG, "initData: prodList: ${product}")
    }

    private fun initView() {
        val rating = ProductService(requireContext()).getrating(productId)
        binding.ratingtext.text = String.format("%.2f",rating)
        binding.ratingbar.rating = rating.toFloat() / 2
        binding.txtMenuName.text = product.name
        binding.txtMenuPrice.text = CommonUtils.makeComma(product.price)
        binding.textMenuCount.text = cnt.toString()

        var img = requireContext().resources.getIdentifier(
            product.img,
            "drawable",
            requireContext().packageName
        )
        binding.menuImage.setImageResource(img)
    }

    private fun initAdapter() {
        commentAdapter = CommentAdapter(mainActivity, productId, getUserData())
        binding.recyclerViewMenuDetail.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
            //원래의 목록위치로 돌아오게함

            adapter!!.stateRestorationPolicy =
                RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        }

    }

    private fun initListener() {

        commentAdapter.onItemClickListener =
            object : CommentAdapter.OnItemClickListener {
                override fun onClick(view: View, position: Int) {

                    var itemrating = product.comment[position].rating
                    Toast.makeText(
                        context,
                        product.comment[position].rating.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.ratingtext.text = itemrating.toString()
                    binding.ratingbar.rating = itemrating.toFloat() / 2
                }

            }
        binding.btnAddCount.setOnClickListener {
            binding.textMenuCount.text = (++cnt).toString()

        }
        binding.btnMinusCount.setOnClickListener {
            if (cnt >= 2)
                binding.textMenuCount.text = (--cnt).toString()

        }
        binding.btnAddList.setOnClickListener {
            OrderService(mainActivity).addShoppingList(product.id, cnt)
            Toast.makeText(context, "상품이 장바구니에 담겼습니다.", Toast.LENGTH_SHORT).show()
            mainActivity.openFragment(6)
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
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_menu_comment)
        dialog.show()
        val rat = dialog.findViewById<RatingBar>(R.id.ratingBarMenuDialogComment)
        rat.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                ProductService(requireContext()).addcomment(  rating*2,
                    binding.makecommentText.text.toString(),
                    getUserData(),
                    productId)
                Log.d(TAG, "onRatingChanged$rating: ")
                Toast.makeText(context,"별점이 등록되었습니다",Toast.LENGTH_SHORT).show()
                initData()
                initView()
                initAdapter()
                initListener()
                dialog.dismiss()
            }

        })

    }


    companion object {
        @JvmStatic
        fun newInstance(key: String, value: Int) =
            MenuDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(key, value)
                }
            }
    }
}