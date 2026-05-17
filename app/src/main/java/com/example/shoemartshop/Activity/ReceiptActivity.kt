package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shoemartshop.Activity.Repository.UserManager
import com.example.shoemartshop.databinding.ActivityReceiptBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ReceiptActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReceiptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Read intent extras
        val subTotal = intent.getDoubleExtra("subTotal", 0.0)
        val paymentMethod = intent.getStringExtra("paymentMethod") ?: "Credit Card"

        setupReceiptDetails(subTotal, paymentMethod)
        setupListeners()
    }

    private fun setupReceiptDetails(subTotal: Double, paymentMethod: String) {
        val formatter = DecimalFormat("#,###.00")

        // 1. Generate 12-digit transaction reference number (e.g. 000085752257)
        val randomNum = abs(java.util.Random().nextLong()) % 1000000000000L
        val referenceNumber = String.format("%012d", randomNum)
        binding.txtReceiptRef.text = referenceNumber

        // 2. Format current date & time (e.g., 22 Mar 2026, 07:43 AM)
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
        val currentDateTime = sdf.format(Date())
        binding.txtReceiptDateTime.text = currentDateTime

        // 3. Set Payment Method
        binding.txtReceiptMethod.text = paymentMethod

        // 4. Set Prices (Delivery fee is removed!)
        binding.txtReceiptSubtotal.text = "৳${formatter.format(subTotal)}"
        binding.txtReceiptTotal.text = "৳${formatter.format(subTotal)}"

        // 5. Dynamic Thank You / User Details
        val currentUser = UserManager.getCurrentUser()
        val userName = currentUser.fullName.ifBlank {
            currentUser.email.substringBefore("@").replaceFirstChar { it.uppercase() }
        }.ifBlank { "Valued Customer" }
        
        binding.txtReceiptThankYou.text = "Thank you for shopping at ShoeMart, $userName! Your order has been placed and a digital copy is sent to ${currentUser.email}."
    }

    private fun setupListeners() {
        binding.btnReceiptDone.setOnClickListener {
            // Cleanly clear the backstack and route back to Home Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
