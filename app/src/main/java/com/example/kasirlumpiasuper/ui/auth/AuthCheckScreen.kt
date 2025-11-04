package com.example.kasirlumpiasuper.ui.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.data.PreferencesManager
import com.example.kasirlumpiasuper.ui.dashboard.DashboardViewModel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AuthCheckScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AuthState.LoggedIn -> {
            val role = state.role
            Log.d("AuthDebug", "AuthCheckScreen → LoggedIn as $role")

            val context = LocalContext.current
            val prefs = remember { PreferencesManager(context) }
            val dashboardViewModel: DashboardViewModel = viewModel()

            // Gunakan key authState agar LaunchedEffect hanya trigger saat benar-benar berubah
            LaunchedEffect(authState) {
                dashboardViewModel.initializeBusinessDay(prefs)

                navController.navigate("main") {
                    popUpTo(NavRoutes.AuthCheck.route) { inclusive = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }

        is AuthState.LoggedOut -> {
            Log.d("AuthDebug", "AuthCheckScreen → LoggedOut → ke LoginScreen")

            LaunchedEffect(authState) {
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(NavRoutes.AuthCheck.route) { inclusive = true }
                }
            }
        }
    }
}
