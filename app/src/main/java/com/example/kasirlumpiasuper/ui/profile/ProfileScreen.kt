package com.example.kasirlumpiasuper.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.auth.AuthViewModel
import com.example.kasirlumpiasuper.ui.navigation.NavRoutes
import com.example.kasirlumpiasuper.ui.profile.ProfilePictureWithEdit
import com.example.kasirlumpiasuper.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var editedName by remember(user) { mutableStateOf(user.name) }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }

    val hasChanges = editedName != user.name || localImageUri != null

    // Launcher pilih gambar
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            localImageUri = uri
            viewModel.updateProfileImage(uri)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(horizontal = 321.dp, vertical = 24.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                ProfilePictureWithEdit(
                    imageUrl = user.profileImageUrl,
                    localImageUri = localImageUri,
                    onEditClick = { launcher.launch("image/*") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nama
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_24),
                            contentDescription = null
                        )
                    },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_edit_square_24),
                            contentDescription = null
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

                // Logout
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = {
                            authViewModel.logoutUser()
                            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavRoutes.AuthCheck.route) {
                                popUpTo(0)
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_logout_24),
                                contentDescription = "Log out",
                                tint = Color.Red
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = "Log Out",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        }

        // Tombol Simpan
        Button(
            colors = ButtonDefaults.buttonColors(Primary),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                if (editedName.isBlank()) {
                    Toast.makeText(context, "Nama lengkap tidak boleh kosong.", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                viewModel.updateUser(name = editedName) { success ->
                    if (success) {
                        Toast.makeText(context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                localImageUri = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 321.dp)
                .padding(bottom = 24.dp),
            enabled = hasChanges && !isLoading
        ) {
            Text("Simpan Perubahan", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun ProfilePicture(
) {
    Box(
        modifier = Modifier.size(100.dp), // ukuran foto
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(R.drawable.lumper_logo),
            contentDescription = "Default Profile Picture",
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
private fun ProfilePictureWithEdit(
    imageUrl: String?,
    localImageUri: Uri?,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.BottomEnd
    ) {

        // FOTO PROFIL UTAMA
        AsyncImage(
            model = localImageUri ?: imageUrl,
            contentDescription = "Profile Picture",
            placeholder = painterResource(R.drawable.baseline_person_24_gray),
            error = painterResource(R.drawable.baseline_person_24_gray),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(BorderStroke(4.dp, Primary), CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Crop
        )

        // TOMBOL EDIT FOTO
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .size(32.dp)
                .background(Primary, CircleShape)
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_photo_camera_24),
                contentDescription = "Edit Foto",
                tint = Color.White
            )
        }
    }
}
