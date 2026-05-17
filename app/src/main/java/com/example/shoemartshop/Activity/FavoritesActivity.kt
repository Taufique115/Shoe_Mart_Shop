package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoemartshop.Activity.Adapter.RecommendationAdapter
import com.example.shoemartshop.Activity.Repository.FavoriteManager
import com.example.shoemartshop.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: RecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeFavorites()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = RecommendationAdapter(mutableListOf())
        binding.recyclerViewFavorites.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewFavorites.adapter = adapter

        adapter.onItemClick = { item ->
            val titleLower = item.title.lowercase()
            val isNike = titleLower.contains("nike")
            val isAdidas = titleLower.contains("adidas")
            
            val thumbnails = if (isNike) {
                arrayListOf(
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_40_00_AM_5_fpcqds.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_39_59_AM_3_rhjglq.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_39_56_AM_1_wixnlo.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_39_58_AM_2_pwlkwb.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778870453/ChatGPT_Image_May_16_2026_12_40_00_AM_4_rttgrg.png"
                )
            } else if (isAdidas) {
                arrayListOf(
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778866966/ChatGPT_Image_May_15_2026_11_41_27_PM_kwwlca.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778866931/ChatGPT_Image_May_15_2026_11_41_15_PM_iqedrj.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778446652/0_5_gjb7tg.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778446652/0_4_pvsivr.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778446625/0_2_gm5lpi.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778446625/0_3_h2wca9.png"
                )
            } else if (item.id.startsWith("item_")) {
                arrayListOf(
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778873999/ChatGPT_Image_May_16_2026_01_38_52_AM_1_ie2bvf.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778873998/ChatGPT_Image_May_16_2026_01_38_53_AM_4_pwluz1.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778873998/ChatGPT_Image_May_16_2026_01_38_53_AM_2_v2vqzo.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778873998/ChatGPT_Image_May_16_2026_01_38_53_AM_3_df7ozb.png",
                    "https://res.cloudinary.com/dxafieanc/image/upload/v1778873998/ChatGPT_Image_May_16_2026_01_38_54_AM_5_bwh9n8.png"
                )
            } else {
                arrayListOf(item.picUrl)
            }
            
            val itemDescription = if (isNike) {
                "A high-performance running shoe with airy knit upper, soft impact-absorbing cushioning, and rugged outsole for everyday use"
            } else if (isAdidas) {
                "High comfort running shoe with breathable upper, soft cushioning, and durable outsole for daily performance."
            } else {
                "Built for runners: ventilated design, cushioned midsole, and rugged outsole for daily adventures."
            }

            val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                putExtra("itemId", item.id)
                putExtra("title", item.title)
                putExtra("imageUrl", item.picUrl)
                putExtra("price", item.price)
                putExtra("oldPrice", 24500.0)
                putExtra("rating", item.rating)
                putExtra("description", itemDescription)
                putStringArrayListExtra("thumbnails", thumbnails)
            }
            startActivity(intent)
        }
    }

    private fun observeFavorites() {
        FavoriteManager.favoriteItems.observe(this) { items ->
            if (items.isNullOrEmpty()) {
                binding.txtEmptyFav.visibility = View.VISIBLE
                binding.recyclerViewFavorites.visibility = View.GONE
            } else {
                binding.txtEmptyFav.visibility = View.GONE
                binding.recyclerViewFavorites.visibility = View.VISIBLE
                adapter.updateData(items.toMutableList())
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
