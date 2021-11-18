package com.ssafy.smartstoredb.adapter

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstoredb.ApplicationClass
import com.ssafy.smartstoredb.R
import com.ssafy.smartstoredb.dto.Comment
import com.ssafy.smartstoredb.service.ProductService
import org.w3c.dom.Text


class CommentAdapter(val context: Context, val productId: Int, val userid: String) :
    RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    var list: List<Comment> = ProductService(context).getProductWithComments(productId).comment

    inner class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var editcomment = itemView.findViewById<TextView>(R.id.et_comment_content)
        var textNoticeContent = itemView.findViewById<TextView>(R.id.textNoticeContent)
        var modifyaccept = itemView.findViewById<ImageView>(R.id.iv_modify_accept_comment)
        var modifycancle = itemView.findViewById<ImageView>(R.id.iv_modify_cancel_comment)
        var modify = itemView.findViewById<ImageView>(R.id.iv_modify_comment)
        var delete = itemView.findViewById<ImageView>(R.id.iv_delete_comment)
        fun bindInfo(data: Comment) {
            editcomment.visibility = View.GONE
            textNoticeContent.text = data.comment
            modifyaccept.visibility = View.GONE
            modifycancle.visibility = View.GONE
            if (data.userId == userid) {
                modify.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
            } else {
                modify.visibility = View.GONE
                delete.visibility = View.GONE
            }
            modify.setOnClickListener {
                editcomment.visibility = View.VISIBLE
                modifyaccept.visibility = View.VISIBLE
                modifycancle.visibility = View.VISIBLE
                modify.visibility = View.GONE
                delete.visibility = View.GONE

            }
            modifyaccept.setOnClickListener {
                change(itemView)
                editcomment.visibility = View.GONE
                modifyaccept.visibility = View.GONE
                modifycancle.visibility = View.GONE
                modify.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
            }
            modifycancle.setOnClickListener {
                editcomment.visibility = View.GONE
                modifyaccept.visibility = View.GONE
                modifycancle.visibility = View.GONE
                modify.visibility = View.VISIBLE
                delete.visibility = View.VISIBLE
            }
            delete.setOnClickListener {
                delete(itemView)
            }
        }
    }

    fun change(itemView: View) {
        var editcomment = itemView.findViewById<TextView>(R.id.et_comment_content)
        var textNoticeContent = itemView.findViewById<TextView>(R.id.textNoticeContent)
        ProductService(context).modifycomment(
            textNoticeContent.text.toString(),
            editcomment.text.toString(),
            userid
        )
        list = ProductService(context).getProductWithComments(productId).comment
        notifyDataSetChanged()
        Toast.makeText(context,"상품평이 수정되었습니다",Toast.LENGTH_SHORT).show()
    }
    fun noti(){
        notifyDataSetChanged()
    }
    fun delete(itemView: View){
        var textNoticeContent = itemView.findViewById<TextView>(R.id.textNoticeContent)
        ProductService(context).deletecomment(
            textNoticeContent.text.toString(),
            userid
        )
        list = ProductService(context).getProductWithComments(productId).comment
        notifyDataSetChanged()
        Toast.makeText(context,"상품평이 삭제되었습니다",Toast.LENGTH_SHORT).show()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.apply {
            bindInfo(list[position])
            itemView.setOnClickListener {
                onItemClickListener.onClick(itemView, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    lateinit var onItemClickListener: OnItemClickListener
}

