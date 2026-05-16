package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoemartshop.Activity.Model.CartItemModel

data class OrderModel(
    val items: List<CartItemModel>,
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

object OrderManager {
    private val _orders = MutableLiveData<MutableList<OrderModel>>(mutableListOf())
    val orders: LiveData<MutableList<OrderModel>> get() = _orders

    fun addOrder(items: List<CartItemModel>, totalAmount: Double) {
        val currentOrders = _orders.value ?: mutableListOf()
        currentOrders.add(OrderModel(items.toList(), totalAmount))
        _orders.value = currentOrders
    }

    fun getTotalSpent(): Double {
        return _orders.value?.sumOf { it.totalAmount } ?: 0.0
    }

    fun getTotalShoesPurchased(): Int {
        return _orders.value?.sumOf { order -> order.items.sumOf { it.quantity } } ?: 0
    }
}
