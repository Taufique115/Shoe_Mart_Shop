package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.R
import com.example.shoemartshop.databinding.ViewholderThumbnailBinding

class ThumbnailAdapter(
    private val thumbnails: List<String>,
    private val onThumbnailClick: (String, Int) -> Unit
) : RecyclerView.Adapter<ThumbnailAdapter.ViewHolder>() {

    // Multi-select: set of selected indices (first item selected by default)
    private val selectedIndices = mutableSetOf<Int>(0)

    class ViewHolder(val binding: ViewholderThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewholderThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = thumbnails[position]

        Glide.with(holder.itemView.context)
            .load(url)
            .into(holder.binding.imgThumbnail)

        // Highlight ALL selected thumbnails
        if (selectedIndices.contains(position)) {
            holder.binding.thumbnailContainer.setBackgroundResource(R.drawable.selected_thumbnail_border)
        } else {
            holder.binding.thumbnailContainer.setBackgroundResource(R.drawable.unselected_thumbnail_border)
        }

        holder.binding.thumbnailContainer.setOnClickListener {
            val clickedIndex = holder.bindingAdapterPosition
            if (clickedIndex == RecyclerView.NO_POSITION) return@setOnClickListener

            // Toggle selection
            if (selectedIndices.contains(clickedIndex)) {
                // Deselect only if more than one is selected (keep at least 1)
                if (selectedIndices.size > 1) {
                    selectedIndices.remove(clickedIndex)
                    notifyItemChanged(clickedIndex)
                }
            } else {
                selectedIndices.add(clickedIndex)
                notifyItemChanged(clickedIndex)
            }

            // Always update main image preview to the tapped thumbnail
            onThumbnailClick(url, clickedIndex)
        }
    }

    override fun getItemCount(): Int = thumbnails.size

    /** Returns how many variants are currently selected */
    fun getSelectedCount(): Int = selectedIndices.size

    /** Returns the URLs of all currently selected variants */
    fun getSelectedUrls(): List<String> = selectedIndices.sorted().map { thumbnails[it] }
}
