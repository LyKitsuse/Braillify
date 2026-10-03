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

        // Ensure isFirstTime is initialized to true if not present
        if (!sharedPreferences.contains("isFirstTime")) {
            sharedPreferences.edit().putBoolean("isFirstTime", true).apply()
        }

        setContent {
            BraillifyTheme {
                var isFirstTime by remember {
                    mutableStateOf(sharedPreferences.getBoolean("isFirstTime", true))
                }

                if (isFirstTime) {
                    val board = Onboarding()
                    board.OnboardingProcess(
                        onFinished = {
                            setFirstTimeOff()
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

    /**
     * Saves the state of isFirstTime to false (turns off first time onboarding).
     */
    fun setFirstTimeOff() {
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
    }
}
