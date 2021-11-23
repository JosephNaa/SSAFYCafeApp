package com.ssafy.smartstoredb.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.smartstoredb.config.ApplicationClass
import java.util.*

class OrderViewModel: ViewModel() {
    private var orderTable: String? = null

    private fun checkTime(time:Long): Boolean{
        val curTime = (Date().time+60*60*9*1000)

        return (curTime - time) > ApplicationClass.ORDER_COMPLETED_TIME
    }
}