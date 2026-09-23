package com.example.braillify

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.braillify.ui.theme.BraillifyTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val isFirstTime = sharedPreferences.getBoolean("isFirstTime", false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (isFirstTime) {
            // Onboarding Procedure
            // Put your introductory logic here (e.g., show an onboarding screen)

            // Save the flag as false so this code won't run again
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
            // Proceed with normal app startup
            setContent {
                BraillifyTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                            // Text Input
                            var text by remember { mutableStateOf("") }
                            OutlinedTextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Test Braille Input Here") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)

                            )
                        }
                    }
                }
            }
        }

    }
}


