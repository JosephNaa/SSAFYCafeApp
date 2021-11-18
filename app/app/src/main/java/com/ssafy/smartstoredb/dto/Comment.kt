package com.ssafy.smartstoredb.dto

data class Comment(
    val id: Int = -1,
    val userId: String,
    val productId: Int,
    val rating: Float,
    val comment: String
)