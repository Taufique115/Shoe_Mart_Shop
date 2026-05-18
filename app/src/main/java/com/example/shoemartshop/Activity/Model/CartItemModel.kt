package com.example.shoemartshop.Activity.Model

data class CartItemModel(
    val id: String = "",
    val title: String = "",
    val brand: String = "",
    val price: Double = 0.0,
    val picUrl: String = "",
    var quantity: Int = 0,
    var selectedColorUrl: String? = null,
    var selectedSize: String = "",
    var selectedColor: String = ""
)
