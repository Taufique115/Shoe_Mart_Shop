package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class UserModel(
    val name: String = "Mostafa Jaman Taufique",
    val email: String = "mostafataufique@gmail.com",
    val phone: String = "01621352790",
    val location: String = "Dhaka, Bangladesh"
)

object UserManager {
    private val _currentUser = MutableLiveData<UserModel>(UserModel())
    val currentUser: LiveData<UserModel> get() = _currentUser

    fun updateUser(name: String, email: String, phone: String, location: String) {
        _currentUser.value = UserModel(name, email, phone, location)
    }
    
    fun getCurrentUser(): UserModel {
        return _currentUser.value ?: UserModel()
    }
}
