package com.example.shoemartshop.Activity.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoemartshop.Activity.Model.SliderModel
import com.example.shoemartshop.R

class SliderAdapter(
    private var sliderItems: List<SliderModel>
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bannerCard: CardView = itemView.findViewById(R.id.bannerCard)
        private val bannerBg: ConstraintLayout = itemView.findViewById(R.id.bannerBg)
        
        // Left Layout Views
        private val layoutLeft: ConstraintLayout = itemView.findViewById(R.id.layoutLeft)
        private val imageLeft: ImageView = itemView.findViewById(R.id.imageSlideLeft)
        private val titleLeft: TextView = itemView.findViewById(R.id.titleTextLeft)
        private val subTitleLeft: TextView = itemView.findViewById(R.id.subTitleTextLeft)
        private val buttonLeft: AppCompatButton = itemView.findViewById(R.id.buttonBannerLeft)

        // Right Layout Views
        private val layoutRight: ConstraintLayout = itemView.findViewById(R.id.layoutRight)
        private val imageRight: ImageView = itemView.findViewById(R.id.imageSlideRight)
        private val titleRight: TextView = itemView.findViewById(R.id.titleTextRight)
        private val subTitleRight: TextView = itemView.findViewById(R.id.subTitleTextRight)
        private val buttonRight: AppCompatButton = itemView.findViewById(R.id.buttonBannerRight)

        fun bind(sliderModel: SliderModel) {
            // Set Background Color on both CardView and inner layout
            try {
                val bgColor = Color.parseColor(sliderModel.backgroundColor)
                bannerCard.setCardBackgroundColor(bgColor)
                bannerBg.setBackgroundColor(bgColor)
            } catch (e: Exception) {
                val defaultColor = Color.parseColor("#1B2236")
                bannerCard.setCardBackgroundColor(defaultColor)
                bannerBg.setBackgroundColor(defaultColor)
            }

            if (sliderModel.isImageLeft) {
                layoutLeft.visibility = View.VISIBLE
                layoutRight.visibility = View.GONE
                
                Glide.with(itemView.context).load(sliderModel.url).into(imageLeft)
                titleLeft.text = sliderModel.title
                subTitleLeft.text = sliderModel.subTitle
                buttonLeft.text = sliderModel.buttonText
                
                try {
                    val color = Color.parseColor(sliderModel.textColor)
                    titleLeft.setTextColor(color)
                    subTitleLeft.setTextColor(color)
                } catch (e: Exception) {}
            } else {
                layoutLeft.visibility = View.GONE
                layoutRight.visibility = View.VISIBLE
                
                Glide.with(itemView.context).load(sliderModel.url).into(imageRight)
                titleRight.text = sliderModel.title
                subTitleRight.text = sliderModel.subTitle
                buttonRight.text = sliderModel.buttonText

                try {
                    val color = Color.parseColor(sliderModel.textColor)
                    titleRight.setTextColor(color)
                    subTitleRight.setTextColor(color)
                } catch (e: Exception) {}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slider_item_container,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
    }

    override fun getItemCount(): Int = sliderItems.size
}
