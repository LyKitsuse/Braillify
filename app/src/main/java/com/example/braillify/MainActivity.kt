package com.example.braillify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.braillify.ui.theme.BraillifyTheme
import com.example.braillify.screens.MainScreen

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy {
        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", false)

        setContent {
            BraillifyTheme {
                if (isFirstTime) {
                    // Onboarding
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                    // OnboardingScreen()
                } else {
                    // Main app
                    val mainUI = MainScreen()
                    mainUI.MainScreenUI()
                }
            }
        }
    }
}
