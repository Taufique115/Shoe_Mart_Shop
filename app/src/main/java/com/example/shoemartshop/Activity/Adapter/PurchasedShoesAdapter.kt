package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.CartItemModel
import com.example.shoemartshop.databinding.ViewholderPurchasedItemBinding
import java.text.DecimalFormat

class PurchasedShoesAdapter(private val items: List<CartItemModel>) :
    RecyclerView.Adapter<PurchasedShoesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderPurchasedItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderPurchasedItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("#,###")

        // First line under image: Price
        holder.binding.txtPrice.text = "${formatter.format(item.price)} BDT"

        // Second line: Product Title
        holder.binding.txtTitle.text = item.title
        
        // Third line: Dynamic variant details (e.g. Qty: 02 | Size: 40 | Variant Color #2)
        val variantInfo = "Qty: ${if (item.quantity < 10) "0${item.quantity}" else item.quantity.toString()} | Size: ${item.selectedSize} | ${item.selectedColor}"
        holder.binding.txtVariantDetails.text = variantInfo

        // Load variant image if selected, otherwise fallback to main product image
        Glide.with(holder.itemView.context)
            .load(item.selectedColorUrl ?: item.picUrl)
            .into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int = items.size
}
