package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Repository.AuthService
import com.example.shoemartshop.databinding.ActivityForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private var isSuccessState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load background shoe image from Cloudinary
        Glide.with(this)
            .load("https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes1_oyphr0.png")
            .into(binding.imgShoeBg)

        binding.btnSendOtp.setOnClickListener {
            if (isSuccessState) {
                navigateToLogin()
            } else {
                performPasswordReset()
            }
        }

        binding.txtSignIn.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun performPasswordReset() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            showFeedback("Please enter your registered email address", true)
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

        // Start loading state
        setLoadingState(true)

        AuthService.sendPasswordReset(
            email = email,
            onSuccess = {
                setLoadingState(false)
                isSuccessState = true

                // Hide input elements cleanly
                binding.lblEmail.visibility = View.GONE
                binding.emailContainer.visibility = View.GONE

                // Update text to show success status in a highly premium way
                binding.txtTitle.text = "Check Your Email"
                binding.txtSubTitle.text = "A secure reset link has been sent to $email. Please check your inbox (and spam folder) and follow the instructions to reset your password."

                // Transform the main button to go back to sign in
                binding.btnSendOtp.text = "Back to Sign In"

                showFeedback("Password reset link sent! Check your inbox.", false)
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
            binding.btnSendOtp.isEnabled = false
            binding.btnSendOtp.text = "Sending Link..."
            binding.etEmail.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnSendOtp.isEnabled = true
            binding.btnSendOtp.text = if (isSuccessState) "Back to Sign In" else "Send Reset Link"
            binding.etEmail.isEnabled = true
        }
    }

    private fun showFeedback(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val color = if (isError) 0xFFE53935.toInt() else 0xFF4CAF50.toInt()
        snackbar.view.setBackgroundColor(color)
        snackbar.show()
    }
}
