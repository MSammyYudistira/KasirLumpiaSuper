package com.example.kasirlumpiasuper.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import androidx.compose.runtime.getValue

@Composable
fun AuthCheckScreen(navController: NavHostController, viewModel: AuthViewModel = viewModel()) {
    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is AuthState.LoggedOut -> {
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(0)
                }
            }
            is AuthState.LoggedIn -> {
                val role = (state as AuthState.LoggedIn).role
                if (role == "admin") {
                    navController.navigate(NavRoutes.DashboardAdmin.route) {
                        popUpTo(0)
                    }
                } else {
                    navController.navigate(NavRoutes.DashboardKasir.route) {
                        popUpTo(0)
                    }
                }
            }
            AuthState.Loading -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}