package com.example.braillify

import androidx.compose.ui.geometry.Offset

object BrailleDictionary {
    val brailleConversion = mapOf(
        // Braille Alphabet (Binary order: Dot 1, 2, 3, 4, 5, 6)
        "100000" to "a", // Also Number 1
        "110000" to "b", // Also Number 2
        "100100" to "c", // Also Number 3
        "100110" to "d", // Also Number 4
        "100010" to "e", // Also Number 5
        "110100" to "f", // Also Number 6
        "110110" to "g", // Also Number 7
        "110010" to "h", // Also Number 8
        "010100" to "i", // Also Number 9
        "010110" to "j", // Also Number 0
        "101000" to "k",
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
        "010111" to "w", // Dots 2, 4, 5, 6
        "101101" to "x",
        "101111" to "y",
        "101011" to "z",

        // Punctuation Marks (Binary order: Dot 1, 2, 3, 4, 5, 6)
        "010000" to ",",   // Dot 2
        "011000" to ";",   // Dots 2, 3
        "010010" to ":",   // Dots 2, 5
        "010011" to ".",   // Dots 2, 5, 6
        "011010" to "!",   // Dots 2, 3, 5
        "011001" to "?",   // Dots 2, 3, 6 (Also used for Open Quote in UEB)
        "001000" to "'",   // Dot 3
        "001001" to "-",   // Dots 3, 6
        "110001" to "(",   // Dots 1, 2, 6
        "001110" to ")",   // Dots 3, 4, 5

        // Formatting Signs
        "000011" to "Letter Sign",  // Dots 5, 6
        "000001" to "Capital Sign", // Dot 6
        "001111" to "Numeral Sign"  // Dots 3, 4, 5, 6
    )

    val pointsLRef = listOf(
        Offset(340f, 170f),
        Offset(340f, 380f),
        Offset(340f, 550f),
        Offset(1250f, 170f),
        Offset(1250f, 380f),
        Offset(1250f, 550f),
    )

    // Synthetic Data A
    val pointsL2D: List<List<Offset>> = listOf(
        // Dot 1 (a)
        listOf(
            Offset(340f, 170f),
            Offset(365f, 170f),
            Offset(365f, 195f),
            Offset(340f, 195f)
        ),
        // Dot 2 (b)
        listOf(
            Offset(340f, 380f),
            Offset(365f, 380f),
            Offset(365f, 405f),
            Offset(340f, 405f)
        ),
        // Dot 3 (c)
        listOf(
            Offset(340f, 550f),
            Offset(365f, 550f),
            Offset(365f, 575f),
            Offset(340f, 575f)
        ),
        // Dot 4 (d)
        listOf(
            Offset(1250f, 170f),
            Offset(1275f, 170f),
            Offset(1275f, 195f),
            Offset(1250f, 195f)
        ),
        // Dot 5 (e)
        listOf(
            Offset(1250f, 380f),
            Offset(1275f, 380f),
            Offset(1275f, 405f),
            Offset(1250f, 405f)
        ),
        // Dot 6 (f)
        listOf(
            Offset(1250f, 550f),
            Offset(1275f, 550f),
            Offset(1275f, 575f),
            Offset(1250f, 575f)
        )
    )
    // Synthetic Data A (Main Reference)
    val pointsP = listOf(
        Offset(340f, 170f),
        Offset(340f, 380f),
        Offset(340f, 550f),
        Offset(1250f, 170f),
        Offset(1250f, 380f),
        Offset(1250f, 550f)
    )
}