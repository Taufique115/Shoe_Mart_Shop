package com.example.shoemartshop.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Repository.CartManager
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ActivityPaymentBinding
import java.text.DecimalFormat

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var isBkashSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadImages()
        calculateTotal()
        setupListeners()
    }

    private fun setupUI() {
        // Initialize state (Card selected by default)
        selectCardMethod()
    }

    private fun loadImages() {
        val bkashUrl = "https://res.cloudinary.com/dxafieanc/image/upload/v1778877626/bkash_apxhc5.png"
        val cardUrl = "https://res.cloudinary.com/dxafieanc/image/upload/v1778877661/Mastercard-Logo_vhzzbx.png"

        Glide.with(this).load(bkashUrl).into(binding.imgBkashLogo)
        Glide.with(this).load(cardUrl).into(binding.imgCardLogo)
        Glide.with(this).load(cardUrl).into(binding.imgCardLogoSmall)
    }

    private fun calculateTotal() {
        val subTotal = CartManager.getSubTotal()
        val deliveryFee = if (subTotal > 0) 45.0 else 0.0
        val total = subTotal + deliveryFee

        val formatter = DecimalFormat("#,###.00")
        binding.btnPayAction.text = "Pay ৳${formatter.format(total)}"
        binding.txtBkashAmount.text = "৳${formatter.format(total)}"
    }

    private fun setupListeners() {

        binding.btnMethodCard.setOnClickListener {
            selectCardMethod()
        }

        binding.btnMethodBkash.setOnClickListener {
            selectBkashMethod()
        }

        binding.etCardNumber.addTextChangedListener(object : TextWatcher {
            var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting || s == null) return
                isFormatting = true
                val raw = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in raw.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(raw[i])
                }
                binding.etCardNumber.setText(formatted.toString())
                binding.etCardNumber.setSelection(formatted.length)
                isFormatting = false
            }
        })

        binding.btnPayAction.setOnClickListener {
            if (CartManager.cartItems.value.isNullOrEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var isValid = true

            if (isBkashSelected) {
                val bkashNumber = binding.etBkashNumber.text.toString()
                if (bkashNumber.length != 11 || !bkashNumber.startsWith("01")) {
                    isValid = false
                }
            } else {
                val cardNumber = binding.etCardNumber.text.toString().replace(" ", "")
                val cardName = binding.etCardName.text.toString()
                val cvv = binding.etCvv.text.toString()

                if (cardNumber.length != 16 || cardName.isBlank() || cvv.length != 3) {
                    isValid = false
                }
            }

            if (!isValid) {
                binding.txtErrorSubtitle.visibility = View.VISIBLE
                return@setOnClickListener
            } else {
                binding.txtErrorSubtitle.visibility = View.GONE
            }

            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
            CartManager.clearCart()
            
            // Go back to Dashboard and clear backstack
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun selectCardMethod() {
        isBkashSelected = false
        binding.txtErrorSubtitle.visibility = View.GONE
        
        // Update Card UI
        binding.btnMethodCard.setBackgroundResource(R.drawable.payment_method_selected_bg)
        binding.imgCardSelected.setImageResource(R.drawable.checked_icon_pink)
        
        // Update bKash UI
        binding.btnMethodBkash.setBackgroundResource(R.drawable.payment_method_unselected_bg)
        binding.imgBkashSelected.setImageResource(R.drawable.unchecked_circle)
        
        // Hide bKash forms, show card details
        binding.infoBanner.visibility = View.GONE
        binding.bkashDetailsForm.visibility = View.GONE
        binding.cardDetailsForm.visibility = View.VISIBLE
        
        // Reset Bottom button and footer
        val subTotal = CartManager.getSubTotal()
        val deliveryFee = if (subTotal > 0) 45.0 else 0.0
        val total = subTotal + deliveryFee
        val formatter = DecimalFormat("#,###.00")
        
        binding.btnPayAction.setBackgroundResource(R.drawable.black_rounded_button)
        binding.btnPayAction.text = "Pay ৳${formatter.format(total)}"
        binding.btnPayAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.txtFooterSecurity.text = "Your payment information is safe and secure."
    }

    private fun selectBkashMethod() {
        isBkashSelected = true
        binding.txtErrorSubtitle.visibility = View.GONE
        
        // Update Card UI
        binding.btnMethodCard.setBackgroundResource(R.drawable.payment_method_unselected_bg)
        binding.imgCardSelected.setImageResource(R.drawable.unchecked_circle)
        
        // Update bKash UI
        binding.btnMethodBkash.setBackgroundResource(R.drawable.payment_method_bkash_selected_bg)
        binding.imgBkashSelected.setImageResource(R.drawable.checked_icon_pink)
        
        // Show bKash forms, hide card details
        binding.infoBanner.visibility = View.VISIBLE
        binding.bkashDetailsForm.visibility = View.VISIBLE
        binding.cardDetailsForm.visibility = View.GONE
        
        // Update Bottom button and footer for bKash
        binding.btnPayAction.setBackgroundResource(R.drawable.pink_rounded_button)
        binding.btnPayAction.text = "Pay Now"
        binding.btnPayAction.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        binding.txtFooterSecurity.text = "You will be redirected to bKash to complete payment securely."
    }
}
