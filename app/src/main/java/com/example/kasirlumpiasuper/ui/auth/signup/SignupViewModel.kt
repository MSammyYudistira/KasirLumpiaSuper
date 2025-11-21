package com.example.kasirlumpiasuper.ui.auth.signup

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasirlumpiasuper.domain.model.Users
import com.example.kasirlumpiasuper.data.firestore.FirestoreRepository
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var verificationSent by mutableStateOf(false)
        private set

    var signupSuccess by mutableStateOf(false)
        private set

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore

    fun signupUser(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: String
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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                val userData = Users(
                    uid = uid,
                    name = username,
                    email = email,
                    role = role,
                    createdAt = Timestamp.now()
                )

                firestore.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        sendVerificationEmail(email)
                    }
                    .addOnFailureListener {
                        errorMessage = "Gagal menyimpan user."
                        isLoading = false
                    }
            }
            .addOnFailureListener {
                errorMessage = it.message
                isLoading = false
            }
    }

    private fun sendVerificationEmail(email: String) {
        auth.currentUser?.sendEmailVerification()
            ?.addOnSuccessListener {
                verificationSent = true
                errorMessage = "Email verifikasi dikirim ke $email"

                startEmailVerificationChecker()
            }
            ?.addOnFailureListener {
                errorMessage = "Gagal mengirim email verifikasi."
                isLoading = false
            }
    }

    private fun startEmailVerificationChecker() {
        viewModelScope.launch {
            repeat(30) {  // maksimal 30 x cek (±60 detik)
                delay(2000)

                auth.currentUser?.reload()
                val verified = auth.currentUser?.isEmailVerified ?: false

                if (verified) {
                    signupSuccess = true
                    isLoading = false
                    return@launch
                }
            }

            // jika expired
            isLoading = false
            errorMessage = "Verifikasi email belum dilakukan."
        }
    }
}
