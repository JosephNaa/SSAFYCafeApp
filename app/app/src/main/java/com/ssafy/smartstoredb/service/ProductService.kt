package com.ssafy.smartstoredb.service

import android.content.Context
import android.util.Log
import androidx.core.content.contentValuesOf
import com.ssafy.smartstoredb.db.DBHelper
import com.ssafy.smartstoredb.dto.Comment
import com.ssafy.smartstoredb.dto.Product
import com.ssafy.smartstoredb.dto.User

private const val TAG = "UserService_싸피"

class ProductService(context: Context) : AbstractService(context) {
    val TABLE = "t_product"
    val COLUMNS = arrayOf("id", "name", "type", "price", "img")

    val COMMENT_TABLE = "t_comment"
    val COMMENT_COLUMNS = arrayOf("id", "user_id", "product_id", "rating", "comment")

    fun getProductList(): List<Product> {
        var product = arrayListOf<Product>()

        getReadableDatabase().use { db ->
            db.query(TABLE, COLUMNS, null, null, null, null, null).use {
                if (it.moveToFirst()) {
                    do {
                        product.add(
                            Product(
                                it.getInt(0),
                                it.getString(1),
                                it.getString(2),
                                it.getInt(3),
                                it.getString(4).split(".")[0]
                            )
                        )
                    } while (it.moveToNext())
                }
            }
        }

        return product
    }

    fun modifycomment(beforecomment: String, comment: String, userId: String) {
        var id = 0
        getReadableDatabase().use {
            val sql = """
                select id
                from t_comment
                where user_id like ? and comment like ?
                limit 1
            """.trimIndent()
            it.rawQuery(sql, arrayOf(userId, beforecomment)).use {
                if (it.moveToFirst()) {
                    id = it.getInt(0)
                }
            }

        }
        getWritableDatabase().use {

            val newcomment = contentValuesOf(
                "comment" to comment
            )

            it.update(
                COMMENT_TABLE,
                newcomment,
                "user_id like ? and comment like ? and id = ?",
                arrayOf(userId, beforecomment,id.toString())
            )
        }
    }

    fun deletecomment(comment: String, userId: String) {
        var id = 0
        getReadableDatabase().use {
            val sql = """
                select id
                from t_comment
                where user_id like ? and comment like ?
                limit 1
            """.trimIndent()
            it.rawQuery(sql, arrayOf(userId, comment)).use {
                if (it.moveToFirst()) {
                    id = it.getInt(0)
                }
            }

        }
        getWritableDatabase().use {
            it.delete(
                COMMENT_TABLE, "user_id like ? and comment like ? and id = ? ",
                arrayOf(userId, comment,id.toString())
            )
        }
    }

    fun getProduct(productId: Int): Product {
        var product = Product()

        getReadableDatabase().use { db ->
            db.query(TABLE, COLUMNS, "id=?", arrayOf(productId.toString()), null, null, null).use {
                if (it.moveToNext()) {
                    product = Product(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getInt(3),
                        it.getString(4).split(".")[0]
                    )
                }
            }
        }

        return product
    }

    fun getrating(comment: String): Double {
        var cnt = 0.0;
        getReadableDatabase().use { db ->
            val sql =
                """
                    select avg(rating) from t_comment where comment like ?
                """.trimIndent()
            db.rawQuery(sql, arrayOf(comment)).use {
                if (it.moveToFirst()) {
                    cnt = it.getDouble(0)
                }
            }
        }
        return cnt
    }

    fun addcomment(rating: Float, comment: String, userId: String, productId: Int) {
        var newcomment = contentValuesOf(
            "user_id" to userId,
            "product_id" to productId,
            "rating" to rating,
            "comment" to comment
        )
        getWritableDatabase().use {
            it.insert(COMMENT_TABLE, null, newcomment)
        }
    }

    fun getrating(productId: Int): Double {
        var cnt = 0.0;
        getReadableDatabase().use { db ->
            val sql =
                """
                    select avg(rating) from t_comment where product_id =?
                """.trimIndent()
            db.rawQuery(sql, arrayOf(productId.toString())).use {
                if (it.moveToFirst()) {
                    cnt = it.getDouble(0)
                }
            }
        }
        return cnt
    }

    fun getProductWithComments(productId: Int): Product {
        var product = Product()
        getReadableDatabase().use { db ->
            db.query(TABLE, COLUMNS, "id=?", arrayOf(productId.toString()), null, null, null).use {
                if (it.moveToNext()) {
                    product = Product(
                        it.getInt(0),
                        it.getString(1),
                        it.getString(2),
                        it.getInt(3),
                        it.getString(4).split(".")[0]
                    )
                }
            }
        }
        getReadableDatabase().use { db ->
            db.query(
                COMMENT_TABLE,
                COMMENT_COLUMNS,
                "product_id=?",
                arrayOf(productId.toString()),
                null,
                null,
                null
            ).use {
                if (it.moveToFirst()) {
                    do {
                        product.comment.add(
                            Comment(
                                it.getInt(0),
                                it.getString(1),
                                it.getInt(2),
                                it.getFloat(3),
                                it.getString(4)
                            )
                        )
                    } while (it.moveToNext())
                }
            }
        }

        return product
    }
}