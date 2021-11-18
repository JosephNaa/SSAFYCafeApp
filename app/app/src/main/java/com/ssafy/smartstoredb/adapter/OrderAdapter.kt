package com.ssafy.smartstoredb.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.dto.UserOrderDetail
import com.ssafy.smartstoredb.util.CommonUtils

private const val TAG = "OrderAdapter_싸피"
class OrderAdapter(val context: Context, val list:List<UserOrderDetail>) :RecyclerView.Adapter<OrderAdapter.OrderHolder>(){

    inner class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textMenuNames = itemView.findViewById<TextView>(R.id.textMenuNames)
        val textMenuPrice = itemView.findViewById<TextView>(R.id.textMenuPrice)
        val textMenuDate = itemView.findViewById<TextView>(R.id.textMenuDate)
        val textCompleted = itemView.findViewById<TextView>(R.id.textCompleted)

        fun bindInfo(data:UserOrderDetail){
            var img = context.resources.getIdentifier(data.img, "drawable", context.packageName)
            menuImage.setImageResource(img)
            if(data.sumQuantity > 1){
                textMenuNames.text = "${data.productName} 외 ${data.sumQuantity -1}건"  //외 x건
            }else{
                textMenuNames.text = data.productName
            }
            textMenuPrice.text = CommonUtils.makeComma(data.sumPrice)
            textMenuDate.text = data.orderDate
            textCompleted.text = "픽업완료" // or 준비중
            //클릭연결
            itemView.setOnClickListener{
                Log.d(TAG, "bindInfo: ${data.orderId}")
                itemClickListner.onClick(it, layoutPosition, data.orderId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, orderid:Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}

