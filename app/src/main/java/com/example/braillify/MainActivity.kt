package com.example.braillify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.braillify.ui.theme.BraillifyTheme
import com.example.braillify.screens.MainScreen
import com.example.braillify.screens.Onboarding

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy {
        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BraillifyTheme {
                var isFirstTime by remember {
                    mutableStateOf(sharedPreferences.getBoolean("isFirstTime", true))
                }

                if (isFirstTime) {
                    val board = Onboarding()
                    board.OnboardingProcess(
                        onFinished = {
                            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                            isFirstTime = false
                        }
                    )
                } else {
                    val mainUI = MainScreen()
                    mainUI.MainScreenUI()
                }
            }
        }
    }
}
