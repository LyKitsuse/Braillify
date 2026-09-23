package com.example.braillify.screens

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.braillify.Calibrate

class MainScreen {
    @Preview(showBackground = true)
    @Composable
    fun OnboardingProcess() {
        val cal = Calibrate()
        // Calibrate Button
        Button(onClick = {
            Log.d("DEBUG", "Entered Calibration")
            cal.calibrateNew()
        }) {
            Text("Calibrate")
        }

        // Profiles
        Button(onClick = {
            println("Button clicked!")
        }) {
            Text("Profile")
        }
    }

}