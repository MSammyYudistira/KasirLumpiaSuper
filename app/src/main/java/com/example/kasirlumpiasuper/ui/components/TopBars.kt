package com.example.kasirlumpiasuper.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.data.repository.FirestoreViewModel
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary

enum class TopBarMenu {
    DASHBOARD, HISTORY, STATS, PROFILE
}

@Composable
fun CustomTopBar(
    viewModel: FirestoreViewModel,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: (() -> Unit)? = null,
    onProfileClick: () -> Unit,
    onSelectedMenu: TopBarMenu
) {
    val user by viewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Surface(
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 72.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Text(
                text = "Dashboard Kasir",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,

                ) {
                TextButton(onClick = onHomeClick) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_home_24),
                        contentDescription = "Beranda",
                        tint = if (onSelectedMenu == TopBarMenu.DASHBOARD) Primary else OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Beranda",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (onSelectedMenu == TopBarMenu.DASHBOARD) Primary else OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(64.dp))

                TextButton(onClick = onHistoryClick) {
                    Icon(
                        painter = painterResource(R.drawable.outline_history_24),
                        contentDescription = "Riwayat",
                        tint = if (onSelectedMenu == TopBarMenu.HISTORY) Primary else OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Riwayat",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (onSelectedMenu == TopBarMenu.HISTORY) Primary else OnSurfaceVariant
                    )
                }

                if (onStatsClick != null) {
                    Spacer(modifier = Modifier.width(64.dp))

                    TextButton(onClick = onStatsClick) {
                        Icon(
                            painter = painterResource(R.drawable.outline_statistic_up),
                            contentDescription = "Statistik",
                            tint = if (onSelectedMenu == TopBarMenu.STATS) Primary else OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Statistik",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (onSelectedMenu == TopBarMenu.STATS) Primary else OnSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        colorFilter = ColorFilter.tint(
                            if (onSelectedMenu == TopBarMenu.PROFILE) Color.Black else OnSurfaceVariant
                        ),
                        painter = painterResource(R.drawable.baseline_person_pin_24),
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                onProfileClick()
                            }
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.name ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (onSelectedMenu == TopBarMenu.PROFILE) Color.Black else OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTopBarWithBackAction(
    onBackClick: () -> Unit,
    title: String
) {
    Surface(
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(R.drawable.outline_back_24),
                    contentDescription = "Back",
                    tint = Primary,
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTopBarWithBackActionPreview() {
    KasirLumpiaSuperTheme {
        CustomTopBarWithBackAction(
            onBackClick = { TODO() },
            title = "Beranda"
        )
    }
}