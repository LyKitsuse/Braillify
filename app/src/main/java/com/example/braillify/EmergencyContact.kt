package com.example.braillify

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.telecom.TelecomManager
import android.util.Log
import com.example.braillify.screens.SettingsPrefs

class EmergencyContact {
    var count = 0
    var state = 0 // 0 = default, 1 = wait swipe up, 2 = wait swipe down

    fun checkIfSOS(context: Context, out: String) {
        if (!SettingsPrefs.getSOSEnabled(context)) {
            count = 0
            state = 0
            return
        }
        if (out == "111111") {
            count++
            Log.d("DEBUG", "Count: $count")
            if (count >= 5) {
                state = 1
            }
        } else {
            Log.d("DEBUG", "Count Reset: $count")
            count = 0
            state = 0
        }
    }

    fun pingPhoneNum(context: Context, onSpeak: ((String) -> Unit)? = null) {
        val number = "0${SettingsPrefs.getEmergencyNumber(context)}"
        if (number.isNotEmpty()) {
            val message = "Emergency Contact, Redirecting to Phone Call"
            if (onSpeak != null) {
                onSpeak(message)
            } else {
                var tts: TextToSpeech? = null
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val params = android.os.Bundle().apply {
                            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, SettingsPrefs.getVoiceVolume(context))
                        }
                        tts?.speak(message, TextToSpeech.QUEUE_FLUSH, params, "EmergencyTTS")
                    }
                }
            }

            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager
            val defaultDialer = telecomManager?.defaultDialerPackage

            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                if (!defaultDialer.isNullOrEmpty()) {
                    setPackage(defaultDialer)
                }
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (!defaultDialer.isNullOrEmpty()) {
                        setPackage(defaultDialer)
                    }
                }
                try {
                    context.startActivity(dialIntent)
                } catch (_: Exception) {
                    val fallbackIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallbackIntent)
                }
            }
        }
    }

    fun handleSwipe(totalDx: Float, totalDy: Float, context: Context, onSpeak: ((String) -> Unit)? = null): Boolean {
        if (!SettingsPrefs.getSOSEnabled(context)) {
            state = 0
            count = 0
            return false
        }
        if (state == 0) return false

        val isVertical = kotlin.math.abs(totalDy) > kotlin.math.abs(totalDx)
        val isUp = isVertical && totalDy < 0
        val isDown = isVertical && totalDy > 0

        if (state == 1 && isUp) {
            state = 2
            Log.d("DEBUG", "Swiped UP")
            return true
        }

        if (state == 2 && isDown) {
            count = 0
            state = 0
            Log.d("DEBUG", "Call Number")
            pingPhoneNum(context, onSpeak)
            return true
        }

        state = 0
        return true
    }
}
