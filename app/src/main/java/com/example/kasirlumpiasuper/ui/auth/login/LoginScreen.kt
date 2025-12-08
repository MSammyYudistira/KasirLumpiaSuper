package com.example.kasirlumpiasuper.ui.auth.login

import android.R.attr.onClick
import android.annotation.SuppressLint
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.auth.AuthViewModel
import com.example.kasirlumpiasuper.ui.components.CustomTextField
import com.example.kasirlumpiasuper.ui.dashboard.DashboardViewModel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.theme.Background
import com.example.kasirlumpiasuper.ui.theme.Surface

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(),
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel? = viewModel(navController.getBackStackEntry("main"))
) {

    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginViewModel.errorMessage) {
        loginViewModel.errorMessage?.let { text ->
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loginState) {
        loginState?.let { result ->
            if (result.isSuccess) {
                navController.navigate(NavRoutes.AuthCheck.route) {
                    popUpTo(0)
                }
            } else {
                loginViewModel.errorMessage
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
                        loginViewModel.resetPassword(email) { success ->
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
                        loginViewModel.loginUser(email, password, context) { success, role, username ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Selamat datang $username!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate(NavRoutes.AuthCheck.route) {
                                    popUpTo(0)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loginViewModel.isLoading,
                ) {
                    if (loginViewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Loading...")
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.outline_login_24),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Login", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {
                    navController.navigate(NavRoutes.Signup.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }) {
                    Text("Don't an account? Sign up")
                }
            }
        }
    }
}