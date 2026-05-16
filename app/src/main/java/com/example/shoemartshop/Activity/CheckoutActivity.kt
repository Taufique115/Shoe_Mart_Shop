package com.example.shoemartshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoemartshop.Activity.Adapter.CartAdapter
import com.example.shoemartshop.Activity.Repository.CartManager
import com.example.shoemartshop.databinding.ActivityCheckoutBinding
import java.text.DecimalFormat

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupBottomSection()
        observeCart()
    }


    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(mutableListOf())
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = cartAdapter

        cartAdapter.onQuantityChanged = {
            // CartManager is already updating its LiveData which will trigger the observer
        }
    }

    private fun setupBottomSection() {
        binding.btnProceed.setOnClickListener {
            if (CartManager.cartItems.value.isNullOrEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show()
            val intent = android.content.Intent(this, PaymentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeCart() {
        CartManager.cartItems.observe(this) { items ->
            cartAdapter.updateData(items)
            updateTotals()
            
            if (items.isEmpty()) {
                binding.txtEmptyCart.visibility = View.VISIBLE
                binding.recyclerViewCart.visibility = View.GONE
            } else {
                binding.txtEmptyCart.visibility = View.GONE
                binding.recyclerViewCart.visibility = View.VISIBLE
            }
        }
    }

    private fun updateTotals() {
        val subTotal = CartManager.getSubTotal()
        val deliveryFee = if (subTotal > 0) 45.0 else 0.0
        val total = subTotal + deliveryFee

        val formatter = DecimalFormat("#,###.00")
        binding.txtSubTotal.text = "৳${formatter.format(subTotal)}"
        binding.txtDeliveryFee.text = "৳${formatter.format(deliveryFee)}"
        binding.txtTotal.text = "৳${formatter.format(total)}"
    }
}
