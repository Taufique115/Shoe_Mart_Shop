package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Repository.AuthService
import com.example.shoemartshop.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load background shoe image from Cloudinary
        Glide.with(this)
            .load("https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes1_oyphr0.png")
            .into(binding.imgShoeBg)

        binding.btnSignIn.setOnClickListener {
            performLogin()
        }

        binding.txtSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Password Visibility Toggle
        var isPasswordVisible = false
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                binding.btnTogglePassword.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF7A00"))
            } else {
                binding.etPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                binding.btnTogglePassword.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#888888"))
            }
            binding.etPassword.text?.let { binding.etPassword.setSelection(it.length) }
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Validation
        if (email.isEmpty()) {
            showFeedback("Email address cannot be empty", true)
            binding.etEmail.error = "Email required"
            binding.etEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showFeedback("Please enter a valid email address", true)
            binding.etEmail.error = "Invalid format"
            binding.etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            showFeedback("Password cannot be empty", true)
            binding.etPassword.error = "Password required"
            binding.etPassword.requestFocus()
            return
        }

        // 2. Start Loading State
        setLoadingState(true)

        // 3. Authenticate
        AuthService.signIn(
            email = email,
            password = password,
            onSuccess = {
                setLoadingState(false)
                showFeedback("Welcome back! Login successful.", false)
                
                // Redirect to Dashboard and clear backstack
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            onEmailNotVerified = {
                setLoadingState(false)
                showFeedback("Please verify your email address. A new link has been sent.", true)
            },
            onFailure = { errorMessage ->
                setLoadingState(false)
                showFeedback(errorMessage, true)
            }
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignIn.isEnabled = false
            binding.btnSignIn.text = "Signing In..."
            binding.etEmail.isEnabled = false
            binding.etPassword.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnSignIn.isEnabled = true
            binding.btnSignIn.text = "Sign In"
            binding.etEmail.isEnabled = true
            binding.etPassword.isEnabled = true
        }
    }

    private fun showFeedback(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val color = if (isError) 0xFFE53935.toInt() else 0xFF4CAF50.toInt()
        snackbar.view.setBackgroundColor(color)
        snackbar.show()
    }
}
