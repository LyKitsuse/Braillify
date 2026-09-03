package com.example.braillify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.braillify.ui.theme.BraillifyTheme
import com.example.braillify.MockKeyboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BraillifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val sandbox = MockKeyboard()
                    sandbox.BrailleSandbox()
                }
            }
        }
    }
}


