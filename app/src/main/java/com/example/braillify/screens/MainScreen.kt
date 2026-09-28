package com.example.braillify.screens

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.braillify.Calibrate

class MainScreen {
    @Preview(showBackground = true)
    @Composable
    fun MainScreenUI(onRequestKeyboard: (() -> Unit)? = null) {
        val context = LocalContext.current
        val cal = Calibrate()

        // Calibrate Button
        Button(onClick = {
            Log.d("DEBUG", "Entered Practice")
        }) {
            Text("Practice")
        }

        Button(onClick = {
            Log.d("DEBUG", "Entered Calibration")
            cal.calibrateNew()
            onRequestKeyboard?.invoke()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }) {
            Text("Calibrate")
        }

        // Profiles
        Button(onClick = {
            Log.d("DEBUG", "Profile Chosen")
        }) {
            Text("Profile")
        }

        // Voice Settings
        Button(onClick = {
            Log.d("DEBUG", "Voice Settings Chosen")
        }) {
            Text("Voice Settings")
        }
    }
}
