package com.example.braillify.screens

import android.content.Context

object VoiceAssistanceStrings {
    // English Translation
    val english = mapOf(
        // Voice Speed
        "vs_title" to "Voice Speed",
        "vs_slow" to "Slow",
        "vs_slow_desc" to "A slow-paced voice speed",
        "vs_slow_hero_desc" to "A slower, relaxed pace for careful listening.",
        "vs_normal" to "Normal",
        "vs_normal_desc" to "Default speed",
        "vs_normal_hero_desc" to "Balanced speed for clear and comfortable listening.",
        "vs_fast" to "Fast",
        "vs_fast_desc" to "A fast-paced voice speed",
        "vs_fast_hero_desc" to "A quicker pace for faster reading.",
        "vs_veryfast" to "Very Fast",
        "vs_veryfast_desc" to "A very fast-paced voice speed",
        "vs_veryfast_hero_desc" to "Maximum speed for experienced users.",

        // Voice Volume
        "vv_title" to "Voice Volume",
        "vv_current_volume" to "Current volume",
        "vv_low" to "Low",
        "vv_medium" to "Medium",
        "vv_high" to "High",
        "vv_low_content_desc" to "Low volume",
        "vv_high_content_desc" to "High volume",
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Voice Speed
        "vs_title" to "Bilis ng Boses",
        "vs_slow" to "Mabagal",
        "vs_slow_desc" to "Isang mabagal na bilis ng boses",
        "vs_slow_hero_desc" to "Isang mas mabagal, relaks na bilis para sa maingat na pakikinig.",
        "vs_normal" to "Normal",
        "vs_normal_desc" to "Default na bilis",
        "vs_normal_hero_desc" to "Balanse na bilis.",
        "vs_fast" to "Mabilis",
        "vs_fast_desc" to "Mabilis na boses",
        "vs_fast_hero_desc" to "Mas mabilis na boses",
        "vs_veryfast" to "Napakabilis",
        "vs_veryfast_desc" to "Napakabilis na boses",
        "vs_veryfast_hero_desc" to "Pinaka mabilis",

        // Voice Volume
        "vv_title" to "Lakas ng Boses",
        "vv_current_volume" to "Kasalukuyang lakas",
        "vv_low" to "Mahina",
        "vv_medium" to "Katamtaman",
        "vv_high" to "Malakas",
        "vv_low_content_desc" to "Mababang lakas",
        "vv_high_content_desc" to "Mataas na lakas",
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