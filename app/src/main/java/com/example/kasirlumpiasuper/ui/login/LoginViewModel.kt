package com.example.kasirlumpiasuper.ui.login

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class LoginViewModel: ViewModel() {

    var isLoading by mutableStateOf(false)
    private set

    var errorMessage by mutableStateOf<String?>(null)
    private set

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun loginUser(
        email: String,
        password: String,
        onResult: (success: Boolean, role: String?, username: String?) -> Unit
    ) {
        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        firestore.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val role = document.getString("role") ?: "kasir"
                                    val username = document.getString("name") ?: ""
                                    onResult(true, role, username)
                                } else {
                                    errorMessage = "Data user tidak ditemukan."
                                    onResult(false, null, null)
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                errorMessage = "Gagal mengambil data user."
                                onResult(false, null, null)
                            }
                    } else {
                        isLoading = false
                        errorMessage = "User ID tidak ditemukan."
                        onResult(false, null, null)
                    }
                } else {
                    isLoading = false
                    errorMessage = "Login gagal. Email atau password salah."
                    onResult(false, null, null)
                }
            }
    }

    fun resetPassword(
        email: String,
        onResult: (Boolean) -> Unit
    ) {
        if (email.isBlank()) {
            errorMessage = "Mohon isi email terlebih dahulu."
            onResult(false)
            return
        }
        isLoading = true
        errorMessage = null

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    isLoading = false
                    if (task.isSuccessful) {
                        onResult(true)
                    } else {
                        errorMessage = task.exception?.message ?: "Gagal mengirim email reset password."
                        onResult(false)
                }
        }
    }

}