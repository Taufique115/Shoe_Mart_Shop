package com.example.shoemartshop.Activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Adapter.ThumbnailAdapter
import com.example.shoemartshop.Activity.Model.CartItemModel
import com.example.shoemartshop.Activity.Repository.CartManager
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ActivityProductDetailsBinding
import java.text.DecimalFormat

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var thumbnailAdapter: ThumbnailAdapter



    private var quantity = 1
    private var basePrice = 0.0
    private val formatter = DecimalFormat("#,###")

    // Size chips list for easy management
    private lateinit var sizeChips: List<TextView>
    private var selectedSizeIndex = 2 // default: size 40

    // Favorite state
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Read intent extras
        val title = intent.getStringExtra("title") ?: "Adidas Ultraboost Runner"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val price = intent.getDoubleExtra("price", 22000.0)
        val oldPrice = intent.getDoubleExtra("oldPrice", 24500.0)
        val rating = intent.getDoubleExtra("rating", 4.8)
        val description = intent.getStringExtra("description")
            ?: "High comfort running shoe with breathable upper, soft cushioning, and durable outsole for daily performance."
        val variantUrls = intent.getStringArrayListExtra("thumbnails") ?: arrayListOf()

        basePrice = price

        setupUI(title, imageUrl, price, oldPrice, rating, description)
        setupThumbnailCarousel(imageUrl, variantUrls)
        setupSizeChips()
        setupQuantityStepper()
        setupButtons()
    }

    private fun setupUI(
        title: String,
        imageUrl: String,
        price: Double,
        oldPrice: Double,
        rating: Double,
        description: String
    ) {
        binding.txtProductTitle.text = title
        binding.txtCurrentPrice.text = "৳${formatter.format(price)}"
        binding.txtOldPrice.text = "৳${formatter.format(oldPrice)}"
        // Apply strike-through programmatically (android:paintFlags is not a valid XML attr)
        binding.txtOldPrice.paintFlags =
            binding.txtOldPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        binding.txtRating.text = "${rating} Rating"
        binding.txtDescription.text = description
        binding.txtTotalPrice.text = "৳${formatter.format(price)}"
        binding.txtQuantity.text = "1"

        // Load main product image
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imgMainProduct)
    }

    private fun setupThumbnailCarousel(mainImageUrl: String, variantUrls: List<String>) {
        // Prepend the main product image as the first thumbnail
        val allThumbnails = mutableListOf(mainImageUrl).apply { addAll(variantUrls) }

        thumbnailAdapter = ThumbnailAdapter(allThumbnails) { selectedUrl, _ ->
            Glide.with(this)
                .load(selectedUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.imgMainProduct)
            updateTotalPrice()
        }

        binding.recyclerViewThumbnails.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewThumbnails.adapter = thumbnailAdapter
    }

    private fun setupSizeChips() {
        sizeChips = listOf(
            binding.size38,
            binding.size39,
            binding.size40,
            binding.size41,
            binding.size42,
            binding.size43,
            binding.size44
        )

        // Apply default selected state
        updateSizeSelection(selectedSizeIndex)

        sizeChips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                selectedSizeIndex = index
                updateSizeSelection(index)
            }
        }
    }

    private fun updateSizeSelection(selectedIndex: Int) {
        sizeChips.forEachIndexed { index, chip ->
            if (index == selectedIndex) {
                chip.setBackgroundResource(R.drawable.size_chip_selected)
                chip.setTextColor(0xFFFFFFFF.toInt())
            } else {
                chip.setBackgroundResource(R.drawable.size_chip_unselected)
                chip.setTextColor(0xFF555555.toInt())
            }
        }
    }

    private fun setupQuantityStepper() {
        binding.btnIncrease.setOnClickListener {
            quantity++
            binding.txtQuantity.text = quantity.toString()
            updateTotalPrice()
        }

        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.txtQuantity.text = quantity.toString()
                updateTotalPrice()
            }
        }
    }

    private fun updateTotalPrice() {
        val selectedVariantsCount = if (::thumbnailAdapter.isInitialized) {
            thumbnailAdapter.getSelectedCount()
        } else {
            1
        }
        val total = basePrice * quantity * selectedVariantsCount
        binding.txtTotalPrice.text = "৳${formatter.format(total)}"
    }


    private fun setupButtons() {


        // Add to Cart button
        binding.btnAddToCart.setOnClickListener {
            val item = createCartItem()
            CartManager.addItem(item)
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
        }

        // Buy Now button
        binding.btnBuyNow.setOnClickListener {
            val item = createCartItem()
            CartManager.addItem(item)
            
            val checkoutIntent = android.content.Intent(this, CheckoutActivity::class.java)
            startActivity(checkoutIntent)
        }
    }


    private fun createCartItem(): CartItemModel {
        val selectedUrl = if (::thumbnailAdapter.isInitialized) thumbnailAdapter.getSelectedUrls().firstOrNull() else null
        val title = intent.getStringExtra("title") ?: "Unknown Product"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        
        return CartItemModel(
            title = title,
            brand = "Whatman shoes", // Default
            price = basePrice,
            picUrl = imageUrl,
            quantity = quantity,
            selectedColorUrl = selectedUrl
        )
    }
}
