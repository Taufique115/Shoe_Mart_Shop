package com.example.shoemartshop.Activity.Model

data class CartItemModel(
    val title: String,
    val brand: String,
    val price: Double,
    val picUrl: String,
    var quantity: Int,
    var selectedColorUrl: String? = null
)
