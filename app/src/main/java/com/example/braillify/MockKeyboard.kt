package com.example.braillify

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.ui.platform.LocalConfiguration
import com.example.braillify.BrailleDictionary


class MockKeyboard {
    @Preview(showBackground = true)
    @Composable
    fun BrailleSandbox() {
        val configuration = LocalConfiguration.current
        val brailleDict = BrailleDictionary
        var tapLocation by remember { mutableStateOf("Tap Anywhere!") }

        // One list that holds all your taps (each tap automatically has X and Y)
        // This saves the Input stuff (Also find a way to store into data)
        val taps = remember { mutableStateListOf<Offset>() }

        // When taps reaches 20, loop through and print every X and Y
//        LaunchedEffect(taps.size) {
//            if (taps.size == 20) {
//                for (i in taps.indices) {
//                    val currentTap = taps[i]
//
//                    Log.d("MyTag", "Tap $i - X: ${currentTap.x}, Y: ${currentTap.y}")
//                }
//            }
//        }

        LaunchedEffect(Unit) {
            // Synthetic Data (2x2 per group)
            val pointsLeft = BrailleDictionary
            val pL = pointsLeft.pointsL

            val orientation = configuration.orientation
            if(orientation ==  Configuration.ORIENTATION_LANDSCAPE){
                for (point in pL){
                    kotlinx.coroutines.delay(500)
                    taps.add(point)
                    tapLocation = "Simulated Tap - X: ${point.x}, Y: ${point.y}"
                }
            } else {
                // Portrait Coords
//                for (point in pointsP){
//                    kotlinx.coroutines.delay(500)
//                    taps.add(point)
//                    tapLocation = "Simulated Tap - X: ${point.x}, Y: ${point.y}"
//                }
            }

        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        taps.add(offset)
                        tapLocation = "X: ${offset.x}, Y: ${offset.y}"
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw a purple circle for every tap
                for (tap in taps) {
                    drawCircle(
                        color = Color(0xFF6200EE),
                        radius = 10f,
                        center = tap
                    )
                }
            }

            Text(
                text = tapLocation,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
    fun groupPoints(){
        // Group raw points

    }


    // This is for Running the Models (k-NN, SVM, Random Forest)
    fun runModel(){
        // Run Model and use Reference Data Points with Actual Data Points
        // Return Braille Equivalent
    }
}