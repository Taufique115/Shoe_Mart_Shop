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
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ItemModel>()
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    val value = childSnapshot.value
                    
                    // Self-healing: If a corrupt child (non-map or standalone primitive) is under Items, delete it
                    if (key == "price" || value is Long || value is Double || value is String || value is Boolean) {
                        childSnapshot.ref.removeValue()
                        continue
                    }

                    try {
                        val item = childSnapshot.getValue(ItemModel::class.java)
                        if (item != null) {
                            // If item's id property is missing or empty, assign the Firebase database key
                            if (item.id.isEmpty() && key != null) {
                                item.id = key
                            }
                            
                            // Dynamically update low/placeholder prices to realistic premium prices (19,000 - 22,000 BDT)
                            if (item.price < 5000.0 && item.id.isNotEmpty()) {
                                val newPrice = when {
                                    item.title.contains("adidas", ignoreCase = true) -> 19500.0
                                    item.title.contains("nike", ignoreCase = true) -> 21200.0
                                    item.title.contains("puma", ignoreCase = true) -> 20800.0
                                    item.title.contains("skechers", ignoreCase = true) -> 19900.0
                                    else -> 19000.0 + (item.id.hashCode() % 3001).coerceAtLeast(0)
                                }
                                item.price = newPrice
                                // Safely persist under correct child path
                                ref.child(item.id).child("price").setValue(newPrice)
                            }
                            list.add(item)
                        }
                    } catch (e: Exception) {
                        // Log and skip if deserialization of a specific child fails, protecting the app from crash
                        android.util.Log.e("MainRepository", "Skipping invalid item snapshot: ${e.message}")
                    }
                }

                // 1. Prepare our premium default items
                val defaultItems = listOf(
                    ItemModel("item_1", "Nike Air Zoom Pegasus", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_3_dca5ny.png", 18500.0, 4.6),
                    ItemModel("item_2", "Adidas Ultraboost Runner", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_2_vt49pz.png", 22000.0, 4.8),
                    ItemModel("item_3", "Puma RS-X Reinvention", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446684/shoes_1_v4tg8v.png", 16500.0, 4.5),
                    ItemModel("item_4", "Reebok Nano X2", "https://res.cloudinary.com/dxafieanc/image/upload/v1778446685/shoes_4_n5bcq1.png", 19200.0, 4.3),
                    ItemModel("item_5", "Crocodile ZigWild Shoe", "https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes3_xrnczg.png", 18500.0, 4.6),
                    ItemModel("item_6", "Reebok walk ultra Shoe", "https://res.cloudinary.com/dxafieanc/image/upload/v1778792054/shoes2_i9phml.png", 19800.0, 4.7)
                )

                // 2. Perform safe local merge so that default items are NEVER missing in the UI
                val mergedList = list.toMutableList()
                val existingIds = list.map { it.id }.toSet()

                for (item in defaultItems) {
                    if (!existingIds.contains(item.id)) {
                        mergedList.add(item)
                    }
                }

                // 3. Post the merged catalog directly to the active live data observer
                _recommended.value = mergedList

                // 4. Try to write missing default items to Firebase in the background (if user is authenticated)
                if (list.isEmpty()) {
                    for (item in defaultItems) {
                        ref.child(item.id).setValue(item)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
