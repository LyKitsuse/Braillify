package com.example.braillify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.example.braillify.screens.MainScreen
import com.example.braillify.ui.theme.BraillifyTheme

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy { getSharedPreferences("AppPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", false)
        if (isFirstTime) {
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
            setContent {
                BraillifyTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                            // Onboarding Screen
                        }
                    }
                }
            }
        } else {
            setContent {
                BraillifyTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                            var text by remember { mutableStateOf("") }
                            val focusRequester = remember { FocusRequester() }

                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Test Braille Input Here") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .focusRequester(focusRequester)
                            )

                            val mainUI = MainScreen()
                            mainUI.MainScreenUI(onRequestKeyboard = {
                                focusRequester.requestFocus()
                            })
                        }
                    }
                }
            }
        }
    }
}
