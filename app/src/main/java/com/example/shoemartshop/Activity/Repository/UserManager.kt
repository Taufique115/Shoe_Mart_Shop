package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class UserModel(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val role: String = "Customer",
    val createdAt: Long = 0,
    val lastLogin: Long = 0,
    val profileImage: String = "default_avatar",
    @field:JvmField
    val isVerified: Boolean = false
) {
    // Backwards compatibility with the existing UI (which uses .name)
    val name: String get() = fullName
}

object UserManager {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _currentUser = MutableLiveData<UserModel>(UserModel())
    val currentUser: LiveData<UserModel> get() = _currentUser

    init {
        // Observe auth state changes in real-time
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Fetch user detailed profile from Firestore
                fetchUserDetails(firebaseUser.uid)
                // Load past order history from Firestore
                OrderManager.loadUserOrders()
            } else {
                // Clear state back to guest defaults
                _currentUser.postValue(UserModel())
            }
        }
    }

    fun fetchUserDetails(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    if (user != null) {
                        _currentUser.postValue(user)
                    }
                } else {
                    // Fallback to basic auth info if firestore record is not written yet
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val basicUser = UserModel(
                            uid = firebaseUser.uid,
                            fullName = firebaseUser.displayName ?: "Mostafa Jaman Taufique",
                            email = firebaseUser.email ?: "mostafataufique@gmail.com",
                            isVerified = firebaseUser.isEmailVerified
                        )
                        _currentUser.postValue(basicUser)
                    }
                }
            }
            .addOnFailureListener {
                // If it fails, fallback to guest default
                _currentUser.postValue(UserModel())
            }
    }

    fun getCurrentUser(): UserModel {
        return _currentUser.value ?: UserModel()
    }

    fun updateUser(name: String, email: String, phone: String, location: String) {
        val current = getCurrentUser()
        val updated = current.copy(
            fullName = name,
            email = email,
            phone = phone,
            location = location
        )
        _currentUser.value = updated

        // Sync change back to Firestore in the background if authenticated
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            db.collection("users").document(firebaseUser.uid)
                .set(updated)
        }
    }
}
