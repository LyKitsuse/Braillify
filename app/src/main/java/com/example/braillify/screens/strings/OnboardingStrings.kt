package com.example.braillify.screens

import android.content.Context

object OnboardingStrings {
    // English Translation
    val english = mapOf(
        // Language Selection
        "onb_lang_welcome" to "Welcome to",
        "onb_lang_subtitle" to "Let's make communication\nmore accessible.",
        "onb_lang_step" to "Step 1 of 4 • Language Selection",
        "onb_lang_choose_title" to "Choose your language",
        "onb_lang_choose_desc" to "Select your preferred language for using Braillify.",
        "onb_lang_footer" to "You can change your language later in Settings.",
        "onb_lang_english_native" to "English",
        "onb_lang_tagalog_native" to "Filipino",
        "onb_next" to "Next",

        // Welcome
        "onb_welcome_title" to "Welcome to Braillify",
        "onb_welcome_sub" to "Type, learn, and stay connected —\nwith Braille.",
        "onb_welcome_start" to "Get Started",
        "onb_welcome_learn" to "Learn more",

        // Steps (Reusable)
        "onb_step" to "Step %1\$d of 4",
        "onb_next_btn" to "Next",
        "onb_skip" to "Skip for now",
        "onb_ive_enabled" to "I've enabled it — Continue",
        "onb_do_later" to "I'll do it later",
        "onb_open_settings" to "Open Settings",

        // Step 1 - TalkBack
        "onb_s1_title" to "Enable TalkBack Shortcut",
        "onb_s1_desc" to "TalkBack touch gestures can interfere with multi-finger Braille typing. Enabling the TalkBack shortcut lets you quickly toggle TalkBack off while typing and turn it back on when you're done.",
        "onb_s1_info" to "This prevents TalkBack from interfering with your Braille typing.",
        "onb_s1_warn" to "Please enable accessibility service to continue.",

        // Step 2 - Keyboard
        "onb_s2_title" to "Enable Braillify Keyboard",
        "onb_s2_desc" to "Add Braillify to your keyboard settings to start typing in Braille.",
        "onb_s2_info" to "This allows you to use Braillify as your keyboard input method.",
        "onb_s2_warn" to "Please enable Braillify Keyboard on Settings.",

        // Step 3 - Calibration
        "onb_s3_title" to "Calibrate Your Touchpoints",
        "onb_s3_desc" to "Tap six points on the screen to align the Braille keys with where your fingers naturally rest.",
        "onb_s3_info" to "This ensures accurate Braille input and a smoother experience.",
        "onb_s3_btn" to "Start Calibration",

        // Step 4 - Emergency
        "onb_s4_title" to "Set up Emergency Contact",
        "onb_s4_desc" to "Add a trusted contact so someone can be reached in case of an emergency.",
        "onb_s4_info" to "This adds an extra layer of safety when you need it most.",
        "onb_s4_btn" to "Add Contact",

        // Learn More Dialog
        "onb_learn_more_title" to "What is Braillify?",
        "onb_learn_more_heading" to "Purpose of the app",
        "onb_learn_more_body" to "Braillify is a mobile application designed to help visually impaired users type, explore, and learn Braille using their mobile devices. It brings together Braille typing, accessibility support, and learning tools in one application.\n\n" +
                "Whether you are new to Braille or already familiar with it, Braillify provides features to help you practice Braille characters, communicate through text, and navigate the app with accessibility in mind.\n\n" +
                "With a simple interface and guided setup, Braillify aims to make using Braille on a touchscreen more approachable and convenient.\n\n",
        "onb_learn_more_list_title" to "What you can do with Braillify:",
        "onb_learn_more_list" to "- Type using a Braille keyboard.\n" +
                "- Explore Braille characters and their dot patterns.\n" +
                "- Practice and improve your familiarity with Braille.\n" +
                "- Use available accessibility features while interacting with the app.\n" +
                "- Save useful information in Notes.\n\n",
        "onb_learn_more_closing" to "Your journey with Braille starts here.",
        "onb_close" to "Close"
    )

    //Tagalog Translation
    val tagalog = mapOf(
        // Language Selection
        "onb_lang_welcome" to "Maligayang Pagdating sa",
        "onb_lang_subtitle" to "Gawin nating mas madaling ma-access\nang komunikasyon.",
        "onb_lang_step" to "Hakbang 1 ng 4 • Pagpili ng Wika",
        "onb_lang_choose_title" to "Piliin ang iyong wika",
        "onb_lang_choose_desc" to "Piliin ang iyong gustong wika para sa paggamit ng Braillify.",
        "onb_lang_footer" to "Maaari mong baguhin ang wika mamaya sa Settings.",
        "onb_lang_english_native" to "Ingles",
        "onb_lang_tagalog_native" to "Filipino",
        "onb_next" to "Susunod",

        // Welcome
        "onb_welcome_title" to "Maligayang Pagdating sa Braillify",
        "onb_welcome_sub" to "Mag-type, matuto, at manatiling konektado —\ngamit ang Braille.",
        "onb_welcome_start" to "Magsimula",
        "onb_welcome_learn" to "Matuto pa",

        // Steps (Reusable)
        "onb_step" to "Hakbang %1\$d ng 4",
        "onb_next_btn" to "Susunod",
        "onb_skip" to "Laktawan muna",
        "onb_ive_enabled" to "Na-enable ko na — Magpatuloy",
        "onb_do_later" to "Gagawin ko mamaya",
        "onb_open_settings" to "Buksan ang Settings",

        // Step 1 - TalkBack
        "onb_s1_title" to "I-enable ang TalkBack Shortcut",
        "onb_s1_desc" to "Ang mga kilos ng TalkBack ay maaaring makagambala sa multi-finger Braille typing. Ang pag-enable ng TalkBack shortcut ay nagbibigay-daan sa iyo upang mabilis na patayin ang TalkBack habang nagta-type at buksan itong muli kapag tapos ka na.",
        "onb_s1_info" to "Pinipigilan nito ang TalkBack na makagambala sa iyong Braille typing.",
        "onb_s1_warn" to "Mangyaring i-enable ang accessibility service upang magpatuloy.",

        // Step 2 - Keyboard
        "onb_s2_title" to "I-enable ang Keyboard ng Braillify",
        "onb_s2_desc" to "Idagdag ang Braillify sa iyong keyboard settings upang magsimulang mag-type sa Braille.",
        "onb_s2_info" to "Pinapayagan ka nitong gamitin ang Braillify bilang iyong keyboard input method.",
        "onb_s2_warn" to "Mangyaring i-enable ang Keyboard ng Braillify sa Settings.",

        // Step 3 - Calibration
        "onb_s3_title" to "I-calibrate ang Iyong mga Touchpoint",
        "onb_s3_desc" to "Pindutin ang anim na dots sa screen upang ihanay ang mga Braille key sa kung saan mo natural na ipapatong ang iyong mga daliri.",
        "onb_s3_info" to "Tinitiyak nito ang tamang Braille input at mas maayos na karanasan.",
        "onb_s3_btn" to "Simulan ang Calibration",

        // Step 4 - Emergency
        "onb_s4_title" to "Ayusin up ang Emergency Contact",
        "onb_s4_desc" to "Magdagdag ng mapagkakatiwalaang contact upang may maabot sa oras ng emergency.",
        "onb_s4_info" to "Nagbibigay ito ng karagdagang layer ng kaligtasan kapag kailangan mo ito.",
        "onb_s4_btn" to "Magdagdag ng Contact",

        // Learn More Dialog
        "onb_learn_more_title" to "Ano ang Braillify?",
        "onb_learn_more_heading" to "Layunin ng app",
        "onb_learn_more_body" to "Ang Braillify ay isang mobile application na dinisenyo upang tulungan ang mga may kapansanan sa paningin na mag-type, mag-explore, at matuto ng Braille gamit ang kanilang mga mobile device. Pinagsasama-sama nito ang Braille typing, accessibility support, at mga learning tool sa isang application.\n\n" +
                "Kung bago ka pa lang sa Braille o pamilyar na rito, nagbibigay ang Braillify ng mga feature na makakatulong sa iyo na magpraktis ng mga Braille character, makipag-usap gamit ang pag-text, at mag-navigate sa app na isinasaalang-alang ang accessibility.\n\n" +
                "Sa simpleng interface at guided setup, layunin ng Braillify na gawing mas madaling ma-access at maginhawa ang paggamit ng Braille sa isang touchscreen.\n\n",
        "onb_learn_more_list_title" to "Ano ang magagawa mo sa Braillify:",
        "onb_learn_more_list" to "- Mag-type gamit ang keyboard ng Braille.\n" +
                "- Tuklasin ang mga Braille character at ang kanilang mga dot pattern.\n" +
                "- Magpraktis at pagbutihin ang iyong pagiging pamilyar sa Braille.\n" +
                "- Gamitin ang mga available na accessibility features habang nakikipag-ugnayan sa app.\n" +
                "- Mag-save ng kapaki-pakinabang na impormasyon sa mga Note.\n\n",
        "onb_learn_more_closing" to "Ang iyong paglalakbay sa Braille ay nagsisimula rito.",
        "onb_close" to "Isara"
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