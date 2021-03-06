package com.ssafy.smartstoredb.ui.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.ssafy.smartstoredb.data.service.MyFirebaseMessagingService
import com.ssafy.smartstoredb.databinding.ListItemNoticeBinding
import com.ssafy.smartstoredb.ui.main.SP_NAME


class NoticeAdapter :RecyclerView.Adapter<NoticeAdapter.NoticeHolder>(){

    var noticeData = MyFirebaseMessagingService().fcmList

    inner class NoticeHolder(val binding: ListItemNoticeBinding) : RecyclerView.ViewHolder(binding.root){
        var mNotice: String? = null
        init {
            binding.btnNoticeDelete.setOnClickListener {
                noticeData.remove(mNotice)
                writeSharedPreference("fcm", noticeData)
                notifyDataSetChanged()
            }
        }
        fun bindInfo(notice: String){
            binding.textNoticeContent.text = notice
            this.mNotice = notice
        }

        // SP 저장
        private fun writeSharedPreference(key:String, value:ArrayList<String>){
            val sp = binding.root.context.getSharedPreferences(SP_NAME, FirebaseMessagingService.MODE_PRIVATE)
            val editor = sp.edit()
            val gson = Gson()
            val json: String = gson.toJson(value)
            editor.putString(key, json)
            editor.apply()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeHolder {
        val binding = ListItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_notice, parent, false)
        return NoticeHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeHolder, position: Int) {
        val notice = noticeData.get(position)
        holder.bindInfo(notice)
    }

    override fun getItemCount(): Int {
        return noticeData.size
    }

    fun update(newList: ArrayList<String>) {
        this.noticeData = newList
        notifyDataSetChanged()
    }
}

