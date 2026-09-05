package com.example.braillify

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import com.example.braillify.machineLearningModels.kNearestNeighbor

class MockKeyboard {

    var isCapital: Boolean = false
    var isCapitalOnce: Boolean = false
    var isNumeral: Boolean = false

    @Preview(showBackground = true)
    @Composable
    fun BrailleSandbox() {
        val configuration = LocalConfiguration.current
        val points = BrailleDictionary
        var tapLocation by remember { mutableStateOf("Tap Anywhere!") }

        // One list that holds all your taps (each tap automatically has X and Y)
        // This saves the Input stuff (Also find a way to store into data)
        val taps = remember { mutableStateListOf<Offset>() }
        var tapEq: String by remember { mutableStateOf("Tap Anywhere!") }
        // When taps reaches 20, loop through and print every X and Y
        LaunchedEffect(Unit) {
            // Synthetic Data (2x2 per group)
            val pL = points.pointsL2D

            val orientation = configuration.orientation
            if(orientation ==  Configuration.ORIENTATION_LANDSCAPE){
                for (dotGroup in pL) {
                    for (point in dotGroup) {
                        kotlinx.coroutines.delay(100)
                        taps.add(point)
                        tapLocation = "Simulated Tap - X: ${point.x}, Y: ${point.y}"
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        // Wait for the first finger to touch the screen
                        val downEvent = awaitFirstDown()
                        val dict = BrailleDictionary

                        // Give a tiny fraction of a second (100ms) for all other fingers in the chord to land
                        // Adjust 100L if you want a wider or tighter time window
                        withTimeoutOrNull(50L) {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                        // Grab EVERY finger touching the screen right now
                        val currentPointers = currentEvent.changes.filter { it.pressed }

                        // Save all finger positions at once
                        for (pointer in currentPointers) {
                            taps.add(pointer.position)
                        }

                        // Update UI text showing how many fingers touched
                        tapLocation = "Recorded ${currentPointers.size} fingers at once!"
                        // print the current coordinates being pressed (or dots) or call

                        // Call runModel()
                        val rawOutput: String = runModel(taps, dict.pointsL2D)
                        val brailleOutput = processRawBraille(rawOutput, points)

                        Log.d("CONV", "Point -> $brailleOutput")
                        tapEq = brailleOutput
                        taps.clear()
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
                text = tapEq,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }

    fun convertToBraille(cells: List<Boolean>): String{
        // Converting Flags to Braille
        var brailleCell: String = ""
        // Move one to six
        brailleCell += if (cells[0]) "1" else "0"
        brailleCell += if (cells[1]) "1" else "0"
        brailleCell += if (cells[2]) "1" else "0"
        brailleCell += if (cells[3]) "1" else "0"
        brailleCell += if (cells[4]) "1" else "0"
        brailleCell += if (cells[5]) "1" else "0"

        Log.d("CONV", brailleCell)

        return brailleCell
    }

    // This is for Running the Models (k-NN, SVM, Random Forest)
    fun runModel(tapSet: List<Offset>, pointsL2D: List<List<Offset>>): String {
        // Run Model and use Reference Data Points with Actual Data Points
        var cells = mutableListOf(false, false, false, false, false, false)
        val ML_kNN = kNearestNeighbor()
        var set: String?

        // Loop through all Points
        for(i in tapSet){
            set = ML_kNN.kNN(i, pointsL2D)
            when(set){
                "a" -> cells[3] = true
                "b" -> cells[4] = true
                "c" -> cells[5] = true
                "d" -> cells[0] = true
                "e" -> cells[1] = true
                "f" -> cells[2] = true
            }
        }

        return convertToBraille(cells)
    }

    fun processRawBraille(brailleOutput: String, pts: BrailleDictionary): String {
        val output: String = when {
            // Caps Lock Turn Off (checked first if already in caps lock mode)
            brailleOutput == "000001" && isCapital -> {
                isCapital = false
                isCapitalOnce = false
                "Capital Off"
            }
            // Capital Once active -> next press activates Caps Lock
            brailleOutput == "000001" && isCapitalOnce -> {
                isCapital = true
                isCapitalOnce = false
                "Caps Lock"
            }
            // First tap: Capital Once On
            brailleOutput == "000001" -> {
                isCapitalOnce = true
                "Capital Sign"
            }
            brailleOutput == "000011" -> {
                isNumeral = false
                "Letter Sign"
            }
            brailleOutput == "001111" -> {
                isNumeral = true
                "Numeral Sign"
            }
            // Handle numerals if the flag is active
            isNumeral -> when (brailleOutput) {
                "100000" -> "1"
                "110000" -> "2"
                "100100" -> "3"
                "100110" -> "4"
                "100010" -> "5"
                "110100" -> "6"
                "110110" -> "7"
                "110010" -> "8"
                "010100" -> "9"
                "010110" -> "0"
                else -> {
                    val original = pts.brailleConversion[brailleOutput] ?: ""
                    val result = if (isCapital || isCapitalOnce) original.uppercase() else original
                    isCapitalOnce = false
                    result
                }
            }
            // Fallback to standard conversion with capitalization support
            else -> {
                val original = pts.brailleConversion[brailleOutput] ?: ""
                val result = if (isCapital || isCapitalOnce) original.uppercase() else original
                isCapitalOnce = false // consume single capital flag
                val ifNullCheck = if(result != null || result != "") result else "Null"
                ifNullCheck
            }
        }
        return output
    }
}