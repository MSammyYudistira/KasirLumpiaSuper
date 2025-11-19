package com.example.kasirlumpiasuper.ui.auth.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kasirlumpiasuper.data.model.Users
import com.example.kasirlumpiasuper.data.repository.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignupViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun signupUser(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        onSuccess: () -> Unit
    ) {
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Isi semua field!"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Format email tidak valid!"
            return
        }

        if (password.length < 6) {
            errorMessage = "Password minimal 6 karakter!"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Password dan Konfirmasi Password tidak sama!"
            return
        }

        isLoading = true
        errorMessage = null

        // 🔹 Buat akun di Firebase Auth
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        isLoading = false
                        errorMessage = "Gagal mengambil UID pengguna."
                        return@addOnCompleteListener
                    }

                    val newUser = Users(
                        uid = uid,
                        name = username.trim(),
                        email = email.trim(),
                        role = role,
                        createdAt = Timestamp.now()
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            isLoading = false
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            auth.currentUser?.delete()
                            isLoading = false
                            errorMessage = "Gagal menyimpan data user: ${e.message}"
                            Log.e("SignupError", "Firestore set failed: ${e.message}", e)
                        }
                } else {
                    isLoading = false
                    errorMessage = "Pendaftaran gagal: ${task.exception?.message ?: "Terjadi kesalahan tak diketahui"}"
                    Log.e("SignupError", "Auth failed: ${task.exception?.message}", task.exception)
                }
            }
    }
}
