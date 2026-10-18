package com.example.braillify

import androidx.compose.ui.geometry.Offset
import com.example.braillify.machineLearningModels.kNearestNeighbor

class SequentialMode {
    private val mL_kNN = kNearestNeighbor()
    private val accumulatedDots = BooleanArray(6) { false }

    fun sequentialInput(
        taps: List<Offset>,
        calibratedMain: List<List<Offset>>,
        dict: BrailleDictionary,
        keyboard: Keyboard,
        onBrailleOutputChanged: (String) -> Unit,
        onStatusLabelChanged: (String) -> Unit
    ) {
        if (taps.isEmpty()) return

        for (tap in taps) {
            val detectedDot = mL_kNN.kNN(tap, calibratedMain)
            when (detectedDot) {
                "a" -> accumulatedDots[0] = true
                "b" -> accumulatedDots[1] = true
                "c" -> accumulatedDots[2] = true
                "d" -> accumulatedDots[3] = true
                "e" -> accumulatedDots[4] = true
                "f" -> accumulatedDots[5] = true
            }
        }

        val rawPattern = getRawBraillePattern()
        val previewText = previewBraille(rawPattern, dict, keyboard)

        onBrailleOutputChanged(previewText)
        onStatusLabelChanged("Typed: $previewText")
        keyboard.speakCharacter(previewText)
    }

    private fun previewBraille(rawPattern: String, dict: BrailleDictionary, keyboard: Keyboard): String {
        return when {
            rawPattern == "000001" -> "Capital Sign"
            rawPattern == "000011" -> "Letter Sign"
            rawPattern == "001111" -> "Numeral Sign"
            keyboard.isNumeral -> when (rawPattern) {
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
                    val knownChar = dict.brailleConversion[rawPattern]
                    if (knownChar != null) {
                        if (keyboard.isCapital || keyboard.isCapitalOnce) knownChar.uppercase() else knownChar
                    } else {
                        keyboard.formatDots(rawPattern)
                    }
                }
            }
            else -> {
                val knownChar = dict.brailleConversion[rawPattern]
                if (knownChar != null) {
                    if (keyboard.isCapital || keyboard.isCapitalOnce) knownChar.uppercase() else knownChar
                } else {
                    keyboard.formatDots(rawPattern)
                }
            }
        }
    }

    fun hasInput(): Boolean {
        return accumulatedDots.any { it }
    }

    fun clear() {
        for (i in accumulatedDots.indices) {
            accumulatedDots[i] = false
        }
    }

    fun getRawBraillePattern(): String {
        return accumulatedDots.joinToString("") { if (it) "1" else "0" }
    }
}
