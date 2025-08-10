package com.example.kasirlumpiasuper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.ui.HomeScreen
import com.example.kasirlumpiasuper.ui.LoginScreen
import com.example.kasirlumpiasuper.ui.SignupScreen
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.google.firebase.FirebaseApp

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
    NavHost(navController, startDestination = "login") {
        composable ("login") { LoginScreen(navController)}
        composable ("signup") { SignupScreen(navController)}
        composable ("home") { HomeScreen(navController)}

    }
}
