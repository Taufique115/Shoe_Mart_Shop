package com.example.shoemartshop.Activity.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.shoemartshop.Activity.Model.BrandModel
import com.example.shoemartshop.Activity.Model.ItemModel
import com.example.shoemartshop.Activity.Model.SliderModel
import com.example.shoemartshop.Activity.Repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    val brands: LiveData<MutableList<BrandModel>> = repository.brands
    val banners: LiveData<MutableList<SliderModel>> = repository.banners
    val recommended: LiveData<MutableList<ItemModel>> = repository.recommended

    fun loadBrands() {
        repository.loadBrands()
    }

    fun loadBanners() {
        repository.loadBanners()
    }

    fun loadRecommended() {
        repository.loadRecommended()
    }
}
