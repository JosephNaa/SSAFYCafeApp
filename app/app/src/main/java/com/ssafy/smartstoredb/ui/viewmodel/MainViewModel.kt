package com.ssafy.smartstoredb.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.smartstore.dto.ShoppingCart

class MainViewModel: ViewModel() {
    private var orderTable: String? = null
    private var shoppingCart = mutableListOf<ShoppingCart>()
    var distance = MutableLiveData<Double>()

    fun getOrderTable(): String? {
        return orderTable
    }

    fun setOrderTable(tableNum: String?) {
        orderTable = tableNum
    }

    fun getShoppingCart(): MutableList<ShoppingCart> {
        return shoppingCart
    }

    fun addShoppingItem(item: ShoppingCart) {
        shoppingCart.add(item)
    }

    fun clearShoppingCart() {
        shoppingCart.clear()
    }
}