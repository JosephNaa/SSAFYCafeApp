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
import com.ssafy.smartstoredb.dto.Product

private const val TAG = "MenuAdapter_싸피"
class MenuAdapter(val context: Context, val prodList:List<Product>) :RecyclerView.Adapter<MenuAdapter.MenuHolder>(){

    inner class MenuHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val menuName = itemView.findViewById<TextView>(R.id.textMenuNames)
        val menuImage = itemView.findViewById<ImageView>(R.id.menuImage)

        fun bindInfo(product : Product){
            menuName.text = product.name
            var img = context.resources.getIdentifier(product.img, "drawable", context.packageName)
            menuImage.setImageResource(img)

            itemView.setOnClickListener{
                itemClickListner.onClick(it, position, prodList[position].id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_menu, parent, false)
        return MenuHolder(view)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        holder.apply{
            bindInfo(prodList[position])
        }
    }

    override fun getItemCount(): Int {
        return prodList.size
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int, productId:Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }
}

