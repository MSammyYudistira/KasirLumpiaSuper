package com.example.kasirlumpiasuper.ui.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Signup : NavRoutes("signup")
    object Profile : NavRoutes("profile")
    object DashboardAdmin : NavRoutes("dashboard_admin")
    object DashboardKasir : NavRoutes("dashboard_kasir")
    object History : NavRoutes("history")
    object Statistic : NavRoutes("statistic")
    object Splash : NavRoutes("splash")
    object AuthCheck : NavRoutes("auth_check")
    object Stock : NavRoutes("stock")
    object Transaction : NavRoutes("transaction")
    object Payment : NavRoutes("payment")
    object InputRecap : NavRoutes("input_recap")
    object DetailRecap{
        const val route = "detail_recap"
    }
    object OrderDetail {
        const val route = "order_detail"
    }
}