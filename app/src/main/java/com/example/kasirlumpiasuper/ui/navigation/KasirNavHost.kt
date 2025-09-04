package com.example.kasirlumpiasuper.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.kasirlumpiasuper.data.repository.FirestoreViewModel
import com.example.kasirlumpiasuper.ui.LoadingScreen
import com.example.kasirlumpiasuper.ui.admin.AdminDashboard
import com.example.kasirlumpiasuper.ui.auth.AuthCheckScreen
import com.example.kasirlumpiasuper.ui.auth.login.LoginScreen
import com.example.kasirlumpiasuper.ui.auth.signup.SignupScreen
import com.example.kasirlumpiasuper.ui.components.CustomTopBar
import com.example.kasirlumpiasuper.ui.components.TopBarMenu
import com.example.kasirlumpiasuper.ui.history.HistoryScreen
import com.example.kasirlumpiasuper.ui.kasir.KasirDashboard
import com.example.kasirlumpiasuper.ui.profile.ProfileScreen
import com.example.kasirlumpiasuper.ui.splash.SplashScreen
import com.example.kasirlumpiasuper.ui.stats.StatisticScreen
import com.example.kasirlumpiasuper.ui.stock.StockScreen

@Composable
fun KasirNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val firestoreViewModel: FirestoreViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen {
                navController.navigate(NavRoutes.AuthCheck.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            }
        }

        composable(NavRoutes.AuthCheck.route) { AuthCheckScreen(navController) }
        composable(NavRoutes.Login.route) { LoginScreen(navController) }
        composable(NavRoutes.Signup.route) { SignupScreen(navController) }

        navigation(
            startDestination = NavRoutes.DashboardKasir.route,
            route = "main"
        ) {

            composable(NavRoutes.DashboardKasir.route) {
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        KasirDashboard(navController)
                    }
                }
            }
            composable(NavRoutes.DashboardAdmin.route) {
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        AdminDashboard(
                            navController = navController
                        )
                    }
                }
            }
            composable(NavRoutes.Profile.route) {
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        ProfileScreen(navController)
                    }
                }
            }
            composable(NavRoutes.History.route) {
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        HistoryScreen(navController)
                    }
                }
            }
            composable(NavRoutes.Statistic.route) {
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        StatisticScreen(navController)
                    }
                }
            }

            composable(NavRoutes.Stock.route) { StockScreen(navController) }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    viewModel: FirestoreViewModel,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedMenu = when (currentRoute) {
        NavRoutes.DashboardKasir.route -> TopBarMenu.DASHBOARD
        NavRoutes.History.route -> TopBarMenu.HISTORY
        NavRoutes.Statistic.route -> TopBarMenu.STATS
        NavRoutes.Profile.route -> TopBarMenu.PROFILE
        else -> TopBarMenu.DASHBOARD
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                onHomeClick = { navController.navigateSingleTopTo(NavRoutes.DashboardKasir.route) },
                onHistoryClick = { navController.navigateSingleTopTo(NavRoutes.History.route) },
                onProfileClick = { navController.navigateSingleTopTo(NavRoutes.Profile.route) },
                onStatsClick = { navController.navigateSingleTopTo(NavRoutes.Statistic.route) },
                onSelectedMenu = selectedMenu,
                viewModel = viewModel
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
