package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoemartshop.Activity.Model.CartItemModel

object CartManager {
    private val _cartItems = MutableLiveData<MutableList<CartItemModel>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItemModel>> get() = _cartItems

    fun addItem(item: CartItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        
        // Check if item already exists (same title and color)
        val existingItem = currentList.find { it.title == item.title && it.selectedColorUrl == item.selectedColorUrl }
        if (existingItem != null) {
            existingItem.quantity += item.quantity
        } else {
            currentList.add(item)
        }
        
        _cartItems.value = currentList
    }

    fun removeItem(item: CartItemModel) {
        val currentList = _cartItems.value ?: mutableListOf()
        currentList.remove(item)
        _cartItems.value = currentList
    }

    fun updateQuantity(item: CartItemModel, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(item)
            return
        }
        val currentList = _cartItems.value ?: mutableListOf()
        val index = currentList.indexOf(item)
        if (index != -1) {
            currentList[index].quantity = newQuantity
            _cartItems.value = currentList
        }
    }
    
    fun getSubTotal(): Double {
        val list = _cartItems.value ?: return 0.0
        return list.sumOf { it.price * it.quantity }
    }
    
    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}
