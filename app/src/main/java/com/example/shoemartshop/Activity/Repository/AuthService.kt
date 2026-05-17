package com.example.shoemartshop.Activity.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthService {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /**
     * Registers a new user with email and password.
     * Triggers verification email, writes profile details to Firestore, and logs out the user immediately
     * so they are forced to verify their email before their first login.
     */
    fun signUp(
        fullName: String,
        email: String,
        phone: String,
        location: String,
        password: String,
        role: String = "Customer",
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        // 1. Trigger verification email
                        firebaseUser.sendEmailVerification()
 
                        // 2. Prepare user record
                        val userModel = UserModel(
                            uid = firebaseUser.uid,
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            location = location,
                            role = role,
                            createdAt = System.currentTimeMillis(),
                            lastLogin = System.currentTimeMillis(),
                            profileImage = "default_avatar",
                            isVerified = false
                        )
 
                        // 3. Write user details to Firestore
                        db.collection("users").document(firebaseUser.uid)
                            .set(userModel)
                            .addOnSuccessListener {
                                // 4. Sign out since verification is strictly required to log in
                                auth.signOut()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure("Firestore Error: ${e.localizedMessage}")
                            }
                    } else {
                        onFailure("Session initialization failed.")
                    }
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Registration failed.")
                }
            }
    }

    /**
     * Authenticates a user with email and password.
     * Enforces strict email verification check.
     */
    fun signIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onEmailNotVerified: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        if (firebaseUser.isEmailVerified) {
                            // Update last login timestamp and verification status in Firestore
                            db.collection("users").document(firebaseUser.uid)
                                .update(
                                    "lastLogin", System.currentTimeMillis(),
                                    "isVerified", true
                                )
                                .addOnCompleteListener {
                                    // Trigger real-time fetching of new details
                                    UserManager.fetchUserDetails(firebaseUser.uid)
                                    onSuccess()
                                }
                        } else {
                            // Resend verification email and log out immediately
                            firebaseUser.sendEmailVerification()
                            auth.signOut()
                            onEmailNotVerified()
                        }
                    } else {
                        onFailure("User session invalid.")
                    }
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Invalid email or password.")
                }
            }
    }

    /**
     * Sends a secure password reset link to the user's email address.
     */
    fun sendPasswordReset(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Failed to send reset email.")
                }
            }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Helper to verify if the user session exists and is verified.
     */
    fun isUserLoggedIn(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }
}
