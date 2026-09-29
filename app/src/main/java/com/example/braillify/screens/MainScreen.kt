package com.example.braillify.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.braillify.Calibrate

class MainScreen {
    @Preview(showBackground = true)
    @Composable
    fun MainScreenUI() {
        Box {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            )
            buttons()
        }


    }

    @Composable
    fun buttons(){
        // TODO: to be removed
        var text by remember { mutableStateOf("") }

        val cal = Calibrate()
        Row {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Test Braille Input Here") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.DarkGray)
                )

                // Practice Mode
                Button(onClick = {
                    Log.d("DEBUG", "Entered Practice")
                }) {
                    Text("Practice")
                }

                // Calibrate Button
                Button(onClick = {
                    Log.d("DEBUG", "Entered Calibration")
                    cal.calibrateNew()
                }) {
                    Text("Calibrate")
                }

                // Profiles
                Button(onClick = {
                    Log.d("DEBUG", "Profile Chosen")
                }) {
                    Text("Profile")
                }

                // Profiles
                Button(onClick = {
                    Log.d("DEBUG", "Voice Settings Chosen")
                }) {
                    Text("Voice Settings")
                }
            }
        }
    }

}