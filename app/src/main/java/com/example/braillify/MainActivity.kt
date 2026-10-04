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

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy {
        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // isFirstTime set to true if not set to true already
        if (!sharedPreferences.contains("isFirstTime")) {
            sharedPreferences.edit().putBoolean("isFirstTime", true).apply()
        }

        // Force isFirstTime to true
//        sharedPreferences.edit().putBoolean("isFirstTime", true).apply()
        setContent {
            BraillifyTheme {
                var isFirstTime by remember {
                    mutableStateOf(sharedPreferences.getBoolean("isFirstTime", true))
                }

                // Is First Time then Onboarding Process
                if (isFirstTime) {
                    val board = Onboarding()
                    board.OnboardingProcess(
                        onFinished = {
                            setFirstTimeOff()
                            isFirstTime = false
                        }
                    )
                // Is Not First Time then Main Screen
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
