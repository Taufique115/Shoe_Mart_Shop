package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoemartshop.Activity.Model.CartItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class OrderModel(
    var orderId: String = "",
    var userId: String = "",
    var customerName: String = "",
    var customerEmail: String = "",
    var items: List<CartItemModel> = emptyList(),
    var totalAmount: Double = 0.0,
    var timestamp: Long = System.currentTimeMillis()
)

object OrderManager {
    private val _orders = MutableLiveData<MutableList<OrderModel>>(mutableListOf())
    val orders: LiveData<MutableList<OrderModel>> get() = _orders

    fun addOrder(items: List<CartItemModel>, totalAmount: Double) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val firebaseUser = auth.currentUser

        val user = UserManager.getCurrentUser()
        val orderId = db.collection("orders").document().id

        val newOrder = OrderModel(
            orderId = orderId,
            userId = firebaseUser?.uid ?: "guest",
            customerName = user.fullName.ifEmpty { "Guest Shopper" },
            customerEmail = user.email.ifEmpty { "guest@shoemart.com" },
            items = items.toList(),
            totalAmount = totalAmount,
            timestamp = System.currentTimeMillis()
        )

        // 1. Add to local cache for immediate UI feedback
        val currentOrders = _orders.value ?: mutableListOf()
        currentOrders.add(newOrder)
        _orders.value = currentOrders

        // 2. Persist to global Firestore collection in background
        db.collection("orders").document(orderId).set(newOrder)
    }

    fun loadUserOrders() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val firebaseUser = auth.currentUser ?: return

        db.collection("orders")
            .whereEqualTo("userId", firebaseUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<OrderModel>()
                for (doc in documents) {
                    val order = doc.toObject(OrderModel::class.java)
                    list.add(order)
                }
                _orders.postValue(list)
            }
    }

    fun getTotalSpent(): Double {
        return _orders.value?.sumOf { it.totalAmount } ?: 0.0
    }

    fun getTotalShoesPurchased(): Int {
        return _orders.value?.sumOf { order -> order.items.sumOf { it.quantity } } ?: 0
    }
}
