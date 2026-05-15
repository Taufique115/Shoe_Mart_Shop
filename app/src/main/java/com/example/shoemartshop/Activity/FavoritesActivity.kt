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
            // Open product details (similar to Dashboard)
            val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                putExtra("title", item.title)
                putExtra("imageUrl", item.picUrl)
                putExtra("price", item.price)
                putExtra("rating", item.rating)
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
