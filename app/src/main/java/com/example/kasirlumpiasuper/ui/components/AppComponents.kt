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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.HintText
import com.example.kasirlumpiasuper.ui.theme.OnSurfaceVariant
import com.example.kasirlumpiasuper.ui.theme.Primary

enum class TopBarMenu {
    DASHBOARD, HISTORY, STATS, PROFILE
}

@Composable
fun CustomTopBar(
    userName: String,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: (() -> Unit)? = null,
    onProfileClick: () -> Unit,
    onSelectedMenu: TopBarMenu
) {
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
                text = "Halaman Beranda",
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
                        text = userName,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (onSelectedMenu == TopBarMenu.PROFILE) Color.Black else OnSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    iconRes: Int,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = HintText) },
        leadingIcon = {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = Primary
            )
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Primary,
            unfocusedIndicatorColor = HintText,
            cursorColor = Primary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedLabelColor = Primary,
            unfocusedLabelColor = Color.Gray
        )
    )
}