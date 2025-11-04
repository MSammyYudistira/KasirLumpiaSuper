package com.example.kasirlumpiasuper.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {


    private val _user = MutableStateFlow(
        Users(
            name = "",
            email = "",
            quote = ""
        )
    )

    val user: StateFlow<Users> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserFromAuth()
    }


    private fun loadUserFromAuth() {
        _isLoading.value = true
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let { user ->
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: user.email ?: ""
                        val quote = document.getString("quote") ?: ""
                        _user.value = Users(name = name, email = email, quote = quote)
                    }
                }
                .addOnCompleteListener {
                    _isLoading.value = false
                }
        } ?: run { _isLoading.value = false }
    }

    fun updateUser(name: String, quote: String, onResult: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _isLoading.value = true // ✅ aktifkan loading saat update

        val updates = mapOf("name" to name, "quote" to quote)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(updates)
            .addOnSuccessListener {
                _isLoading.value = false
                onResult(true)
            }
            .addOnFailureListener {
                _isLoading.value = false
                it.printStackTrace()
                onResult(false)
            }
    }

    fun refreshUser() {
        loadUserFromAuth()
    }
}