package com.example.braillify

import androidx.compose.ui.geometry.Offset

object BrailleDictionary {
    val brailleConversion = mapOf(
        // Braille Alphabet
        "100000" to "a", // Also 1
        "110000" to "b", // Also 2
        "100100" to "c", // Also 3
        "100110" to "d", // Also 4
        "100010" to "e", // Also 5
        "110100" to "f", // Also 6
        "110110" to "g", // Also 6
        "110010" to "h", // Also 7
        "010100" to "i", // Also 8
        "010110" to "j", // Also 9
        "101000" to "k", // Also 0
        "111000" to "l",
        "101100" to "m",
        "101110" to "n",
        "101010" to "o",
        "111100" to "p",
        "111110" to "q",
        "111010" to "r",
        "011100" to "s",
        "011110" to "t",
        "101001" to "u",
        "111001" to "v",
        "101101" to "x",
        "101111" to "y",
        "101011" to "z",
        "010111" to "w",

        // Punctuation Marks
        "010000" to ",",
        "011000" to ";",
        "010010" to ":",
        "010011" to ".",
        "011010" to "!",
        "011011" to "()",
        "011001" to "?",
        "001010" to "*",
        "001011" to "\"",
        "001000" to "'",
        "001001" to "-",

        // Formatting Signs
        "000011" to "Letter Sign",
        "000001" to "Capital Sign",
        "001011" to "Numeral Sign" // Note: 001111 in standard Braille (dots 3, 4, 5, 6)
    )

    val pointsLRef = listOf(
        Offset(340f, 170f),
        Offset(340f, 380f),
        Offset(340f, 550f),
        Offset(1250f, 170f),
        Offset(1250f, 380f),
        Offset(1250f, 550f),
    )

    val pointsL = listOf(
        // Point 1 (340, 170) -> 2x2 grid
        Offset(340f, 170f),
        Offset(365f, 170f),
        Offset(365f, 195f),
        Offset(340f, 195f),

        // Point 2 (340, 380) -> 2x2 grid
        Offset(340f, 380f),
        Offset(365f, 380f),
        Offset(365f, 405f),
        Offset(340f, 405f),

        // Point 3 (340, 550) -> 2x2 grid
        Offset(340f, 550f),
        Offset(365f, 550f),
        Offset(365f, 575f),
        Offset(340f, 575f),

        // Point 4 (1250, 170) -> 2x2 grid
        Offset(1250f, 170f),
        Offset(1275f, 170f),
        Offset(1275f, 195f),
        Offset(1250f, 195f),

        // Point 5 (1250, 380) -> 2x2 grid
        Offset(1250f, 380f),
        Offset(1275f, 380f),
        Offset(1275f, 405f),
        Offset(1250f, 405f),

        // Point 6 (1250, 550) -> 2x2 grid
        Offset(1250f, 550f),
        Offset(1275f, 550f),
        Offset(1275f, 575f),
        Offset(1250f, 575f)
    )

    val pointsP = listOf(
        Offset(340f, 170f),
        Offset(340f, 380f),
        Offset(340f, 550f),
        Offset(1250f, 170f),
        Offset(1250f, 380f),
        Offset(1250f, 550f)
    )
}