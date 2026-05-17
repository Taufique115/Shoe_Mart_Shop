package com.example.shoemartshop.Activity.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shoemartshop.Activity.Repository.UserModel
import com.example.shoemartshop.databinding.ViewholderUserCardBinding

class UserAdapter(private val usersList: MutableList<UserModel>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    var onToggleRoleClick: ((UserModel) -> Unit)? = null
    var onDeleteClick: ((UserModel) -> Unit)? = null

    class UserViewHolder(val binding: ViewholderUserCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ViewholderUserCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersList[position]
        holder.binding.txtUserName.text = user.fullName.ifEmpty { "Anonymous User" }
        holder.binding.txtUserEmail.text = user.email
        holder.binding.txtUserPhone.text = user.phone.ifEmpty { "Not Provided" }
        holder.binding.txtUserLocation.text = user.location.ifEmpty { "Not Provided" }

        // Role Badging
        holder.binding.txtUserRole.text = user.role
        if (user.role == "Admin") {
            holder.binding.txtUserRole.setBackgroundColor(0xFFFF1744.toInt()) // Hot Pink
        } else {
            holder.binding.txtUserRole.setBackgroundColor(0xFF888888.toInt()) // Gray
        }

        // Click Actions
        holder.binding.btnToggleRole.setOnClickListener {
            onToggleRoleClick?.invoke(user)
        }

        holder.binding.btnDeleteUser.setOnClickListener {
            onDeleteClick?.invoke(user)
        }
    }

    override fun getItemCount(): Int = usersList.size

    fun updateData(newUsers: List<UserModel>) {
        usersList.clear()
        usersList.addAll(newUsers)
        notifyDataSetChanged()
    }
}
