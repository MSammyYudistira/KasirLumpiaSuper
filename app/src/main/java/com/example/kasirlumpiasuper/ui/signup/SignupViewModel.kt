package com.example.kasirlumpiasuper.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.kasirlumpiasuper.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignupViewModel: ViewModel() {

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

        if (password != confirmPassword) {
            errorMessage = "Password dan Confirm Password tidak sama!"
            return
        }

        isLoading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener

                    val newUser = User(
                        uid = uid,
                        name = username.trim(),
                        email = email.trim(),
                        role = role,
                    )

                    firestore.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            isLoading = false
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            auth.currentUser?.delete()
                            errorMessage = "Gagal menyimpan data user: ${e.message}"
                        }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message ?: "Signup gagal"
                }
            }
    }
}