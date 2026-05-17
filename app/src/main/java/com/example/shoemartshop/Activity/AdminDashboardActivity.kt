package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoemartshop.Activity.Adapter.OrderFeedAdapter
import com.example.shoemartshop.Activity.Repository.OrderModel
import com.example.shoemartshop.databinding.ActivityAdminDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DecimalFormat

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding
    private val db = FirebaseFirestore.getInstance()
    private val ordersFeedList = mutableListOf<OrderModel>()
    private val feedAdapter = OrderFeedAdapter(ordersFeedList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupRecyclerView()
        loadAnalyticsData()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnManageUsers.setOnClickListener {
            val intent = Intent(this, AdminUserActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOrdersFeed.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOrdersFeed.adapter = feedAdapter
    }

    private fun loadAnalyticsData() {
        binding.progressBarOrders.visibility = View.VISIBLE

        // 1. Fetch all users count
        db.collection("users").get()
            .addOnSuccessListener { usersSnap ->
                val usersCount = usersSnap.size()
                binding.txtTotalUsersCount.text = "$usersCount Users"
            }

        // 2. Fetch all orders and listen for updates in real time!
        db.collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                binding.progressBarOrders.visibility = View.GONE
                
                if (error != null) {
                    binding.txtNoOrdersHint.visibility = View.VISIBLE
                    binding.txtNoOrdersHint.text = "Error loading orders: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    binding.txtNoOrdersHint.visibility = View.GONE
                    
                    val list = mutableListOf<OrderModel>()
                    var totalRevenue = 0.0
                    
                    for (doc in snapshot) {
                        val order = doc.toObject(OrderModel::class.java)
                        list.add(order)
                        totalRevenue += order.totalAmount
                    }
                    
                    val salesCount = list.size
                    val df = DecimalFormat("#,###.00")
                    
                    // Update main metrics
                    binding.txtTotalRevenue.text = "BDT ${df.format(totalRevenue)}"
                    binding.txtTotalSalesCount.text = "$salesCount Orders"
                    
                    val avgValue = if (salesCount > 0) totalRevenue / salesCount else 0.0
                    binding.txtAvgOrderValue.text = "BDT ${df.format(avgValue)}"
                    
                    // Update recyclerview
                    feedAdapter.updateData(list)
                } else {
                    binding.txtNoOrdersHint.visibility = View.VISIBLE
                    binding.txtTotalRevenue.text = "BDT 0.00"
                    binding.txtTotalSalesCount.text = "0 Orders"
                    binding.txtAvgOrderValue.text = "BDT 0.00"
                    feedAdapter.updateData(emptyList())
                }
            }
    }
}
