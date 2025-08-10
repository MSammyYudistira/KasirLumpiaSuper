package com.example.kasirlumpiasuper.ui

import android.R.attr.label
import android.R.attr.name
import android.R.attr.password
import android.app.ProgressDialog.show
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.User
import com.example.kasirlumpiasuper.ui.theme.Background
import com.example.kasirlumpiasuper.ui.theme.HintText
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.OnSurface
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun SignupScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = Firebase.auth
    val firestore = Firebase.firestore

    fun handleSignup(role: String) {
        // Validasi sederhana
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(context, "Isi semua field!", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(context, "Password dan Confirm Password tidak sama!", Toast.LENGTH_SHORT)
                .show()
        }

        // Buat akun
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener

                    // Siapkan data user untuk Firestore
                    val newUser = User(
                        uid = uid,
                        name = username.trim(),
                        email = email.trim(),
                        role = role,
                    )

                    // Simpan ke koleksi "users" dengan document id = uid
                    firestore.collection("users")
                        .document(uid)
                        .set(newUser)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Signup berhasil!",
                                Toast.LENGTH_SHORT
                            ).show()

                            if (role.isNotEmpty()) {
                                navController.navigate("login"){
                                    popUpTo("signup") { inclusive = true}
                                }
                            } else {
                                Toast.makeText(context, "Gagal Signup", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            auth.currentUser?.delete()
                            Toast.makeText(
                                context,
                                "Gagal menyimpan data user: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Signup gagal: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

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
                Text("Buat Akun Baru", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Daftarkan diri Anda untuk menggunakan aplikasi kasir Lumpia Super",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                CustomTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    placeholder = "Masukkan username kamu",
                    iconRes = R.drawable.baseline_person_24
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

                CustomTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Ketik ulang password kamu",
                    iconRes = R.drawable.outline_password_24,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
                    onClick = { handleSignup("kasir") }) {
                    Text("Sign Up")
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(onClick = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }) {
                    Text("Already have an account? Login")
                }
            }
        }
    }
}


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    iconRes: Int,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = HintText) },
        leadingIcon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = Primary
            )
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Primary,
            unfocusedIndicatorColor = HintText,
            cursorColor = Primary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Primary,
            unfocusedLabelColor = Color.Gray
        )
    )
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun SignupScreenPreview() {
    KasirLumpiaSuperTheme {
        SignupScreen(
            navController = rememberNavController()
        )
    }
}