package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.CartItemModel
import com.example.shoemartshop.Activity.Repository.CartManager
import com.example.shoemartshop.databinding.ViewholderCartBinding
import java.text.DecimalFormat

class CartAdapter(private var items: MutableList<CartItemModel>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    var onQuantityChanged: (() -> Unit)? = null

    class ViewHolder(val binding: ViewholderCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.txtTitle.text = item.title
        val brandText = if (item.selectedSize.isNotEmpty()) {
            "${item.brand} | Size: ${item.selectedSize}"
        } else {
            item.brand
        }
        holder.binding.txtBrand.text = brandText
        
        val formatter = DecimalFormat("#,###.00")
        holder.binding.txtPrice.text = "৳${formatter.format(item.price)}"
        
        holder.binding.txtQuantity.text = if (item.quantity < 10) "0${item.quantity}" else item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.selectedColorUrl ?: item.picUrl)
            .into(holder.binding.imgProduct)

        holder.binding.btnIncrease.setOnClickListener {
            CartManager.updateQuantity(item, item.quantity + 1)
            onQuantityChanged?.invoke()
        }

        holder.binding.btnDecrease.setOnClickListener {
            CartManager.updateQuantity(item, item.quantity - 1)
            onQuantityChanged?.invoke()
        }
        
        holder.binding.btnDelete.visibility = View.VISIBLE
        holder.binding.btnDelete.setOnClickListener {
            CartManager.removeItem(item)
            onQuantityChanged?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CartItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
