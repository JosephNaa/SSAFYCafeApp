package com.ssafy.smartstoredb.ui.main.order.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.adapter.CommentListener
import com.ssafy.smartstore.response.MenuDetailWithCommentResponse
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.config.ApplicationClass
import com.ssafy.smartstoredb.model.dto.Comment

private const val TAG = "CommentAdapter_싸피"
class CommentAdapter(var list:List<MenuDetailWithCommentResponse> ) :RecyclerView.Adapter<CommentAdapter.CommentHolder>(){

    var user = ApplicationClass.sharedPreferencesUtil.getUser()

    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bindInfo(data :MenuDetailWithCommentResponse){
            Log.d(TAG, "bindInfo: $data")
            itemView.findViewById<TextView>(R.id.textNoticeContent).text = data.commentContent

            var modifyAccept = itemView.findViewById<ImageView>(R.id.iv_modify_accept_comment)
            var modifyCancel = itemView.findViewById<ImageView>(R.id.iv_modify_cancel_comment)
            var modifyComment = itemView.findViewById<ImageView>(R.id.iv_modify_comment)
            var deleteComment = itemView.findViewById<ImageView>(R.id.iv_delete_comment)
            var commentContent = itemView.findViewById<EditText>(R.id.et_comment_content)


            if (user.id == data.userId) {
                modifyAccept.visibility = View.VISIBLE
                modifyCancel.visibility = View.VISIBLE
                modifyComment.visibility = View.VISIBLE
                deleteComment.visibility = View.VISIBLE
                commentContent.visibility = View.GONE
            } else {
                modifyAccept.visibility = View.GONE
                modifyCancel.visibility = View.GONE
                modifyComment.visibility = View.GONE
                deleteComment.visibility = View.GONE
                commentContent.visibility = View.GONE
            }

            modifyAccept.setOnClickListener {
                data.commentContent = commentContent.text.toString()
                commentContent.text = null
                commentContent.visibility = View.GONE
                itemClickListner.onClick(it, layoutPosition, data, 2)
            }

            modifyCancel.setOnClickListener {
                commentContent.text = null
                commentContent.visibility = View.GONE

            }

            modifyComment.setOnClickListener {
                commentContent.visibility = View.VISIBLE
            }

            deleteComment.setOnClickListener {
                itemClickListner.onClick(it, layoutPosition, data, 3)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        Log.d(TAG, "onCreateViewHolder: ")
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ")
        holder.bindInfo(list[position])
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${list.size} ")
        return list.size
    }

    interface ItemClickListener {
        fun onClick(view: View,  position: Int, comment:MenuDetailWithCommentResponse, flag: Int)
    }

    //클릭리스너 선언
    private lateinit var itemClickListner: CommentAdapter.ItemClickListener
    //클릭리스너 등록 매소드
    fun setItemClickListener(itemClickListener: CommentAdapter.ItemClickListener) {
        Log.d(TAG, "setItemClickListener: ")
        this.itemClickListner = itemClickListener
    }

    fun updateAdapter(mDataList: List<MenuDetailWithCommentResponse>) {
        this.list = mDataList;
        Log.d(TAG, "updateAdapter: ")
        notifyDataSetChanged();
    }
}

