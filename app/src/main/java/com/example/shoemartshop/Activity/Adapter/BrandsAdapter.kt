package com.example.shoemartshop.Activity.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.BrandModel
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ViewholderBrandBinding

class BrandsAdapter(private val items: MutableList<BrandModel>) : RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    fun updateData(newData: List<BrandModel>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ViewholderBrandBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    var onClick: ((BrandModel) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.binding.pic)

        holder.binding.root.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                lastSelectedPosition = selectedPosition
                if (selectedPosition == currentPosition) {
                    // Unselect to show All products
                    selectedPosition = -1
                    notifyItemChanged(lastSelectedPosition)
                    onClick?.invoke(BrandModel(title = "All"))
                } else {
                    selectedPosition = currentPosition
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onClick?.invoke(item)
                }
            }
        }

        // Update item background and image tint based on selection
        val isSelected = selectedPosition == position
        holder.binding.pic.setBackgroundResource(
            if (isSelected) 0 else R.drawable.grey_full_corner
        )

        ImageViewCompat.setImageTintList(
            holder.binding.pic,
            ColorStateList.valueOf(
                holder.itemView.context.getColor(
                    if (isSelected) R.color.white else R.color.black
                )
            )
        )
    }

    override fun getItemCount(): Int = items.size
}
