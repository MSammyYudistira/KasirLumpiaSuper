package com.example.kasirlumpiasuper.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.signup.CustomTextField
import com.example.kasirlumpiasuper.ui.theme.Background
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { text ->
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

//    if (viewModel.isLoading) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Transparent),
//            contentAlignment = Alignment.Center,
//        ) {
//            CircularProgressIndicator(
//                color = Color.Green,
//                strokeWidth = 2.dp,
//            )
//        }
//    } else {
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
                    "Silahkan login untuk masuk ke aplikasi kasir lumpia super",
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
                        viewModel.resetPassword(email) { success ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Email reset password telah dikirim ke $email",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Lupa password?")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        viewModel.loginUser(email, password) { success, role, username ->
                            if (success) {
                                if (role == "kasir") {
                                    Toast.makeText(
                                        context,
                                        "Selamat datang kasir $username!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else if (role == "admin") {
                                    Toast.makeText(
                                        context,
                                        "Selamat datang Admin!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !viewModel.isLoading,
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Loading...")
                    } else {
                        Icon(painter = painterResource(R.drawable.outline_login_24), contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Login", style = MaterialTheme.typography.titleSmall)
                    }
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