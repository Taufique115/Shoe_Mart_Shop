package com.example.shoemartshop.Activity.Model

import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName

data class ItemModel(
    var id: String = "",
    var title: String = "",
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var isFavorite: Boolean = false
) {
    // 1. Backing property mapped to Firebase's "picUrl" key. Accepts Any? (String or List) safely.
    var picUrlObj: Any? = null
        @PropertyName("picUrl")
        get() = field
        @PropertyName("picUrl")
        set(value) {
            field = value
        }

    // 2. Expose picUrl as a public property of type String for developers, completely excluded from Firebase reflection.
    @get:Exclude
    @set:Exclude
    var picUrl: String
        get() {
            return when (val obj = picUrlObj) {
                is String -> obj
                is List<*> -> obj.firstOrNull()?.toString() ?: ""
                else -> obj?.toString() ?: ""
            }
        }
        set(value) {
            picUrlObj = value
        }

    // Secondary constructor to ensure manual compiles continue to build
    constructor(
        id: String,
        title: String,
        picUrl: String,
        price: Double,
        rating: Double,
        isFavorite: Boolean = false
    ) : this(
        id = id,
        title = title,
        price = price,
        rating = rating,
        isFavorite = isFavorite
    ) {
        this.picUrl = picUrl
    }
}
