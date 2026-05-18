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
    private var allThumbnailsList = listOf<String>()

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
        val itemId = intent.getStringExtra("itemId") ?: ""
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
        setupAdminControls(itemId, title, imageUrl, price, rating, description)
    }

    private fun setupAdminControls(
        itemId: String,
        title: String,
        imageUrl: String,
        price: Double,
        rating: Double,
        description: String
    ) {
        // Observe user session reactive role to show the edit/delete overlay!
        com.example.shoemartshop.Activity.Repository.UserManager.currentUser.observe(this) { user ->
            if (user.role == "Admin" && itemId.isNotEmpty()) {
                binding.layoutAdminProductControls.visibility = android.view.View.VISIBLE
            } else {
                binding.layoutAdminProductControls.visibility = android.view.View.GONE
            }
        }

        binding.btnEditProduct.setOnClickListener {
            val editIntent = android.content.Intent(this, ProductEditActivity::class.java).apply {
                putExtra("itemId", itemId)
                putExtra("title", title)
                putExtra("price", price)
                putExtra("picUrl", imageUrl)
                putExtra("rating", rating)
            }
            startActivity(editIntent)
            finish() // Close details so it gets reloaded cleanly when saved
        }

        binding.btnDeleteProduct.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Remove Product?")
                .setMessage("Are you absolutely sure you want to delete this product from the store catalog permanently?")
                .setPositiveButton("Remove") { _, _ ->
                    com.google.firebase.database.FirebaseDatabase.getInstance()
                        .getReference("Items").child(itemId).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product successfully deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to remove product: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
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
        // Prepend the main product image as the first thumbnail, filtering out duplicates
        val allThumbnails = mutableListOf(mainImageUrl).apply { 
            addAll(variantUrls.filter { it != mainImageUrl }) 
        }
        allThumbnailsList = allThumbnails

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
            val items = getSelectedCartItems()
            items.forEach { item ->
                CartManager.addItem(item)
            }
            Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show()
        }

        // Buy Now button
        binding.btnBuyNow.setOnClickListener {
            val items = getSelectedCartItems()
            val title = intent.getStringExtra("title") ?: "Unknown Product"
            
            // Sync current variant selections to checkout cleanly
            CartManager.syncProductVariants(title, items)
            
            val checkoutIntent = android.content.Intent(this, CheckoutActivity::class.java)
            startActivity(checkoutIntent)
        }
    }

    private fun getSelectedCartItems(): List<CartItemModel> {
        val selectedUrls = if (::thumbnailAdapter.isInitialized) {
            thumbnailAdapter.getSelectedUrls()
        } else {
            listOf(intent.getStringExtra("imageUrl") ?: "")
        }

        // Get the selected size text from the chips
        val selectedSizeText = sizeChips.getOrNull(selectedSizeIndex)?.text?.toString() ?: "40"
        val title = intent.getStringExtra("title") ?: "Unknown Product"
        val imageUrl = intent.getStringExtra("imageUrl") ?: ""
        val brand = intent.getStringExtra("brand") ?: "Whatman shoes"

        return selectedUrls.map { colorUrl ->
            val index = allThumbnailsList.indexOf(colorUrl)
            val colorName = if (index != -1) {
                if (index == 0) "Main Color" else "Variant Color #${index + 1}"
            } else {
                "Default Color"
            }

            CartItemModel(
                id = "${title}_${selectedSizeText}_${colorUrl}",
                title = title,
                brand = brand,
                price = basePrice,
                picUrl = imageUrl,
                quantity = quantity,
                selectedColorUrl = colorUrl,
                selectedSize = selectedSizeText,
                selectedColor = colorName
            )
        }
    }
}
