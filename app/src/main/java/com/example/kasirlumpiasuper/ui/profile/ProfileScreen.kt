package com.example.kasirlumpiasuper.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()

    // state input sementara (biar bisa bandingin dengan data asli)
    var editedName by remember(user) { mutableStateOf(user.name) }
    var editedQuote by remember(user) { mutableStateOf(user.quote) }

    // cek apakah ada perubahan dari data asli
    val hasChanges = editedName != user.name || editedQuote != user.quote

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }


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
                ProfilePicture()

                Spacer(modifier = Modifier.height(32.dp))

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
                    value = editedQuote,
                    onValueChange = { editedQuote = it },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_format_quote_24),
                            contentDescription = null
                        )
                    },
                    label = { Text("Quote") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_edit_square_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Tombol Simpan
        Button(
            colors = ButtonDefaults.buttonColors(Primary),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                val updatedUser = user.copy(
                    name = editedName,
                    quote = editedQuote
                )
                viewModel.updateUser(updatedUser) { success ->
                    if (success) {
                        Toast.makeText(context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 321.dp),
            enabled = hasChanges
        ) {
            Text("Simpan Perubahan", style = MaterialTheme.typography.titleSmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            colors = ButtonDefaults.buttonColors(Color.Red),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 321.dp)
        ) {
            Text("Log Out", style = MaterialTheme.typography.titleSmall)
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
        Icon(
            painter = painterResource(R.drawable.baseline_person_24),
            contentDescription = "Default Profile Picture",
            tint = Color.Gray,
            modifier = Modifier.size(100.dp)
        )
    }
}

//@Preview(showBackground = true, device = Devices.TABLET)
//@Composable
//private fun ProfileTopBarPreview() {
//    KasirLumpiaSuperTheme {
//        ProfileScreen()
//    }
//}

//@Preview(showBackground = true)
//@Composable
//private fun ProfilePictureWithEditPreview() {
//    KasirLumpiaSuperTheme {
//        ProfilePictureWithEdit(
//            image = painterResource(R.drawable.lumper_logo),
//            onEditClick = {}
//        )
//    }
//}