package com.example.braillify.screens

import android.content.Context

object MainScreenStrings {
    // English Translation
    val english = mapOf(
        // Main Screen
        "main_app_name" to "Braillify",
        "main_tap_start" to "Tap to Start",
        "main_tap_start_desc" to "Open the Braille Keyboard and start typing.",
        "main_tap_start_a11y" to "Tap to Start. Open the Braille Keyboard and start typing.",
        "main_start_btn" to "Start",

        "main_practice_title" to "Practice Braille",
        "main_practice_desc" to "Practice Braille Code",

        "main_notes_title" to "Notes",
        "main_notes_desc" to "Saved Notes: %1\$d",

        "main_recalibrate_title" to "Recalibrate",
        "main_recalibrate_desc" to "Adjust Tap Points",

        "main_profile_title" to "Profile",
        "main_profile_desc" to "Go to Saved Profiles",

        // NavBar
        "nav_home" to "Home",
        "nav_keyboard" to "Keyboard",
        "nav_settings" to "Settings",
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Main Screen
        "main_app_name" to "Braillify",
        "main_tap_start" to "Pindutin para Magsimula",
        "main_tap_start_desc" to "Buksan ang Braille Keyboard at magsimulang mag-type.",
        "main_tap_start_a11y" to "Pindutin para Magsimula. Buksan ang Braille Keyboard at magsimulang mag-type.",
        "main_start_btn" to "Simulan",

        "main_practice_title" to "Magpraktis ng Braille",
        "main_practice_desc" to "Magpraktis ng Braille Code",

        "main_notes_title" to "Mga Note",
        "main_notes_desc" to "Mga Naka-save na Note: %1\$d",

        "main_recalibrate_title" to "I-recalibrate",
        "main_recalibrate_desc" to "Ayusin ang mga Tap Point",

        "main_profile_title" to "Profile",
        "main_profile_desc" to "Pumunta sa mga Naka-save na Profile",

        // NavBar
        "nav_home" to "Simula",
        "nav_keyboard" to "Keyboard",
        "nav_settings" to "Mga Setting",
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

