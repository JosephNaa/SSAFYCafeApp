package com.ssafy.smartstoredb.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.R


class LatestOrderAdapter :RecyclerView.Adapter<LatestOrderAdapter.LatestOrderHolder>(){

    inner class LatestOrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindInfo(){


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestOrderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_latest_order, parent, false)
        return LatestOrderHolder(view)
    }

    override fun onBindViewHolder(holder: LatestOrderHolder, position: Int) {
//        holder.bind()
        holder.apply {
            bindInfo()
            //클릭연결
            itemView.setOnClickListener{
                itemClickListner.onClick(it, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    //클릭 인터페이스 정의 사용하는 곳에서 만들어준다.
    interface ItemClickListener {
        fun onClick(view: View,  position: Int)
    }
    //클릭리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

}

