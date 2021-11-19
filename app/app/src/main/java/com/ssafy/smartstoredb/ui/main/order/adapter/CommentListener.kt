package com.ssafy.smartstore.adapter

import com.ssafy.smartstoredb.model.dto.Comment


interface CommentListener {
    fun addComment(comment: Comment)
    fun updateComment(comment: Comment) // 코멘트 수정
    fun removeComment(commentId: Int) // 코멘트 삭제
}