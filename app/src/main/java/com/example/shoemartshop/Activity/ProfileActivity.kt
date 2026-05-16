package com.example.shoemartshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Adapter.PurchasedShoesAdapter
import com.example.shoemartshop.Activity.Repository.OrderManager
import com.example.shoemartshop.Activity.Repository.UserManager
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ActivityProfileBinding
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
        calculateStats()
        setupPurchasedList()
    }

    private fun loadUserDetails() {
        val user = UserManager.getCurrentUser()
        binding.txtName.text = user.name
        binding.txtEmail.text = user.email
        binding.txtPhone.text = user.phone
        binding.txtLocation.text = user.location

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
            binding.txtPurchasesLabel.visibility = android.view.View.VISIBLE
            binding.recyclerViewPurchased.visibility = android.view.View.VISIBLE
            
            binding.recyclerViewPurchased.layoutManager = GridLayoutManager(this, 2)
            binding.recyclerViewPurchased.adapter = PurchasedShoesAdapter(allPurchasedItems)
        } else {
            binding.txtPurchasesLabel.visibility = android.view.View.GONE
            binding.recyclerViewPurchased.visibility = android.view.View.GONE
        }
    }
}
