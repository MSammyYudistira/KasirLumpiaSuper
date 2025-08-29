package com.example.kasirlumpiasuper.ui.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val _loginState = MutableStateFlow<Result<Boolean>?>(null)
    val loginState: StateFlow<Result<Boolean>?> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        _loginState.value = null
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _loginState.value = Result.success(true)
            }
            .addOnFailureListener { e ->
                _loginState.value = Result.failure(e)
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onResult: (success: Boolean, role: String?, username: String?) -> Unit
    ) {

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Isi semua field!"
            return
        }

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
                                    isLoading = false
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