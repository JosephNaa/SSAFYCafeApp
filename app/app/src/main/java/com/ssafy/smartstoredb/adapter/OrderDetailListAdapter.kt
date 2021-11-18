package com.ssafy.smartstoredb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.dto.OrderDetail
import com.ssafy.smartstoredb.util.CommonUtils


class OrderDetailListAdapter(val context: Context, val orderDetail:List<OrderDetail>) :RecyclerView.Adapter<OrderDetailListAdapter.OrderDetailListHolder>(){

    inner class OrderDetailListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        val textShoppingMenuName = itemView.findViewById<TextView>(R.id.textShoppingMenuName)
        val textShoppingMenuMoney = itemView.findViewById<TextView>(R.id.textShoppingMenuMoney)
        val textShoppingMenuCount = itemView.findViewById<TextView>(R.id.textShoppingMenuCount)
        val textShoppingMenuMoneyAll = itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll)

        fun bindInfo(data:OrderDetail){
            var type = if(data.productType == "coffee") "잔" else "개"

            var img = context.resources.getIdentifier(data.img, "drawable", context.packageName)
            menuImage.setImageResource(img)

            textShoppingMenuName.text = data.productName
            textShoppingMenuMoney.text = CommonUtils.makeComma(data.unitPrice)
            textShoppingMenuCount.text = "${data.quantity} ${type}"
            textShoppingMenuMoneyAll.text = CommonUtils.makeComma(data.unitPrice * data.quantity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_order_detail_list, parent, false)
        return OrderDetailListHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDetailListHolder, position: Int) {
        holder.bindInfo(orderDetail[position])
    }

    override fun getItemCount(): Int {
        return orderDetail.size
    }
}

