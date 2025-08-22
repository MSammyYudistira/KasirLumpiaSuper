package com.example.kasirlumpiasuper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.ui.home.HomeScreen
import com.example.kasirlumpiasuper.ui.login.LoginScreen
import com.example.kasirlumpiasuper.ui.profile.ProfileScreen
import com.example.kasirlumpiasuper.ui.signup.SignupScreen
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            KasirLumpiaSuperTheme {
                AuthScreen()
            }
        }
    }
}

@Composable
fun AuthScreen() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
            "home"
        } else {
            "login"
        }
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("profile") { ProfileScreen(navController) }

    }
}
