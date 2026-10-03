package com.example.braillify.screens

import android.content.Context

object SettingsPrefs {
    private const val PREFS_NAME = "braillify_settings"
    private const val KEY_VOICE_SPEED = "voice_speed"
    private const val KEY_VOICE_VOLUME = "voice_volume"
    private const val KEY_HAPTIC = "haptic_on"
    private const val KEY_LANDSCAPE_TYPING = "landscape_typing"
    private const val KEY_LATENCY_DELAY = "latency_delay"
    private const val KEY_SEQUENTIAL_MODE = "sequential_mode"

    fun getVoiceSpeed(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_VOICE_SPEED, "Normal") ?: "Normal"

    fun setVoiceSpeed(context: Context, value: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_VOICE_SPEED, value).apply()
    }

    fun getVoiceVolume(context: Context): Float =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getFloat(KEY_VOICE_VOLUME, 0.67f)

    fun setVoiceVolume(context: Context, value: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(KEY_VOICE_VOLUME, value).apply()
    }

    fun getHapticOn(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_HAPTIC, true)

    fun setHapticOn(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_HAPTIC, value).apply()
    }

    fun getLandscapeTyping(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LANDSCAPE_TYPING, true)

    fun setLandscapeTyping(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_LANDSCAPE_TYPING, value).apply()
    }

    fun getLatencyDelay(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LATENCY_DELAY, 0)

    fun setLatencyDelay(context: Context, value: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_LATENCY_DELAY, value).apply()
    }

    fun getSequentialMode(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_SEQUENTIAL_MODE, false)

    fun setSequentialMode(context: Context, value: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_SEQUENTIAL_MODE, value).apply()
    }
}