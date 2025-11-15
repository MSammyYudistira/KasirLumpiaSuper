package com.example.kasirlumpiasuper.ui.navigation

sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("login")
    object Signup : NavRoutes("signup")
    object Profile : NavRoutes("profile")
//    object DashboardAdmin : NavRoutes("dashboard_admin")
//    object DashboardKasir : NavRoutes("dashboard_kasir")
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

    object MidtransWebView {
        const val route = "midtrans_webview/{encodedUrl}/{orderId}"
        fun build(encodedUrl: String, orderId: String) = "midtrans_webview/$encodedUrl/$orderId"
    }

    object MenuManagement : NavRoutes("menu_management")

    object MenuDetail : NavRoutes("menu_detail/{productId}") {
        fun build(productId: String) = "menu_detail/$productId"
    }

    object QrisScreen {
        const val route = "qris_screen/{orderId}"
        fun build(orderId: String) = "qris_screen/$orderId"
    }

}