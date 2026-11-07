package com.example.braillify.screens

import android.content.Context

object PracticeBrailleStrings {
    // English Translation
    val english = mapOf(
        // Practice Braille
        "practice_title" to "Practice Braille",
        "practice_keyboard_title" to "Braille Keyboard",
        "practice_keyboard_desc" to "Type using braille and see the result.",
        "practice_dictionary_title" to "Braille Dictionary",
        "practice_dictionary_desc" to "Browse and learn braille characters, numbers and punctuations.",
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Practice Braille
        "practice_title" to "Magpraktis ng Braille",
        "practice_keyboard_title" to "Braille Keyboard",
        "practice_keyboard_desc" to "Mag-type gamit ang braille at tingnan ang resulta.",
        "practice_dictionary_title" to "Diksiyonaryo ng Braille",
        "practice_dictionary_desc" to "Mag-hanap at matuto ng mga karakter, numero, at bantas sa braille.",
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

