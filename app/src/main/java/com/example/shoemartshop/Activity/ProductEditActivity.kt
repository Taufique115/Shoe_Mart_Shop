package com.example.shoemartshop.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.ItemModel
import com.example.shoemartshop.databinding.ActivityProductEditBinding
import com.google.firebase.database.FirebaseDatabase

class ProductEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductEditBinding
    private val database = FirebaseDatabase.getInstance()
    private var itemId: String? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIntentMode()
        setupListeners()
        setupRealtimePreview()
    }

    private fun checkIntentMode() {
        itemId = intent.getStringExtra("itemId")
        if (!itemId.isNullOrEmpty()) {
            isEditMode = true
            binding.txtHeaderTitle.text = "EDIT CATALOG ITEM"
            binding.btnSaveProduct.text = "Save Product Changes"
            binding.btnDeleteProduct.visibility = View.VISIBLE
            
            // Pre-fill form fields
            binding.etProductTitle.setText(intent.getStringExtra("title") ?: "")
            binding.etProductPrice.setText(intent.getDoubleExtra("price", 0.0).toString())
            binding.etProductPicUrl.setText(intent.getStringExtra("picUrl") ?: "")
            binding.etProductRating.setText(intent.getDoubleExtra("rating", 0.0).toString())
            
            // Trigger preview image loading
            loadPreviewImage(intent.getStringExtra("picUrl") ?: "")
        } else {
            isEditMode = false
            binding.txtHeaderTitle.text = "ADD CATALOG ITEM"
            binding.btnSaveProduct.text = "Publish to Store"
            binding.btnDeleteProduct.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
        }

        binding.btnDeleteProduct.setOnClickListener {
            confirmDeleteProduct()
        }
    }

    private fun setupRealtimePreview() {
        binding.etProductPicUrl.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadPreviewImage(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etProductPicUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.etProductPicUrl.text.toString().trim()
                if (input.isNotEmpty()) {
                    binding.etProductPicUrl.setText(formatImageUrl(input))
                }
            }
        }
    }

    private fun formatImageUrl(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        // If it already has http:// or https://, return it as-is
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed
        }

        // If it starts with res.cloudinary.com, prepend https://
        if (trimmed.startsWith("res.cloudinary.com")) {
            return "https://$trimmed"
        }

        // If it looks like a simple Cloudinary public ID/filename (no slashes, no dots, or ends with png/jpg)
        val isSimpleId = !trimmed.contains("/") && (!trimmed.contains(".") || trimmed.endsWith(".png") || trimmed.endsWith(".jpg") || trimmed.endsWith(".webp"))
        if (isSimpleId && trimmed.length < 60) {
            val baseId = trimmed.removeSuffix(".png").removeSuffix(".jpg").removeSuffix(".webp")
            return "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/$baseId.png"
        }

        // Otherwise, fallback: prepend https://
        return "https://$trimmed"
    }

    private fun loadPreviewImage(url: String) {
        val formattedUrl = formatImageUrl(url)
        if (formattedUrl.isNotEmpty()) {
            Glide.with(this)
                .load(formattedUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(binding.imgProductPreview)
            
            // Clear tint if image loads successfully
            binding.imgProductPreview.imageTintList = null
        } else {
            binding.imgProductPreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun saveProduct() {
        val title = binding.etProductTitle.text.toString().trim()
        val priceStr = binding.etProductPrice.text.toString().trim()
        val picUrlRaw = binding.etProductPicUrl.text.toString().trim()
        val ratingStr = binding.etProductRating.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
            return
        }

        if (picUrlRaw.isEmpty()) {
            Toast.makeText(this, "Pic URL cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val picUrl = formatImageUrl(picUrlRaw)
        // Visually update the text field with the full formatted URL
        binding.etProductPicUrl.setText(picUrl)

        val rating = ratingStr.toDoubleOrNull()
        if (rating == null || rating < 1.0 || rating > 5.0) {
            Toast.makeText(this, "Please enter a rating between 1.0 and 5.0", Toast.LENGTH_SHORT).show()
            return
        }

        val ref = database.getReference("Items")
        val finalId = if (isEditMode) itemId!! else ref.push().key ?: System.currentTimeMillis().toString()

        val product = ItemModel(
            id = finalId,
            title = title,
            picUrl = picUrl,
            price = price,
            rating = rating,
            isFavorite = false
        )

        ref.child(finalId).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product successfully saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmDeleteProduct() {
        if (!isEditMode || itemId == null) return

        AlertDialog.Builder(this)
            .setTitle("Remove Product?")
            .setMessage("Are you absolutely sure you want to delete this product from the store catalog permanently?")
            .setPositiveButton("Remove") { _, _ ->
                database.getReference("Items").child(itemId!!).removeValue()
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
