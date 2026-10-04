package com.example.braillify.screens

import android.R.attr.accessibilityHeading
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.Calibrate
import android.content.Intent
import android.provider.Settings

class Onboarding {
    val cardTextPadding = PaddingValues(
        top = 24.dp,
        start = 24.dp,
        end = 24.dp,
        bottom = 0.dp
    )
    private val subText: Color = Color(0xff8d8f84)
    private val cardBackground: Color = Color(0xfff2efeb)

    val subTextFont = 13.sp
    val mainTextFont = 16.sp

    @Preview(showBackground = true)
    @Composable
    fun OnboardingProcess(onFinished: () -> Unit = {}) {
        var stepsAchieved by remember { mutableStateOf(0) }
        var warningDesc by remember { mutableStateOf("") }
        var isClickedYet by remember { mutableStateOf(false) }
        val context = LocalContext.current

        val messageInstruction: String
        val messageDescription: String
        val interactableButton: String

        var text by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(stepsAchieved) {
            isClickedYet = false
            warningDesc = ""
        }

        when (stepsAchieved) {
            0 -> {
                messageInstruction = "Step 1: Enable Talkback Shortcut"
                messageDescription = "TalkBack touch gestures can interfere with multi-finger Braille typing. Enabling the TalkBack Shortcut lets you quickly toggle TalkBack off while typing and turn it back on when you're done."
                interactableButton = "Enable Service"
            }
            1 -> {
                messageInstruction = "Step 2: Enable Braillify Keyboard"
                messageDescription = "Enable Braillify in your keyboard settings to start typing in Braille."
                interactableButton = "Enable Braillify in Settings"
            }
            else -> {
                messageInstruction = "Step 3: Calibrate Your Touchpoints"
                messageDescription = "Tap six points on the screen to align the Braille keys with where your fingers naturally rest."
                interactableButton = "Start Calibration"
            }
        }

        Log.d("DEBUG", "Entered Onboarding, step: $stepsAchieved")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Hidden TextBox for pulling up the keyboard from navbar or anywhere
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                welcomeMessage()
                infoCard(messageInstruction, messageDescription)
                buttonInteract(
                    buttDesc = interactableButton,
                    stepsAchieved = stepsAchieved,
                    context = context,
                    focusRequester = focusRequester
                ) {
                    isClickedYet = true
                }
                continueInteract(
                    currentWarningDesc = warningDesc,
                    stepsAchieved = stepsAchieved,
                    isClickedYet = isClickedYet,
                    onShowWarning = { warningDesc = it }
                ) {
                    Log.d("DEBUG", "Clicked Continue, step: $stepsAchieved")
                    if (stepsAchieved < 2) {
                        stepsAchieved++
                    } else {
                        onFinished()
                    }
                }
            }
        }
    }

    @Composable
    fun welcomeMessage() {
        Column(
            Modifier
                .padding(cardTextPadding)
                .fillMaxWidth()
        ) {
            Text(
                text = "Braillify",
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(bottom = 24.dp)
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background(cardBackground)
            ) {
                Text(
                    text = "Welcome to Braillify",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = mainTextFont,
                    modifier = Modifier
                        .padding(cardTextPadding)
                )
                Text(
                    text = "Braillify is a mobile application designed to help visually impaired users read and write using Braille, with the support of voice assistance and machine learning technology.",
                    color = subText,
                    fontSize = subTextFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(cardTextPadding)
                )
                Text(
                    text = "Before we get started. Follow the steps below.",
                    color = subText,
                    fontWeight = FontWeight.Bold,
                    fontSize = subTextFont,
                    modifier = Modifier
                        .padding(
                            top = 24.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        )
                )
            }
        }
    }

    @Composable
    fun infoCard(messInstruction: String, messDesc: String, ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(cardTextPadding)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(cardBackground)
        ) {
            Text(
                text = messInstruction,
                color = Color.Black,
                fontSize = mainTextFont,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(cardTextPadding)
            )
            Text(
                text = messDesc,
                color = subText,
                fontWeight = FontWeight.Bold,
                fontSize = subTextFont,
                modifier = Modifier
                    .padding(
                        top = 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
            )
        }
    }

    @Composable
    fun buttonInteract(buttDesc: String,stepsAchieved: Int,context: Context,focusRequester: FocusRequester,onClickAction: () -> Unit) {
        val keyboardController = LocalSoftwareKeyboardController.current

        Button(
            onClick = {
                Log.d("DEBUG", "Clicked Action Button for step $stepsAchieved")
                when (stepsAchieved) {
                    0 -> {
                        try {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("DEBUG", "Error opening accessibility settings", e)
                        }
                    }
                    1 -> {
                        try {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("DEBUG", "Error opening accessibility settings", e)
                        }
                    }
                    else -> {
                        Log.d("DEBUG", "Calibration Clicked")
                        Calibrate().calibrateNew()

                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
                onClickAction()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B3FE4)),
            modifier = Modifier
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 0.dp
                )
                .fillMaxWidth(),
        ) {
            Text(
                text = buttDesc,
                Modifier.padding(10.dp)
            )
        }
    }

    @Composable
    fun continueInteract(currentWarningDesc: String,stepsAchieved: Int,isClickedYet: Boolean,onShowWarning: (String) -> Unit,onContinueClick: () -> Unit) {
        Column {
            Text(
                text = currentWarningDesc,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(
                        top = 12.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 0.dp
                    )
            )
            ElevatedButton(
                onClick = {
                    if (!isClickedYet) {
                        when (stepsAchieved) {
                            0 -> {
                                onShowWarning("Please enable accessibility service to continue.")
                            }
                            1 -> {
                                onShowWarning("Please enable Braillify Keyboard on Settings.")
                            }
                            else -> {
                                onShowWarning("")
                            }
                        }
                    } else {
                        onContinueClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = cardBackground,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 6.dp,
                        bottom = 12.dp
                    )
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Continue",
                    Modifier.padding(10.dp)
                )
            }
        }
    }
}
