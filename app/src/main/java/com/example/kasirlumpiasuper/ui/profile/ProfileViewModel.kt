package com.example.kasirlumpiasuper.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.utils.StorageHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {


    private val _user = MutableStateFlow(
        Users(
            name = "",
            email = ""
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
                    if (document.exists()) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: user.email ?: ""
                        val role = document.getString("role") ?: "kasir"
                        val profileImage = document.getString("profileImageUrl") ?: ""

                        _user.value = Users(
                            uid = user.uid,
                            name = name,
                            email = email,
                            role = role,
                            profileImageUrl = profileImage
                        )
                    }
                }
                .addOnCompleteListener {
                    _isLoading.value = false
                }
        } ?: run { _isLoading.value = false }
    }

    fun updateUser(name: String, onResult: (Boolean) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _isLoading.value = true // ✅ aktifkan loading saat update

        val updates = mapOf("name" to name)

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

    suspend fun uploadProfileImage(uri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val url = StorageHelper.uploadProfileImage(uid, uri)

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("profileImageUrl", url)
            .await()

        // refresh user
        loadUserFromAuth()
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                uploadProfileImage(uri)
            } catch (e: Exception) {
                Log.e("ProfileVM", "Gagal update foto profil: ${e.message}")
            }
        }
    }
}