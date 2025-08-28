package com.example.kasirlumpiasuper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kasirlumpiasuper.ui.navigation.KasirNavHost
import com.example.kasirlumpiasuper.ui.theme.KasirLumpiaSuperTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            KasirLumpiaSuperTheme {
                KasirNavHost()
            }
        }
    }
}
