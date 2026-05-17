package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.shoemartshop.Activity.Adapter.BrandsAdapter
import com.example.shoemartshop.Activity.Adapter.RecommendationAdapter
import com.example.shoemartshop.Activity.Adapter.SliderAdapter
import com.example.shoemartshop.Activity.Model.ItemModel
import com.example.shoemartshop.Activity.ViewModel.MainViewModel
import com.example.shoemartshop.Activity.Repository.UserManager
import com.example.shoemartshop.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityDashboardBinding
    private val brandsAdapter = BrandsAdapter(mutableListOf())
    private val recommendationAdapter = RecommendationAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBrands()
        initBanners()
        initRecommended()
        initSearch()
        observeCart()
        observeFavorites()
        setupBottomNav()
        setupAdminFab()
    }

    private fun setupAdminFab() {
        UserManager.currentUser.observe(this) { user ->
            if (user.role == "Admin") {
                binding.fabAddProduct.visibility = View.VISIBLE
            } else {
                binding.fabAddProduct.visibility = View.GONE
            }
        }

        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, ProductEditActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initSearch() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase()
                val allRecommended = viewModel.recommended.value ?: emptyList()
                
                if (query.isEmpty()) {
                    recommendationAdapter.updateData(allRecommended.toMutableList())
                } else {
                    val filteredList = allRecommended.filter {
                        it.title.lowercase().contains(query)
                    }
                    recommendationAdapter.updateData(filteredList.toMutableList())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupBottomNav() {
        binding.homeBtn.setOnClickListener {
            Toast.makeText(this, "Refreshing shop content...", Toast.LENGTH_SHORT).show()
            
            // Show loading indicators
            binding.progressBarBanner.visibility = View.VISIBLE
            binding.progressBarCategory.visibility = View.VISIBLE
            binding.progressBarRecommendation.visibility = View.VISIBLE
            
            // Reload all dashboard content dynamically
            viewModel.loadBanners()
            viewModel.loadBrands()
            viewModel.loadRecommended()
        }

        binding.cartBtn.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
        binding.favBtn.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun observeCart() {
        com.example.shoemartshop.Activity.Repository.CartManager.cartItems.observe(this) { items ->
            val totalCount = items.sumOf { it.quantity }
            if (totalCount > 0) {
                binding.txtCartBadge.visibility = View.VISIBLE
                binding.txtCartBadge.text = totalCount.toString()
            } else {
                binding.txtCartBadge.visibility = View.GONE
            }
        }
    }

    private fun observeFavorites() {
        com.example.shoemartshop.Activity.Repository.FavoriteManager.favoriteItems.observe(this) { items ->
            val favCount = items.size
            if (favCount > 0) {
                binding.txtFavBadge.visibility = View.VISIBLE
                binding.txtFavBadge.text = favCount.toString()
            } else {
                binding.txtFavBadge.visibility = View.GONE
            }
        }
    }

    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this) { banners ->
            val adapter = SliderAdapter(banners)
            binding.viewpagerSlider.adapter = adapter
            binding.viewpagerSlider.clipToPadding = false
            binding.viewpagerSlider.clipChildren = false
            binding.viewpagerSlider.offscreenPageLimit = 3
            binding.viewpagerSlider.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

            val compositePageTransformer = CompositePageTransformer().apply {
                addTransformer(MarginPageTransformer(40))
            }
            binding.viewpagerSlider.setPageTransformer(compositePageTransformer)
            
            if (banners.size > 1) {
                binding.dotIndicator.visibility = View.VISIBLE
                binding.dotIndicator.attachTo(binding.viewpagerSlider)
            } else {
                binding.dotIndicator.visibility = View.GONE
            }
            
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanners()
    }

    private fun initBrands() {
        binding.recyclerViewCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategory.adapter = brandsAdapter

        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.brands.observe(this) { data ->
            brandsAdapter.updateData(data)
            binding.progressBarCategory.visibility = View.GONE
        }

        brandsAdapter.onClick = { brand ->
            val brandName = brand.title.lowercase()
            val allRecommended = viewModel.recommended.value ?: emptyList()
            
            if (brandName == "all") {
                recommendationAdapter.updateData(allRecommended.toMutableList())
            } else {
                val filteredList = allRecommended.filter {
                    it.title.lowercase().contains(brandName)
                }
                recommendationAdapter.updateData(filteredList.toMutableList())
            }
        }

        viewModel.loadBrands()
    }

    private fun initRecommended() {
        binding.recyclerViewRecommendation.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewRecommendation.adapter = recommendationAdapter

        binding.progressBarRecommendation.visibility = View.VISIBLE

        viewModel.recommended.observe(this) { data ->
            recommendationAdapter.updateData(data)
            binding.progressBarRecommendation.visibility = View.GONE
        }
        
        recommendationAdapter.onFavoriteClick = {
            // No need to manually update badge, observer handles it
        }

        // Launch ProductDetailsActivity only when Adidas or Nike card is tapped
        recommendationAdapter.onItemClick = { item ->
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

        viewModel.loadRecommended()
    }


}
