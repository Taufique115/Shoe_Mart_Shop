package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Repository.AuthService
import com.example.shoemartshop.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (ime.bottom > 0) ime.bottom else systemBars.bottom
            binding.scrollView.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                bottomPadding
            )
            insets
        }

        // Load background shoe image from Cloudinary
        Glide.with(this)
            .load("https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes1_oyphr0.png")
            .into(binding.imgShoeBg)

        binding.btnCreateAccount.setOnClickListener {
            performRegistration()
        }

        binding.checkAdmin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.layoutAdminCode.visibility = View.VISIBLE
            } else {
                binding.layoutAdminCode.visibility = View.GONE
                binding.etAdminCode.setText("")
            }
        }

        binding.txtSignIn.setOnClickListener {
            finish()
        }

        // Password Visibility Toggles
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

        var isConfirmPasswordVisible = false
        binding.btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                binding.etConfirmPassword.transformationMethod = android.text.method.HideReturnsTransformationMethod.getInstance()
                binding.btnToggleConfirmPassword.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FF7A00"))
            } else {
                binding.etConfirmPassword.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
                binding.btnToggleConfirmPassword.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#888888"))
            }
            binding.etConfirmPassword.text?.let { binding.etConfirmPassword.setSelection(it.length) }
        }
    }

    private fun performRegistration() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val isTermsChecked = binding.checkTerms.isChecked

        // 1. Validation Checks
        if (fullName.isEmpty() || fullName.length < 3) {
            showFeedback("Full Name must be at least 3 characters", true)
            binding.etFullName.error = "Name too short"
            binding.etFullName.requestFocus()
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showFeedback("Please enter a valid email address", true)
            binding.etEmail.error = "Invalid email"
            binding.etEmail.requestFocus()
            return
        }

        if (phone.isEmpty() || phone.length < 11) {
            showFeedback("Please enter a valid phone number (minimum 11 digits)", true)
            binding.etPhone.error = "Invalid phone number"
            binding.etPhone.requestFocus()
            return
        }

        if (location.isEmpty() || location.length < 3) {
            showFeedback("Please enter your city and country", true)
            binding.etLocation.error = "Location too short"
            binding.etLocation.requestFocus()
            return
        }

        if (!isPasswordStrong(password)) {
            showFeedback("Password must be at least 8 characters long and contain both letters and digits", true)
            binding.etPassword.error = "Weak password"
            binding.etPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            showFeedback("Passwords do not match", true)
            binding.etConfirmPassword.error = "Passwords mismatch"
            binding.etConfirmPassword.requestFocus()
            return
        }

        if (!isTermsChecked) {
            showFeedback("You must agree to the Terms & Conditions and Privacy Policy", true)
            binding.checkTerms.requestFocus()
            return
        }

        val isAdminChecked = binding.checkAdmin.isChecked
        val adminCode = binding.etAdminCode.text.toString().trim()

        if (isAdminChecked) {
            if (adminCode.isEmpty()) {
                showFeedback("Admin Secret Passcode cannot be empty", true)
                binding.etAdminCode.error = "Passcode required"
                binding.etAdminCode.requestFocus()
                return
            }
            if (adminCode != "admin123") {
                showFeedback("Invalid Admin Secret Passcode!", true)
                binding.etAdminCode.error = "Wrong passcode"
                binding.etAdminCode.requestFocus()
                return
            }
        }

        val role = if (isAdminChecked) "Admin" else "Customer"

        // 2. Start Loading State
        setLoadingState(true)

        // 3. Register user with Firebase and write to Firestore
        AuthService.signUp(
            fullName = fullName,
            email = email,
            phone = phone,
            location = location,
            password = password,
            role = role,
            onSuccess = {
                setLoadingState(false)
                
                // Show high-fidelity instruction dialog or long Snackbar
                showFeedback("Account created! Verify your email to login.", false)
                
                // Complete registration and close activity
                android.widget.Toast.makeText(
                    this,
                    "Registration successful! Verification email sent.",
                    android.widget.Toast.LENGTH_LONG
                ).show()
                
                finish()
            },
            onFailure = { errorMessage ->
                setLoadingState(false)
                showFeedback(errorMessage, true)
            }
        )
    }

    private fun isPasswordStrong(password: String): Boolean {
        if (password.length < 8) return false
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnCreateAccount.isEnabled = false
            binding.btnCreateAccount.text = "Creating Account..."
            binding.etFullName.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etPhone.isEnabled = false
            binding.etLocation.isEnabled = false
            binding.etPassword.isEnabled = false
            binding.etConfirmPassword.isEnabled = false
            binding.checkTerms.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnCreateAccount.isEnabled = true
            binding.btnCreateAccount.text = "Create Account"
            binding.etFullName.isEnabled = true
            binding.etEmail.isEnabled = true
            binding.etPhone.isEnabled = true
            binding.etLocation.isEnabled = true
            binding.etPassword.isEnabled = true
            binding.etConfirmPassword.isEnabled = true
            binding.checkTerms.isEnabled = true
        }
    }

    private fun showFeedback(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val color = if (isError) 0xFFE53935.toInt() else 0xFF4CAF50.toInt()
        snackbar.view.setBackgroundColor(color)
        snackbar.show()
    }
}
