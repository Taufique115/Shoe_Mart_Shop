package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoemartshop.Activity.Model.ItemModel

object FavoriteManager {
    private val _favoriteItems = MutableLiveData<MutableList<ItemModel>>(mutableListOf())
    val favoriteItems: LiveData<MutableList<ItemModel>> get() = _favoriteItems

    fun toggleFavorite(item: ItemModel) {
        val currentList = _favoriteItems.value ?: mutableListOf()
        val existingItem = currentList.find { it.title == item.title }
        
        if (existingItem != null) {
            currentList.remove(existingItem)
            item.isFavorite = false
        } else {
            currentList.add(item)
            item.isFavorite = true
        }
        _favoriteItems.value = currentList
    }

    fun isFavorite(item: ItemModel): Boolean {
        val currentList = _favoriteItems.value ?: mutableListOf()
        return currentList.any { it.title == item.title }
    }
}
