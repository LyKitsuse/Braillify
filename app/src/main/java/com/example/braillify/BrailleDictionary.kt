package com.example.braillify

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
}