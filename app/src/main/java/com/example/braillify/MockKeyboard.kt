package com.example.braillify

import androidx.compose.runtime.mutableStateOf

class MockKeyboard {
    var tapLocation by remember { mutableStateOf("Tap Anywhere!") }
}