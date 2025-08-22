package com.example.kasirlumpiasuper.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.example.kasirlumpiasuper.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        CustomProfileTopBar(
            onBackClick = {
                navController.popBackStack()
            }
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(horizontal = 321.dp, vertical = 32.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                ProfilePictureWithEdit(
                    image = painterResource(R.drawable.lumper_logo),
                    onEditClick = { }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Nama
                OutlinedTextField(
                    value = user.name,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24),
                            contentDescription = null
                        )
                    },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.outline_edit_square_24),
                                contentDescription = null
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Email
                OutlinedTextField(
                    value = user.email,
                    onValueChange = {},
                    label = { Text("Email") },
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_email_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Quote
                OutlinedTextField(
                    value = user.quote,
                    onValueChange = { newValue ->
                        viewModel.updateQuote(newValue)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_format_quote_24),
                            contentDescription = null
                        )
                    },
                    label = { Text("Quote") },
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                painter = painterResource(R.drawable.outline_edit_square_24),
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            colors = ButtonDefaults.buttonColors(Primary),
            shape = RoundedCornerShape(8.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 321.dp)
        ) {
            Text("Simpan Perubahan", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            colors = ButtonDefaults.buttonColors(Color.Red),
            shape = RoundedCornerShape(8.dp),
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 321.dp)
        ) {
            Text("Log Out", style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun ProfilePictureWithEdit(
    image: Painter,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(100.dp), // ukuran foto
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = image,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp)
                .border(
                    BorderStroke(4.dp, color = Primary),
                    CircleShape
                ),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .size(24.dp)
                .background(Primary, shape = RoundedCornerShape(72.dp))
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_photo_camera_24),
                contentDescription = "Edit Foto",
                tint = Color.White,
                modifier = Modifier
                    .background(Primary)
            )
        }
    }
}

@Composable
fun CustomProfileTopBar(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 72.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = painterResource(R.drawable.outline_back_24),
                    contentDescription = "back",
                    tint = Primary
                )
            }
            Text(
                text = "Ubah Profil",
                style = MaterialTheme.typography.titleMedium,
                color = Primary,
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.TABLET)
@Composable
private fun ProfileTopBarPreview() {
    KasirLumpiaSuperTheme {
        CustomProfileTopBar(onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePictureWithEditPreview() {
    KasirLumpiaSuperTheme {
        ProfilePictureWithEdit(
            image = painterResource(R.drawable.lumper_logo),
            onEditClick = {}
        )
    }
}