package com.example.kasirlumpiasuper.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.User
import com.example.kasirlumpiasuper.ui.theme.Background
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(Surface)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.lumper_logo),
                    contentDescription = "Lumper Logo",
                    modifier = Modifier.size(121.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Selamat Datang", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Silahkan atur role kamu lalu login untuk masuk ke aplikasi kasir",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "Masukkan email kamu",
                    iconRes = R.drawable.baseline_email_24
                )


                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Masukkan password kamu",
                    iconRes = R.drawable.outline_password_24,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(
                    onClick = {
                        if (email.isNotBlank()) {
                            Firebase.auth.sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Email reset password telah dikirim ke $email",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Gagal mengirim email reset password: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                context,
                                "Mohon isi email terlebih dahulu.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lupa password?")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(onClick = {
                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = Firebase.auth.currentUser?.uid
                                if (uid != null) {
                                    Firebase.firestore.collection("users")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                val role = document.getString("role") ?: "kasir"
                                                val username = document.getString("name") ?: ""

                                                when (role) {
                                                    "kasir" -> {
                                                        Toast.makeText(
                                                            context,
                                                            "Selamat datang kasir $username!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate("home") {
                                                            popUpTo("login") { inclusive = true }
                                                        }
                                                    }

                                                    "admin" -> {
                                                        Toast.makeText(
                                                            context,
                                                            "Selamat datang Admin!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate("home") {
                                                            popUpTo("login") { inclusive = true }
                                                        }
                                                    }

                                                    else -> {
                                                        Toast.makeText(
                                                            context,
                                                            "Peran tidak dikenali",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Data user tidak ditemukan.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Gagal mengambil data user.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(context, "Login gagal.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {
                    navController.navigate("signup") {
                        popUpTo("login") { inclusive = true }
                    }
                }) {
                    Text("Don't an account? Sign up")
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun LoginScreenPreview() {
    KasirLumpiaSuperTheme {
        LoginScreen(navController = rememberNavController())
    }

}