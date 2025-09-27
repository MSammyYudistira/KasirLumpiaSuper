package com.example.kasirlumpiasuper

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kasirlumpiasuper.ui.navigation.KasirNavHost
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.google.firebase.FirebaseApp
import java.util.jar.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        requestBluetoothPermissions()

        setContent {
            KasirLumpiaSuperTheme {
                KasirNavHost()
            }
        }
    }

    private val bluetoothPremissionLauncher =
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
            bluetoothPremissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        }
    }
}
