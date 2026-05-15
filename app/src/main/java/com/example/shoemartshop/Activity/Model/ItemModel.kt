package com.example.shoemartshop.Activity.Model

data class ItemModel(
    var title: String = "",
    var picUrl: String = "",
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var isFavorite: Boolean = false
)
