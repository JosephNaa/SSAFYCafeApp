package com.ssafy.smartstoredb.service

import android.content.Context
import androidx.core.content.contentValuesOf
import com.ssafy.smartstoredb.dto.User
import com.ssafy.smartstoredb.dto.UserOrderDetail


private const val TAG = "UserService_싸피"
class UserService(context: Context) : AbstractService(context) {
    val TABLE = "t_user"
    val COLUMNS = arrayOf("id", "name", "pass", "stamps")

    fun login(id: String, password: String) : User {
        var user:User

        getReadableDatabase().use {
            it.query(TABLE, COLUMNS, "id = ? and pass = ?", arrayOf(id, password), null, null, null).use{
                if (it.moveToNext()) {
                    user = User(it.getString(0), it.getString(1), it.getString(2), it.getInt(3))
                }else{
                    user = User()
                }
            }
        }
        return user
    }

    //회원 테이블에 있으면 false, 없으면 true를 리턴
    fun isAvailableId(id:String) : Boolean{
        var result : Boolean
        getReadableDatabase().use {
            it.query(TABLE, COLUMNS, "id=?", arrayOf(id), null, null, null).use {
                result = !it.moveToNext()
            }
        }
        return result
    }

    fun join(id: String, name:String, password: String) : Long{
        var newUser = contentValuesOf("id" to id, "name" to name, "pass" to password)

        getWritableDatabase().use {
             return it.insert(TABLE, null, newUser)
        }
    }

    //주문내역 조회
    fun getOrderList(id:String): ArrayList<UserOrderDetail>{
        var list = arrayListOf<UserOrderDetail>()

        getReadableDatabase().use { db ->
            var sql = """
                select 
                    bb.id as order_id,
                    strftime('%Y.%m.%d.%H:%M', bb.order_time, '+9 hours') order_time,
                    bb.quantity, 
                    bb.sum_price, 
                    prod.img,
                    prod.id, 
                    name 
                from t_product prod, 
                (select 
                    a.o_id as id, 
                    sum(quantity) as quantity, 
                    a.order_time, 
                    sum(b.quantity * product.price) as sum_price,
                    min(product_id) min_id
                from t_order a, t_order_detail b, t_product product
                where a.user_id = ?
                and a.o_id = b.order_id
                and b.product_id = product.id
                group by o_id
                ) bb
                where prod.id = bb.min_id
            """.trimIndent()

            db.rawQuery(sql, arrayOf(id)).use {
                if (it.moveToFirst()) {
                    do{
                        list.add(
                            UserOrderDetail( it.getInt(0),
                                it.getString(1),
                                it.getInt(2),
                                it.getInt(3),
                                it.getString(4).split(".")[0],
                                it.getInt(5),
                                it.getString(6) ) )
                    }while(it.moveToNext())
                }

            }

        }

        return list
    }
}