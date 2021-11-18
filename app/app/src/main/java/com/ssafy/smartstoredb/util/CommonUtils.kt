package com.ssafy.smartstoredb.util

import java.text.DecimalFormat

object CommonUtils {

    //천단위 콤마
    fun makeComma(num:Int):String{
        var comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }
}