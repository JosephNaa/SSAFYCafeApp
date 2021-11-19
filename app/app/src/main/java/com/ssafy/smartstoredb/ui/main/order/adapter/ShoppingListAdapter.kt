package com.ssafy.smartstoredb.ui.main.order.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.smartstore.dto.ShoppingCart
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.model.dto.OrderDetail
import com.ssafy.smartstoredb.util.CommonUtils

private const val TAG = "ShoppingListAdapter_싸피"
class ShoppingListAdapter(var list: MutableList<ShoppingCart>) :RecyclerView.Adapter<ShoppingListAdapter.ShoppingListHolder>(){

    inner class ShoppingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
        var textShoppingMenuName = itemView.findViewById<TextView>(R.id.textShoppingMenuName)
        var textShoppingMenuMoney = itemView.findViewById<TextView>(R.id.textShoppingMenuMoney)
        var textShoppingMenuCount = itemView.findViewById<TextView>(R.id.textShoppingMenuCount)
        var textShoppingMenuMoneyAll = itemView.findViewById<TextView>(R.id.textShoppingMenuMoneyAll)

        fun bindInfo(s: ShoppingCart) {
            Glide.with(itemView)
                .load("${ApplicationClass.MENU_IMGS_URL}${s.menuImg}")
                .into(menuImage)

            textShoppingMenuName.text = s.menuName
            textShoppingMenuMoney.text = s.menuPrice.toString()
            textShoppingMenuCount.text = s.menuCnt.toString()
            textShoppingMenuMoneyAll.text = s.totalPrice.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shopping_list, parent, false)
        return ShoppingListHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListHolder, position: Int) {
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
