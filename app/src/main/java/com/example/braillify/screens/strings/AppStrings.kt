package com.example.braillify.screens

import android.content.Context

object AppStrings {
    // Combine all the separate string files here
    private val englishMap = AboutBraillifyStrings.english +
            EmergencyStrings.english +
            VoiceAssistanceStrings.english +
            SettingsMain_Strings.english +
            ProfileStrings.english +
            NoteStrings.english +
            BrailleDictionaryStrings.english +
            PracticeBrailleStrings.english +
            MainScreenStrings.english +
            OnboardingStrings.english


    private val tagalogMap = AboutBraillifyStrings.tagalog +
            EmergencyStrings.tagalog +
            VoiceAssistanceStrings.tagalog +
            SettingsMain_Strings.tagalog +
            ProfileStrings.tagalog +
            NoteStrings.tagalog +
            BrailleDictionaryStrings.tagalog +
            PracticeBrailleStrings.tagalog +
            MainScreenStrings.tagalog +
            OnboardingStrings.tagalog


    fun get(context: Context, key: String): String {
        val lang = SettingsPrefs.getLanguage(context)
        return if (lang == "Tagalog") {
            tagalogMap[key] ?: englishMap[key] ?: key
        } else {
            englishMap[key] ?: key
        }
    }
}