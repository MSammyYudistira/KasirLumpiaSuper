package com.example.kasirlumpiasuper.ui.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.kasirlumpiasuper.data.PreferencesManager
import com.example.kasirlumpiasuper.data.repository.FirestoreViewModel
import com.example.kasirlumpiasuper.ui.auth.AuthCheckScreen
import com.example.kasirlumpiasuper.ui.auth.AuthViewModel
import com.example.kasirlumpiasuper.ui.auth.AuthViewModelFactory
import com.example.kasirlumpiasuper.ui.auth.login.LoginScreen
import com.example.kasirlumpiasuper.ui.auth.signup.SignupScreen
import com.example.kasirlumpiasuper.ui.components.CustomTopBar
import com.example.kasirlumpiasuper.ui.components.TopBarMenu
import com.example.kasirlumpiasuper.ui.dashboard.DashboardScreen
import com.example.kasirlumpiasuper.ui.dashboard.DashboardViewModel
import com.example.kasirlumpiasuper.ui.history.HistoryScreen
import com.example.kasirlumpiasuper.ui.history.HistoryViewModel
import com.example.kasirlumpiasuper.ui.history.OrderDetailScreen
import com.example.kasirlumpiasuper.ui.menu.MenuDetailScreen
import com.example.kasirlumpiasuper.ui.menu.MenuManagementScreen
import com.example.kasirlumpiasuper.ui.menu.MenuViewModel
import com.example.kasirlumpiasuper.ui.payment.PaymentScreen
import com.example.kasirlumpiasuper.ui.payment.PaymentViewModel
import com.example.kasirlumpiasuper.ui.profile.ProfileScreen
import com.example.kasirlumpiasuper.ui.recap.DetailRecapScreen
import com.example.kasirlumpiasuper.ui.recap.InputRecapScreen
import com.example.kasirlumpiasuper.ui.recap.RecapViewModel
import com.example.kasirlumpiasuper.ui.splash.SplashScreen
import com.example.kasirlumpiasuper.ui.stats.StatisticScreen
import com.example.kasirlumpiasuper.ui.stock.StockScreen
import com.example.kasirlumpiasuper.ui.transaction.TransactionScreen
import com.example.kasirlumpiasuper.ui.transaction.TransactionViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnrememberedGetBackStackEntry", "StateFlowValueCalledInComposition")
@Composable
fun KasirNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
//    val navController = rememberNavController()

    // ViewModel global untuk Firestore user (bisa diakses dari mana saja)
    val firestoreViewModel: FirestoreViewModel = viewModel()

    // ViewModel yang akan menampung role admin/kasir (di-scope ke "main")
    // Dibuat di luar NavHost supaya Compose tidak re-create tiap navigasi
//    val dashboardViewModel: DashboardViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {

        // === SPLASH / AUTH SECTION ===
        composable(NavRoutes.Splash.route) {
            SplashScreen {
                navController.navigate(NavRoutes.AuthCheck.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            }
        }

        composable(NavRoutes.AuthCheck.route) { AuthCheckScreen(navController, authViewModel) }
        composable(NavRoutes.Login.route) {
            LoginScreen(
                navController = navController,
                dashboardViewModel = null,
                authViewModel = authViewModel
            )
        }
        composable(NavRoutes.Signup.route) { SignupScreen(navController) }

        // === MAIN SECTION (setelah login) ===
        navigation(
            startDestination = NavRoutes.Dashboard.route,
            route = "main"
        ) {

            composable(NavRoutes.Dashboard.route) { backStackEntry ->
                // ⭐ Ambil parent entry dari "main" NAV GRAPH
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("main")
                }

                // ⭐ DashboardViewModel shared untuk semua screen
                val dashboardViewModel: DashboardViewModel = viewModel(parentEntry)

                val context = LocalContext.current
                val prefs = remember { PreferencesManager(context) }
                MainScaffold(
                    navController = navController,
                    viewModel = firestoreViewModel
                ) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        DashboardScreen(
                            navController = navController,
                            viewModel = dashboardViewModel,
                            prefs = prefs
                        )
                    }
                }
            }

            composable(NavRoutes.Profile.route) {
                MainScaffold(navController = navController, viewModel = firestoreViewModel) {
                    Box(Modifier.padding(it)) {
                        ProfileScreen(
                            navController,
                            authViewModel = authViewModel
                        )
                    }
                }
            }

            composable(NavRoutes.History.route) {
                MainScaffold(navController = navController, viewModel = firestoreViewModel) {
                    Box(Modifier.padding(it)) { HistoryScreen(navController) }
                }
            }

            composable(NavRoutes.Statistic.route) {
                MainScaffold(navController = navController, viewModel = firestoreViewModel) {
                    Box(Modifier.padding(it)) { StatisticScreen(navController) }
                }
            }

            composable(NavRoutes.Stock.route) { backStackEntry ->
                val recapViewModel: RecapViewModel = viewModel(backStackEntry)

                    // ⭐ Ambil parent entry dari "main" NAV GRAPH
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("main")
                    }

                    // ⭐ DashboardViewModel shared untuk semua screen
                    val dashboardViewModel: DashboardViewModel = viewModel(parentEntry)

                    StockScreen(
                    navController,
                    recapViewModel = recapViewModel,
                    dashboardViewModel = dashboardViewModel
                )
            }

            composable(
                route = "${NavRoutes.InputRecap.route}/{dateLabel}"
            ) { backStackEntry ->
                val recapViewModel: RecapViewModel = viewModel()
                val dateLabel = backStackEntry.arguments?.getString("dateLabel") ?: ""
                InputRecapScreen(navController, recapViewModel, dateLabel)
            }

            composable(
                route = "${NavRoutes.DetailRecap.route}/{dateLabel}"
            ) { backStackEntry ->
                val recapViewModel: RecapViewModel = viewModel()
                val dateLabel = backStackEntry.arguments?.getString("dateLabel") ?: ""
                DetailRecapScreen(navController, recapViewModel, dateLabel)
            }

            composable(
                route = "${NavRoutes.OrderDetail.route}/{dateKey}/{queueNumber}"
            ) { backStackEntry ->
                val dateKey = backStackEntry.arguments?.getString("dateKey") ?: return@composable
                val queueNumber = backStackEntry.arguments?.getString("queueNumber")?.toIntOrNull()
                    ?: return@composable

                // Pastikan aman dari crash jika "main" tidak ada
                val parentEntry = remember(navController) {
                    try {
                        navController.getBackStackEntry("main")
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }

                val historyViewModel: HistoryViewModel =
                    if (parentEntry != null) viewModel(parentEntry)
                    else viewModel()

                // 👉 Ambil role dari FirestoreViewModel root milik KasirNavHost
                val user by firestoreViewModel.user.collectAsState()
                val isAdmin = user?.role == "admin"

                OrderDetailScreen(
                    navController = navController,
                    dateKey = dateKey,
                    queueNumber = queueNumber,
                    viewModel = historyViewModel,
                    isAdmin = isAdmin
                )
            }

            composable(
                route = "transaction?dateKey={dateKey}&queueNumber={queueNumber}",
                arguments = listOf(
                    navArgument("dateKey") {
                        nullable = true
                        defaultValue = null
                    },
                    navArgument("queueNumber") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val dateKey = backStackEntry.arguments?.getString("dateKey")
                val queueArg = backStackEntry.arguments?.getInt("queueNumber")
                val queueNumber = if (queueArg == -1) null else queueArg

                val transactionViewModel: TransactionViewModel = viewModel(backStackEntry)

                TransactionScreen(
                    navController = navController,
                    transactionViewModel = transactionViewModel,
                    dateKey = dateKey,
                    queueNumber = queueNumber
                )
            }

            composable(NavRoutes.Payment.route) { backStackEntry ->
                val transactionViewModel: TransactionViewModel =
                    viewModel(navController.getBackStackEntry(NavRoutes.Transaction.route))
                val paymentViewModel: PaymentViewModel = viewModel(backStackEntry)
                PaymentScreen(navController, paymentViewModel, transactionViewModel)
            }

            // Kelola Menu (Admin Only)
            composable(NavRoutes.MenuManagement.route) { backStackEntry ->
                val menuViewModel: MenuViewModel = viewModel(backStackEntry)
                MenuManagementScreen(
                    navController = navController,
                    viewModel = menuViewModel
                )
            }

            composable(NavRoutes.MenuDetail.route) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: "new"
                MenuDetailScreen(navController, productId)
            }
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

    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    val selectedMenu = when (currentRoute) {
        NavRoutes.Dashboard.route -> TopBarMenu.DASHBOARD
        NavRoutes.History.route -> TopBarMenu.HISTORY
        NavRoutes.Statistic.route -> TopBarMenu.STATS
        NavRoutes.Profile.route -> TopBarMenu.PROFILE
        else -> TopBarMenu.DASHBOARD
    }

    val userRole = user?.role == "admin"

    Scaffold(
        topBar = {
            CustomTopBar(
                onHomeClick = { navController.navigateSingleTopTo(NavRoutes.Dashboard.route) },
                onHistoryClick = { navController.navigateSingleTopTo(NavRoutes.History.route) },
                onProfileClick = { navController.navigateSingleTopTo(NavRoutes.Profile.route) },
                onStatsClick = if (userRole) {
                    { navController.navigateSingleTopTo(NavRoutes.Statistic.route) }
                } else null,
                onSelectedMenu = selectedMenu,
                users = user,
                title = if (userRole) "Admin Dashboard" else "Kasir Dashboard"
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}
