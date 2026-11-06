package com.example.braillify.screens

import android.content.Context

object EmergencyStrings {
    // English Translation
    val english = mapOf(
        // SOS Main Screen
        "sos_title" to "Safety Feature",
        "sos_hero_title" to "Stay Safe",
        "sos_hero_desc" to "Quickly call for help by using a special gesture while typing in Braillify.",
        "sos_contact_row" to "Emergency Contact",
        "sos_gesture_row" to "Safety Gesture",
        "sos_enable" to "Enable Safety Gesture",
        "sos_enable_sub" to "Allow Safety Gesture to work while typing",
        "sos_info" to "When enabled, you can trigger Safety Gesture with the selected gesture while using the Braillify keyboard.",

        // Emergency Contact
        "ec_title" to "Emergency Contact",
        "ec_hero_title" to "Emergency Contact",
        "ec_hero_desc" to "This number will be called when SOS is activated.",
        "ec_phone_label" to "Phone Number",
        "ec_save" to "Save",
        "ec_info" to "Make sure this is a trusted and reachable contact.",
        "ec_edit_title" to "Edit Phone Number",
        "ec_edit_placeholder" to "9123456789",
        "ec_edit_label" to "Phone Number (10 digits)",
        "ec_confirm" to "Confirm",
        "ec_cancel" to "Cancel",
        "ec_default" to "+63 XXX XXX XXXX",

        // SOS Gesture
        "sg_title" to "SOS Gesture",
        "sg_hero_title" to "Choose your SOS gesture",
        "sg_hero_desc" to "Trigger SOS while typing using\nthe gesture you select.",
        "sg_opt0_title" to "Tap 6 Dots 5 times, and Then Swipe Up then Swipe Down",
        "sg_opt0_sub" to "Tap 6 Dots 5 times then Swipe Up and Down",
        "sg_opt1_title" to "Triple Tap, Swipe, and Then Triple Tap",
        "sg_opt1_sub" to "Tap 6 Dots 3 times, swipe right, then tap 6 dots 3 times",
        "sg_save" to "Save"
    )

    // Tagalog Translations
    val tagalog = mapOf(
        // SOS Main Screen
        "sos_title" to "Feature ng safety",
        "sos_hero_title" to "Manatiling Ligtas",
        "sos_hero_desc" to "Mabilis na humingi ng tulong sa pamamagitan ng paggamit ng espesyal na gesture habang nagta-type sa Braillify.",
        "sos_contact_row" to "Emergency Contact",
        "sos_gesture_row" to "Safety Gesture",
        "sos_enable" to "Gamitin ang Safety Gesture",
        "sos_enable_sub" to "Paganahin ang Safety Gesture habang nagta-type",
        "sos_info" to "Kapag pinagana, maaari mong i-trigger ang Safety Gesture gamit ang napiling gesture habang ginagamit ang Braillify keyboard.",

        // Emergency Contact
        "ec_title" to "Emergency Contact",
        "ec_hero_title" to "Emergency Contact",
        "ec_hero_desc" to "Ang numerong ito ay tatawagan kapag na-aktibo ang SOS.",
        "ec_phone_label" to "Numero ng Telepono",
        "ec_save" to "I-save",
        "ec_info" to "Tiyakin na ito ay isang mapagkakatiwalaan at matatawagan na contact.",
        "ec_edit_title" to "Baguhin ang Numero ng Telepono",
        "ec_edit_placeholder" to "9123456789",
        "ec_edit_label" to "Numero ng Telepono (10 digits)",
        "ec_confirm" to "Kumpirmahin",
        "ec_cancel" to "Kanselahin",
        "ec_default" to "+63 XXX XXX XXXX",

        // SOS Gesture
        "sg_title" to "SOS Gesture",
        "sg_hero_title" to "Pumili ng SOS gesture",
        "sg_hero_desc" to "Gamitin ang SOS habang nagta-type gamit ang napiling gesture.",
        "sg_opt0_title" to "Pindutin ang anim na Dots ng limang beses, pagkatapos ay I-swipe Pataas at Pababa",
        "sg_opt0_sub" to "Pindutin ang anim na Dots ng limang beses, pagkatapos ay I-swipe Pataas at Pababa",
        "sg_opt1_title" to "Tatlong pindot sa anim na dots, Swipe pakanan, at Pagkatapos ay Tatlong pindot sa anim na dots",
        "sg_opt1_sub" to "Pindutin ng 3 beses ang anim na dots, i-swipe pakanan, pagkatapos ay muling pindutin ng 3 beses ang anim na dots",
        "sg_save" to "I-save"
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