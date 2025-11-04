package com.example.kasirlumpiasuper.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.ui.dashboard.DashboardViewModel

@Composable
fun StatisticScreen(
    navController: NavHostController,
//    dashboardViewModel: DashboardViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hello Statistic Screen")
    }
}