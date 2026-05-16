package com.example.shoemartshop.Activity.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shoemartshop.Activity.Model.BrandModel
import com.example.shoemartshop.Activity.Model.ItemModel
import com.example.shoemartshop.Activity.Model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    
    private val _brands = MutableLiveData<MutableList<BrandModel>>()
    val brands: LiveData<MutableList<BrandModel>> get() = _brands

    private val _banners = MutableLiveData<MutableList<SliderModel>>()
    val banners: LiveData<MutableList<SliderModel>> get() = _banners

    private val _recommended = MutableLiveData<MutableList<ItemModel>>()
    val recommended: LiveData<MutableList<ItemModel>> get() = _recommended

    fun loadBrands() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BrandModel>()
                val seenTitles = mutableSetOf<String>()
                for (childSnapshot in snapshot.children) {
                    val brand = childSnapshot.getValue(BrandModel::class.java)
                    if (brand != null && !seenTitles.contains(brand.title)) {
                        list.add(brand)
                        seenTitles.add(brand.title)
                    }
                }
                
                // Log brand titles to help you check your Firebase data in Logcat
                for (b in list) {
                    android.util.Log.d("FirebaseBrand", "Found brand in Firebase: ${b.title}")
                }
                
                val finalList = mutableListOf<BrandModel>()
                list.find { it.title.equals("Adidas", ignoreCase = true) }?.let { finalList.add(it) }
                list.find { it.title.equals("Nike", ignoreCase = true) }?.let { finalList.add(it) }
                list.find { it.title.equals("Puma", ignoreCase = true) }?.let { finalList.add(it) }
                
                // Adding Reebok and Crocodile directly using PNG URLs
                finalList.add(BrandModel("Reebok", 0, "https://res.cloudinary.com/dxafieanc/image/upload/v1778795344/reebok_logo_vsjvws.png"))
                finalList.add(BrandModel("Crocodile", 0, "https://res.cloudinary.com/dxafieanc/image/upload/v1778795344/crocodile_yro6oy.png"))

                _brands.value = finalList
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun loadBanners() {
        val list = mutableListOf<SliderModel>()

        // Banner 1 - Cloudinary (B22)
        list.add(
            SliderModel(
                url = "https://res.cloudinary.com/dxafieanc/image/upload/v1778949405/B22_eqdrqp.jpg",
                title = "",
                subTitle = "",
                buttonText = "",
                backgroundColor = "#00000000",
                textColor = "#FFFFFF",
                isImageLeft = true
            )
        )

        // Banner 2 - Cloudinary (b11)
        list.add(
            SliderModel(
                url = "https://res.cloudinary.com/dxafieanc/image/upload/v1778949414/b11_ve5rlo.jpg",
                title = "",
                subTitle = "",
                buttonText = "",
                backgroundColor = "#00000000",
                textColor = "#FFFFFF",
                isImageLeft = false
            )
        )

        _banners.value = list
    }

    fun loadRecommended() {
        val list = mutableListOf<ItemModel>()
        list.add(ItemModel("Nike Air Zoom Pegasus", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_3_dca5ny.png", 18500.0, 4.6))
        list.add(ItemModel("Adidas Ultraboost Runner", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_2_vt49pz.png", 22000.0, 4.8))
        list.add(ItemModel("Puma RS-X Reinvention", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446684/shoes_1_v4tg8v.png", 16500.0, 4.5))
        list.add(ItemModel("Reebok Nano X2", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_4_n5bcq1.png", 19200.0, 4.3))
        list.add(ItemModel("Crocodile ZigWild Shoe", "https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes3_xrnczg.png", 18500.0, 4.6))
        list.add(ItemModel("Reebok walk ultra Shoe", "https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes2_i9phml.png", 19800.0, 4.7))
        _recommended.value = list
    }
}
