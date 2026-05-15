package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.ItemModel
import com.example.shoemartshop.databinding.ViewholderRecommendationBinding
import java.text.DecimalFormat

class RecommendationAdapter(private var items: MutableList<ItemModel>) :
    RecyclerView.Adapter<RecommendationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ViewholderRecommendationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderRecommendationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    var onFavoriteClick: ((ItemModel) -> Unit)? = null
    var onItemClick: ((ItemModel) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.txtTitle.text = item.title
        val formatter = DecimalFormat("#,###")
        holder.binding.txtPrice.text = "${formatter.format(item.price)} BDT"
        holder.binding.txtRating.text = item.rating.toString()

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.imgProduct)

        val isFav = com.example.shoemartshop.Activity.Repository.FavoriteManager.isFavorite(item)
        item.isFavorite = isFav
        updateFavoriteIcon(holder, isFav)

        holder.binding.imgFav.setOnClickListener {
            com.example.shoemartshop.Activity.Repository.FavoriteManager.toggleFavorite(item)
            updateFavoriteIcon(holder, item.isFavorite)
            onFavoriteClick?.invoke(item)
        }

        // All product cards are clickable for product details
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(item)
        }
    }

    private fun updateFavoriteIcon(holder: ViewHolder, isFavorite: Boolean) {
        if (isFavorite) {
            holder.binding.imgFav.setColorFilter(android.graphics.Color.parseColor("#9e0404"))
        } else {
            holder.binding.imgFav.setColorFilter(android.graphics.Color.parseColor("#919191"))
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: MutableList<ItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
