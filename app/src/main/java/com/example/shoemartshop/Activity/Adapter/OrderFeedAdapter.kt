package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoemartshop.Activity.Repository.OrderModel
import com.example.shoemartshop.databinding.ViewholderOrderFeedBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderFeedAdapter(private val ordersList: MutableList<OrderModel>) :
    RecyclerView.Adapter<OrderFeedAdapter.OrderFeedViewHolder>() {

    class OrderFeedViewHolder(val binding: ViewholderOrderFeedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderFeedViewHolder {
        val binding = ViewholderOrderFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderFeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderFeedViewHolder, position: Int) {
        val order = ordersList[position]
        holder.binding.txtCustomerName.text = order.customerName
        holder.binding.txtCustomerEmail.text = order.customerEmail

        // Total BDT format
        val df = DecimalFormat("#,###.00")
        holder.binding.txtOrderTotal.text = "BDT ${df.format(order.totalAmount)}"

        // Date format
        val sdf = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        holder.binding.txtOrderDate.text = sdf.format(Date(order.timestamp))

        // Items Summary format
        val itemsSummary = order.items.joinToString(", ") { "${it.quantity}x ${it.title}" }
        holder.binding.txtPurchasedItemsSummary.text = if (itemsSummary.isNotEmpty()) itemsSummary else "No items recorded"
    }

    override fun getItemCount(): Int = ordersList.size

    fun updateData(newOrders: List<OrderModel>) {
        ordersList.clear()
        ordersList.addAll(newOrders)
        notifyDataSetChanged()
    }
}
