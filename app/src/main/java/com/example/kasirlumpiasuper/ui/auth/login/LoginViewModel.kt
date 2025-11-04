package com.example.kasirlumpiasuper.ui.auth.login

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.example.kasirlumpiasuper.ui.utils.DataStoreKeys
import com.example.kasirlumpiasuper.ui.utils.datastore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val _loginState = MutableStateFlow<Result<Boolean>?>(null)
    val loginState: StateFlow<Result<Boolean>?> = _loginState.asStateFlow()

    fun loginUser(
        email: String,
        password: String,
        context: Context,
        onResult: (success: Boolean, role: String?, username: String?) -> Unit
    ) {

        // 1️⃣ Validasi dasar
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email dan password tidak boleh kosong"
            return
        }

        // 2️⃣ Validasi format email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Format email tidak valid"
            return
        }

        // 3️⃣ Validasi panjang password
        if (password.length < 6) {
            errorMessage = "Password minimal 6 karakter"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        firestore.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role") ?: "kasir"
                                val username = document.getString("name") ?: ""

                                // simpan UID ke DataStore
                                CoroutineScope(Dispatchers.IO).launch {
                                    context.datastore.edit { prefs ->
                                        prefs[DataStoreKeys.User_UID] = uid
                                        prefs[DataStoreKeys.User_ROLE] = role          // ⬅️ simpan role
                                        prefs[DataStoreKeys.User_NAME] = username      // ⬅️ opsional
                                    }
                                }

                                onResult(true, role, username)
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Gagal mengambil data user: ${e.message}"
                                isLoading = false
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