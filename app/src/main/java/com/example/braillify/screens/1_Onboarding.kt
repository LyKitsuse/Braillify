package com.example.braillify.screens

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.Calibrate
import com.example.braillify.Keyboard
import com.example.braillify.R
import kotlinx.coroutines.delay

private val Purple = Color(0xFF5B3FE4)
private val PurpleLight = Color(0xFFE8E3FB)
private val Background = Color(0xFFF2F0F5)
private val ACardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)

class Onboarding {
    @Preview(showBackground = true)
    @Composable
    fun OnboardingProcess(onFinished: () -> Unit = {}) {
        // -1 = Language Selection, 0 = Welcome, 1..4 = Steps
        var currentStep by remember { mutableIntStateOf(-1) }
        var step1Done by remember { mutableStateOf(false) }
        var step2Done by remember { mutableStateOf(false) }
        var step3Done by remember { mutableStateOf(false) }
        var step4Done by remember { mutableStateOf(false) }
        var warningDesc by remember { mutableStateOf("") }

        val context = LocalContext.current
        val view = LocalView.current
        val keyboardController = LocalSoftwareKeyboardController.current

        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val stepFocusRequester = remember { FocusRequester() }
        val scrollState = rememberScrollState()
        var wasCalibrating by remember { mutableStateOf(false) }

        var showEmergencyScreen by remember { mutableStateOf(false) }

        LaunchedEffect(currentStep) {
            if (currentStep in 1..4) {
                warningDesc = ""
                scrollState.scrollTo(0)
                delay(100)
                try {
                    stepFocusRequester.requestFocus()
                } catch (_: Exception) {}
                val stepString = String.format(AppStrings.get(context, "onb_step"), currentStep)
                view.announceForAccessibility(stepString)
            }
        }

        LaunchedEffect(Keyboard.isCalibrating) {
            if (Keyboard.isCalibrating) {
                wasCalibrating = true
            } else if (wasCalibrating) {
                if (currentStep == 3) {
                    step3Done = true
                    currentStep = 4
                }
            }
        }

        BackHandler(enabled = currentStep >= 0 && !showEmergencyScreen) {
            currentStep--
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Hidden TextBox
            Box(
                modifier = Modifier
                    .size(1.dp)
                    .graphicsLayer { alpha = 0f }
                    .semantics { hideFromAccessibility() }
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }

            if (showEmergencyScreen) {
                Box(modifier = Modifier.fillMaxSize()) {
                    EmergencyContactScreen(
                        onBack = {
                            showEmergencyScreen = false
                            val newSavedNumber = SettingsPrefs.getEmergencyNumber(context)
                            if (newSavedNumber.isNotEmpty()) {
                                onFinished()
                            }
                        }
                    )
                }
            } else if (currentStep == -1) {
                LanguageSelectionScreen(onNext = { currentStep = 0 })
            } else if (currentStep == 0) {
                WelcomeScreen(onStart = { currentStep = 1 })
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { currentStep-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "‹", color = TextDark, fontSize = 28.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.global_braillify_logo),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = AppStrings.get(context, "main_app_name"), // "Braillify"
                            color = Purple,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        Spacer(Modifier.size(32.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..4) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (i <= currentStep) Purple else PurpleLight)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Step Content
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        when (currentStep) {
                            1 -> Step1Talkback(
                                isClickedYet = step1Done,
                                warningDesc = warningDesc,
                                stepFocusRequester = stepFocusRequester,
                                onActionClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Log.e("DEBUG", "Error opening accessibility settings", e)
                                    }
                                    step1Done = true
                                },
                                onNext = {
                                    step1Done = true
                                    currentStep = 2
                                },
                                onShowWarning = { warningDesc = it }
                            )
                            2 -> Step2Keyboard(
                                isClickedYet = step2Done,
                                warningDesc = warningDesc,
                                stepFocusRequester = stepFocusRequester,
                                onActionClick = {
                                    try {
                                        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Log.e("DEBUG", "Error opening keyboard settings", e)
                                    }
                                    step2Done = true
                                },
                                onNext = {
                                    step2Done = true
                                    currentStep = 3
                                },
                                onShowWarning = { warningDesc = it }
                            )
                            3 -> Step3Calibration(
                                stepFocusRequester = stepFocusRequester,
                                onActionClick = {
                                    step3Done = true
                                    Calibrate().calibrateNew()
                                    focusRequester.requestFocus()
                                    keyboardController?.show()
                                },
                                onNext = {
                                    step3Done = true
                                    currentStep = 4
                                }
                            )
                            4 -> Step4Emergency(
                                stepFocusRequester = stepFocusRequester,
                                onOpenEmergency = {
                                    step4Done = true
                                    showEmergencyScreen = true
                                },
                                onNext = {
                                    step4Done = true
                                    onFinished()
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

// New Phase - Language Selection
@Composable
private fun LanguageSelectionScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(SettingsPrefs.getLanguage(context)) }
    var showHello by remember { mutableStateOf(true) }

    // Back handler: If they press back here, we exit the app
    // (or you can change this to finish the activity).
    val activity = context as? android.app.Activity
    BackHandler(enabled = true) {
        activity?.finish()
    }

    // Loop the text switching
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            showHello = !showHello
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.global_braillify_logo),
            contentDescription = "Braillify Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(40.dp))

        // Animated "Hello / Kumusta"
        Box(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = "${AppStrings.get(context, "onb_lang_hello")}, ${AppStrings.get(context, "onb_lang_kumusta")}"
                },
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showHello,
                transitionSpec = {
                    (slideInVertically(
                        animationSpec = tween(durationMillis = 600),
                        initialOffsetY = { it } // Starts from the bottom
                    ) + fadeIn(animationSpec = tween(600)))
                        .togetherWith(
                            slideOutVertically(
                                animationSpec = tween(durationMillis = 600),
                                targetOffsetY = { -it } // Leaves towards the top
                            ) + fadeOut(animationSpec = tween(600)))
                },
                label = "Greeting"
            ) { isHello ->
                Text(
                    text = if (isHello) AppStrings.get(context, "onb_lang_hello") else AppStrings.get(context, "onb_lang_kumusta"),
                    color = TextDark,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFD0D0D0))
        )

        Spacer(Modifier.height(32.dp))

        // New Header
        Text(
            text = "Set Your Language",
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Language Options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LanguageOption(
                label = AppStrings.get(context, "onb_lang_english"),
                selected = selectedLanguage == "English",
                onSelect = { selectedLanguage = "English" }
            )
            LanguageOption(
                label = AppStrings.get(context, "onb_lang_tagalog"),
                selected = selectedLanguage == "Tagalog",
                onSelect = { selectedLanguage = "Tagalog" }
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                SettingsPrefs.setLanguage(context, selectedLanguage)
                onNext()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(AppStrings.get(context, "onb_lang_next"), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    val backgroundColor = if (selected) PurpleLight else ACardWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .selectable(
                selected = selected,
                onClick = onSelect
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null, // Handled by the selectable modifier
            colors = RadioButtonDefaults.colors(
                selectedColor = Purple,
                unselectedColor = Color.Gray
            )
        )
        Spacer(Modifier.size(16.dp))
        Text(
            text = label,
            color = TextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Phase 0 - Welcome
@Composable
private fun WelcomeScreen(onStart: () -> Unit) {
    val context = LocalContext.current
    var showLearnMore by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        Text(
            text = AppStrings.get(context, "onb_welcome_title"),
            color = TextDark,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = AppStrings.get(context, "onb_welcome_sub"),
            color = TextGray,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.global_braillify_logo),
            contentDescription = "Braillify Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(Modifier.height(60.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(AppStrings.get(context, "onb_welcome_start"), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { showLearnMore = true }) {
            Text(AppStrings.get(context, "onb_welcome_learn"), color = Purple, fontSize = 14.sp)
        }
        Spacer(Modifier.height(24.dp))
    }

    // Learn More Dialog
    if (showLearnMore) {
        AlertDialog(
            onDismissRequest = { showLearnMore = false },
            title = {
                Text(AppStrings.get(context, "onb_learn_more_title"), color = TextDark, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = AppStrings.get(context, "onb_learn_more_heading") + "\n",
                        color = Purple,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = AppStrings.get(context, "onb_learn_more_body") +
                                AppStrings.get(context, "onb_learn_more_list_title") + "\n" +
                                AppStrings.get(context, "onb_learn_more_list") +
                                AppStrings.get(context, "onb_learn_more_closing"),
                        color = TextDark,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLearnMore = false }) {
                    Text(AppStrings.get(context, "onb_close"), color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

// Phase 1 - Talkback
@Composable
private fun Step1Talkback(
    isClickedYet: Boolean,
    warningDesc: String,
    stepFocusRequester: FocusRequester? = null,
    onActionClick: () -> Unit,
    onNext: () -> Unit,
    onShowWarning: (String) -> Unit
) {
    val context = LocalContext.current
    SetupStepLayout(
        stepNumber = 1,
        iconRes = R.drawable.`onboarding_phase1_enabletalkbackshortcut_mi_24px`,
        title = AppStrings.get(context, "onb_s1_title"),
        description = AppStrings.get(context, "onb_s1_desc"),
        infoText = AppStrings.get(context, "onb_s1_info"),
        primaryButtonText = AppStrings.get(context, "onb_open_settings"),
        onPrimaryClick = onActionClick,
        secondaryButtonText = AppStrings.get(context, "onb_ive_enabled"),
        onSecondaryClick = {
            if (!isClickedYet) onShowWarning(AppStrings.get(context, "onb_s1_warn"))
            else onNext()
        },
        warningDesc = warningDesc,
        stepFocusRequester = stepFocusRequester
    )
}

// Phase 2 - Keyboard
@Composable
private fun Step2Keyboard(
    isClickedYet: Boolean,
    warningDesc: String,
    stepFocusRequester: FocusRequester? = null,
    onActionClick: () -> Unit,
    onNext: () -> Unit,
    onShowWarning: (String) -> Unit
) {
    val context = LocalContext.current
    SetupStepLayout(
        stepNumber = 2,
        iconRes = R.drawable.onboarding_phase2_enablebraillifykeyboard_mi_24px,
        title = AppStrings.get(context, "onb_s2_title"),
        description = AppStrings.get(context, "onb_s2_desc"),
        infoText = AppStrings.get(context, "onb_s2_info"),
        primaryButtonText = AppStrings.get(context, "onb_open_settings"),
        onPrimaryClick = onActionClick,
        secondaryButtonText = AppStrings.get(context, "onb_ive_enabled"),
        onSecondaryClick = {
            if (!isClickedYet) onShowWarning(AppStrings.get(context, "onb_s2_warn"))
            else onNext()
        },
        warningDesc = warningDesc,
        stepFocusRequester = stepFocusRequester
    )
}

// Phase 3 - Calibration
@Composable
private fun Step3Calibration(
    stepFocusRequester: FocusRequester? = null,
    onActionClick: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    SetupStepLayout(
        stepNumber = 3,
        iconRes = R.drawable.onboarding_phase3_calibratetouchpoints_mi_24px,
        title = AppStrings.get(context, "onb_s3_title"),
        description = AppStrings.get(context, "onb_s3_desc"),
        infoText = AppStrings.get(context, "onb_s3_info"),
        primaryButtonText = AppStrings.get(context, "onb_s3_btn"),
        onPrimaryClick = onActionClick,
        secondaryButtonText = AppStrings.get(context, "onb_do_later"),
        onSecondaryClick = onNext,
        warningDesc = "",
        stepFocusRequester = stepFocusRequester
    )
}

// Phase 4 - Emergency Contact
@Composable
private fun Step4Emergency(
    stepFocusRequester: FocusRequester? = null,
    onOpenEmergency: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    SetupStepLayout(
        stepNumber = 4,
        iconRes = R.drawable.onboarding_phase4_emergencycontact_mi_24px,
        title = AppStrings.get(context, "onb_s4_title"),
        description = AppStrings.get(context, "onb_s4_desc"),
        infoText = AppStrings.get(context, "onb_s4_info"),
        primaryButtonText = AppStrings.get(context, "onb_s4_btn"),
        onPrimaryClick = onOpenEmergency,
        secondaryButtonText = AppStrings.get(context, "onb_skip"),
        onSecondaryClick = onNext,
        warningDesc = "",
        stepFocusRequester = stepFocusRequester
    )
}

// Shared Layout for Setup Steps
@Composable
private fun SetupStepLayout(
    stepNumber: Int,
    iconRes: Int,
    title: String,
    description: String,
    infoText: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryClick: () -> Unit,
    warningDesc: String,
    stepFocusRequester: FocusRequester? = null
) {
    val context = LocalContext.current
    val stepText = String.format(AppStrings.get(context, "onb_step"), stepNumber)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(PurpleLight, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stepText,
            color = TextDark,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .then(if (stepFocusRequester != null) Modifier.focusRequester(stepFocusRequester) else Modifier)
                .focusable()
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ACardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = description,
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Background),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.global_info_icon_24px),
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = infoText,
                            color = TextDark,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (warningDesc.isNotEmpty()) {
            Text(
                text = warningDesc,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(primaryButtonText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onSecondaryClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Purple
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Purple)
        ) {
            Text(secondaryButtonText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))
    }
}