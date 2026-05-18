package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Adapter.PurchasedShoesAdapter
import com.example.shoemartshop.Activity.Repository.OrderManager
import com.example.shoemartshop.Activity.Repository.UserManager
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserDetails()
        loadStatIcons()

        // Observe orders dynamically to calculate stats and set up the list reactively
        OrderManager.orders.observe(this) { orders ->
            calculateStats()
            setupPurchasedList()
        }

        binding.btnSignOut.setOnClickListener {
            com.example.shoemartshop.Activity.Repository.AuthService.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.cardAdminPanel.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserDetails() {
        // Use reactive LiveData observer to dynamically update the UI if the role toggles!
        UserManager.currentUser.observe(this) { user ->
            binding.txtName.text = user.name
            binding.txtEmail.text = user.email
            binding.txtPhone.text = user.phone
            binding.txtLocation.text = user.location

            if (user.role == "Admin") {
                binding.cardAdminPanel.visibility = View.VISIBLE
            } else {
                binding.cardAdminPanel.visibility = View.GONE
            }
        }

        // Use the cartoon avatar
        binding.imgProfile.setImageResource(R.drawable.ic_male_avatar)
        binding.imgProfile.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
        binding.imgProfile.setBackgroundColor(0xFFF8F9FA.toInt())
    }



    private fun loadStatIcons() {
        val bkashUrl = "https://res.cloudinary.com/dxafieanc/image/upload/v1778877626/bkash_apxhc5.png"
        val shoeUrl = "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_40_00_AM_5_fpcqds.png"

        Glide.with(this).load(bkashUrl).into(binding.imgStatSpent)
        Glide.with(this).load(shoeUrl).into(binding.imgStatShoes)
    }

    private fun calculateStats() {
        val totalSpent = OrderManager.getTotalSpent()
        val totalShoes = OrderManager.getTotalShoesPurchased()

        val formatter = DecimalFormat("#,###.00")
        binding.txtTotalAmount.text = "BDT ${formatter.format(totalSpent)}"
        binding.txtTotalShoes.text = totalShoes.toString()
    }

    private fun setupPurchasedList() {
        // Collect all items from all orders
        val allPurchasedItems = OrderManager.orders.value?.flatMap { it.items } ?: emptyList()

        if (allPurchasedItems.isNotEmpty()) {
            binding.txtPurchasesLabel.visibility = View.VISIBLE
            binding.recyclerViewPurchased.visibility = View.VISIBLE

            binding.recyclerViewPurchased.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewPurchased.adapter = PurchasedShoesAdapter(allPurchasedItems)
        } else {
            binding.txtPurchasesLabel.visibility = View.GONE
            binding.recyclerViewPurchased.visibility = View.GONE
        }
    }
}
