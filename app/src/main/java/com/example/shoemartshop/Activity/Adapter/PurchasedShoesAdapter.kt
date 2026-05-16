package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.CartItemModel
import com.example.shoemartshop.databinding.ViewholderRecommendationBinding
import java.text.DecimalFormat

class PurchasedShoesAdapter(private val items: List<CartItemModel>) :
    RecyclerView.Adapter<PurchasedShoesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderRecommendationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderRecommendationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("#,###")

        holder.binding.txtTitle.text = item.title
        holder.binding.txtPrice.text = "${formatter.format(item.price)} BDT"
        holder.binding.txtRating.text = "Purchased" 
        holder.binding.imgFav.visibility = View.GONE

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int = items.size
}
