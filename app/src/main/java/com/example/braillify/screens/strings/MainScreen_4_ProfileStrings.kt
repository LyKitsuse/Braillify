package com.example.braillify.screens

import android.content.Context

object ProfileStrings {
    // English Translation
    val english = mapOf(
        // Profile Screen
        "profile_title" to "Profile",
        "profile_active" to "Active",
        "profile_tap_to_activate" to "Tap to activate",
        "profile_add_title" to "Add Profile",
        "profile_add_sub" to "Create and calibrate new profile",
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Profile Screen
        "profile_title" to "Profile",
        "profile_active" to "Aktibo",
        "profile_tap_to_activate" to "Pindutin upang magamit",
        "profile_add_title" to "Magdagdag ng Profile",
        "profile_add_sub" to "Gumawa at i-calibrate ang bagong profile",
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