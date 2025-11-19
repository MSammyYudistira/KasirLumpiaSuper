package com.example.kasirlumpiasuper.ui.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Signup : NavRoutes("signup")
    object Profile : NavRoutes("profile")
    object History : NavRoutes("history")
    object Statistic : NavRoutes("statistic")
    object Splash : NavRoutes("splash")
    object AuthCheck : NavRoutes("auth_check")
    object Stock : NavRoutes("stock")
    object Transaction : NavRoutes("transaction")
    object Payment : NavRoutes("payment")
    object InputRecap : NavRoutes("input_recap")
    object DetailRecap : NavRoutes("detail_recap")
    object OrderDetail : NavRoutes("order_detail")
    object Dashboard : NavRoutes("dashboard")

    object MenuManagement : NavRoutes("menu_management")

    object MenuDetail : NavRoutes("menu_detail/{productId}") {
        fun build(productId: String) = "menu_detail/$productId"
    }

}