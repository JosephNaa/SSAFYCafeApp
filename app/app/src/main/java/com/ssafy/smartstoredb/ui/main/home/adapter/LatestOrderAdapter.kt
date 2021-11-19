package com.ssafy.smartstoredb.ui.main.home.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.response.LatestOrderResponse
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.util.CommonUtils
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "LatestOrderAdapter_싸피"
class LatestOrderAdapter(val context: Context, var list: List<LatestOrderResponse>) :RecyclerView.Adapter<LatestOrderAdapter.LatestOrderHolder>(){

    var user = ApplicationClass.sharedPreferencesUtil.getUser()

    inner class LatestOrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<ImageView>(R.id.image)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        val textCompleted = itemView.findViewById<TextView>(R.id.textCompleted)

        fun bindInfo(data:LatestOrderResponse){
//            Log.d(TAG, "bindInfo: ${data.img}")
//            var img = context.resources.getIdentifier(data.img, "drawable", context.packageName)
//            image.setImageResource(img)

            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${data.img}")
                .into(image)

            if(data.orderCnt > 1){
                textMenuNames.text = "${data.productName} 외 ${data.orderCnt -1}건"  //외 x건
            }else{
                textMenuNames.text = data.productName
            }
            textMenuPrice.text = CommonUtils.makeComma(data.productPrice)
            textMenuDate.text =  SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(data.orderDate)
//            textCompleted.text = "픽업완료" // or 준비중
            //클릭연결
            itemView.setOnClickListener{
                itemClickListner.onClick(it, layoutPosition, data.orderId)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestOrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_latest_order, parent, false)
        return LatestOrderHolder(view)
    }

    override fun onBindViewHolder(holder: LatestOrderHolder, position: Int) {
//        holder.bind()
        holder.apply {
            bindInfo(list[position])
            //클릭연결
//            itemView.setOnClickListener{
//                itemClickListner.onClick(it, position)
//            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, orderId: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    fun updateAdapter(mDataList: List<LatestOrderResponse>) {
        this.list = mDataList;
        notifyDataSetChanged();
    }
}

