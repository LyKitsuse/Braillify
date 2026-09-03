package com.example.braillify

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.braillify.BrailleDictionary


class MockKeyboard {
    @Preview(showBackground = true)
    @Composable
    fun BrailleSandbox() {
        val brailleDict = BrailleDictionary
        var tapLocation by remember { mutableStateOf("Tap Anywhere!") }
        val taps = remember { mutableStateListOf<Offset>() }

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
                taps.forEachIndexed { index, offset ->
                    drawCircle(
                        color = Color(0xFF6200EE),
                        radius = 10f,
                        center = offset
                    )
                    if ((index + 1) % 5 == 0) {
                        drawCircle(
                            color = Color.White,
                            radius = 8f,
                            center = offset
                        )
                    }
//                    if (index == 20){
//                        for ()
//                    }
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
}