package com.example.braillify.screens

import android.content.Context

object SettingsMain_Strings {
    // English Translation
    val english = mapOf(
        // Settings Main
        "set_title" to "Settings",
        "set_voice_assistance" to "Voice Assistance",
        "set_voice_speed" to "Voice Speed",
        "set_voice_volume" to "Voice Volume",
        "set_keyboard_prefs" to "Keyboard Preferences",
        "set_landscape" to "Landscape Typing Preference",
        "set_landscape_desc" to "Effective even in portrait",
        "set_input_mode" to "Input Mode",
        "set_input_sequential" to "Sequential",
        "set_input_simultaneous" to "Simultaneous",
        "set_emergency" to "Emergency",
        "set_safety_feature" to "Safety Feature",
        "set_safety_desc" to "Emergency contact and safety gesture",
        "set_app_settings" to "App Settings",
        "set_language" to "Language",
        "set_english" to "English",
        "set_tagalog" to "Tagalog",
        "set_haptic" to "Haptic Feedback",
        "set_about" to "About Braillify",
        "set_exit" to "Exit Braillify",

        // Dialogs
        "dialog_exit_title" to "Exit Braillify?",
        "dialog_exit_desc" to "Are you sure you want to exit the app?",
        "dialog_exit_confirm" to "Exit",
        "dialog_cancel" to "Cancel",
        "dialog_input_title" to "Input Mode",
        "dialog_lang_title" to "Select Language"
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Settings Main
        "set_title" to "Mga Setting",
        "set_voice_assistance" to "Voice Assistance",
        "set_voice_speed" to "Bilis ng Boses",
        "set_voice_volume" to "Lakas ng Boses",
        "set_keyboard_prefs" to "Mga Preference sa Keyboard",
        "set_landscape" to "Preference sa Pagta-type sa Landscape",
        "set_landscape_desc" to "Gumagana kahit sa portrait",
        "set_input_mode" to "Paraan ng Input",
        "set_input_sequential" to "Sunod-sunod",
        "set_input_simultaneous" to "Sabay-sabay",
        "set_emergency" to "Emergency",
        "set_safety_feature" to "Feature ng safety",
        "set_safety_desc" to "Emergency contact at safety gesture",
        "set_app_settings" to "Mga Setting ng App",
        "set_language" to "Wika",
        "set_english" to "Ingles",
        "set_tagalog" to "Tagalog",
        "set_haptic" to "Haptic Feedback",
        "set_about" to "Tungkol sa Braillify",
        "set_exit" to "Lumabas sa Braillify",

        // Dialogs
        "dialog_exit_title" to "Lumabas sa Braillify?",
        "dialog_exit_desc" to "Sigurado ka bang gusto mong lumabas sa app?",
        "dialog_exit_confirm" to "Lumabas",
        "dialog_cancel" to "Kanselahin",
        "dialog_input_title" to "Paraan ng Input",
        "dialog_lang_title" to "Pumili ng Wika"
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

