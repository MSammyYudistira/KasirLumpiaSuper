package com.example.kasirlumpiasuper

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.example.kasirlumpiasuper.ui.auth.AuthViewModel
import com.example.kasirlumpiasuper.ui.auth.AuthViewModelFactory
import com.example.kasirlumpiasuper.ui.navigation.KasirNavHost
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory(applicationContext)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        requestBluetoothPermissions()

        setContent {
            KasirLumpiaSuperTheme {
                val navController = rememberNavController()
                KasirNavHost(navController, authViewModel)
            }
        }
    }

    private val bluetoothPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { it.value == true }
            if (!granted) {
                Toast.makeText(
                    this,
                    "Izin Bluetooth wajib diberikan agar bisa mencetak struk",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        }
    }
}
