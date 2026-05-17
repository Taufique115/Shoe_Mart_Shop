package com.example.shoemartshop.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoemartshop.Activity.Adapter.UserAdapter
import com.example.shoemartshop.Activity.Repository.UserModel
import com.example.shoemartshop.databinding.ActivityAdminUserBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminUserBinding
    private val db = FirebaseFirestore.getInstance()
    private val fullUsersList = mutableListOf<UserModel>()
    private val filteredUsersList = mutableListOf<UserModel>()
    private val userAdapter = UserAdapter(filteredUsersList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupRecyclerView()
        loadUsers()
        setupSearch()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        userAdapter.onToggleRoleClick = { user ->
            toggleUserRole(user)
        }

        userAdapter.onDeleteClick = { user ->
            confirmDeleteUser(user)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter
    }

    private fun loadUsers() {
        binding.progressBarUsers.visibility = View.VISIBLE
        
        // Listen in real time to the Firestore users collection!
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                binding.progressBarUsers.visibility = View.GONE
                
                if (error != null) {
                    binding.txtNoUsersHint.visibility = View.VISIBLE
                    binding.txtNoUsersHint.text = "Error: ${error.message}"
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    fullUsersList.clear()
                    for (doc in snapshot) {
                        val user = doc.toObject(UserModel::class.java)
                        fullUsersList.add(user)
                    }
                    filterUsers(binding.editTextUserSearch.text.toString())
                }
            }
    }

    private fun setupSearch() {
        binding.editTextUserSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterUsers(query: String) {
        val cleanQuery = query.trim().lowercase()
        if (cleanQuery.isEmpty()) {
            filteredUsersList.clear()
            filteredUsersList.addAll(fullUsersList)
        } else {
            val list = fullUsersList.filter {
                it.fullName.lowercase().contains(cleanQuery) || it.email.lowercase().contains(cleanQuery)
            }
            filteredUsersList.clear()
            filteredUsersList.addAll(list)
        }

        userAdapter.updateData(filteredUsersList)

        if (filteredUsersList.isEmpty()) {
            binding.txtNoUsersHint.visibility = View.VISIBLE
        } else {
            binding.txtNoUsersHint.visibility = View.GONE
        }
    }

    private fun toggleUserRole(user: UserModel) {
        val newRole = if (user.role == "Admin") "Customer" else "Admin"
        
        db.collection("users").document(user.uid)
            .update("role", newRole)
            .addOnSuccessListener {
                Toast.makeText(this, "Success: Elevate/demoted ${user.fullName} to $newRole!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update role: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmDeleteUser(user: UserModel) {
        AlertDialog.Builder(this)
            .setTitle("Delete Customer Profile?")
            .setMessage("Are you absolutely sure you want to delete ${user.fullName}'s account profile from database? This action is permanent.")
            .setPositiveButton("Delete") { _, _ ->
                db.collection("users").document(user.uid)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account profile successfully deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error deleting account: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
