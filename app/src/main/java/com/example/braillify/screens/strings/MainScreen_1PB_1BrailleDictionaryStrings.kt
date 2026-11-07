package com.example.braillify.screens

import android.content.Context

object BrailleDictionaryStrings {
    // English Translation
    val english = mapOf(
        // Braille Dictionary
        "dict_title" to "Braille Dictionary",
        "dict_tab_alphabets" to "Alphabets",
        "dict_tab_digits" to "Digits",
        "dict_tab_punctuations" to "Punctuations",
        "dict_section_alphabets" to "Alphabets (A–Z)",
        "dict_section_digits" to "Digits (0–9)",
        "dict_section_punctuations" to "Punctuations",
        "dict_chars_count" to "characters",

        // Detail
        "dict_category_letter" to "Letter",
        "dict_category_digit" to "Digit",
        "dict_category_symbol" to "Symbol",
        "dict_pattern_title" to "Braille Pattern",
        "dict_info_text" to "The %1\$s %2\$s is represented by dot%3\$s %4\$s.",
        "dict_dot" to "dot",
        "dict_dots" to "dots",

        // Dot Labels
        "dict_dot_1" to "top left",
        "dict_dot_2" to "middle left",
        "dict_dot_3" to "bottom left",
        "dict_dot_4" to "top right",
        "dict_dot_5" to "middle right",
        "dict_dot_6" to "bottom right"
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Braille Dictionary
        "dict_title" to "Diksiyonaryo ng Braille",
        "dict_tab_alphabets" to "Letra",
        "dict_tab_digits" to "Numero",
        "dict_tab_punctuations" to "Bantas",
        "dict_section_alphabets" to "Mga Letra (A–Z)",
        "dict_section_digits" to "Mga Numero (0–9)",
        "dict_section_punctuations" to "Mga Bantas",
        "dict_chars_count" to "mga karakter",

        // Detail
        "dict_category_letter" to "Letra",
        "dict_category_digit" to "Numero",
        "dict_category_symbol" to "Simbolo",
        "dict_pattern_title" to "Pattern ng Braille",
        "dict_info_text" to "Ang %1\$s %2\$s ay kinakatawan ng dot%3\$s %4\$s.",
        "dict_dot" to "dot",
        "dict_dots" to "mga dot",

        // Dot Labels
        "dict_dot_1" to "taas na kaliwa",
        "dict_dot_2" to "gitnang kaliwa",
        "dict_dot_3" to "babang kaliwa",
        "dict_dot_4" to "taas na kanan",
        "dict_dot_5" to "gitnang kanan",
        "dict_dot_6" to "babang kanan"
    )

    fun get(context: Context, key: String): String {
        val lang = SettingsPrefs.getLanguage(context)
        return if (lang == "Tagalog") {
            tagalog[key] ?: english[key] ?: key
        } else {
            english[key] ?: key
        }
    }
}