package com.example.braillify.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.Calibrate

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
        val context = LocalContext.current

        val messageInstruction: String
        val messageDescription: String
        val interactableButton: String
        val warningDesc: String

        when (stepsAchieved) {
            0 -> {
                messageInstruction = "Step 1: Enable Accessibility Service"
                messageDescription = "Braillify requires the Braille Accessibility Service to assist with reading and typing Braille across your device."
                interactableButton = "Enable Service"
                warningDesc = "Please enable accessibility service to continue."
            }
            1 -> {
                messageInstruction = "Step 2: Calibrate Touch Points"
                messageDescription = "Calibrate your screen touch points to ensure accurate Braille dot detection and typing precision."
                interactableButton = "Calibrate Now"
                warningDesc = "Please complete calibration to continue."
            }
            else -> {
                messageInstruction = "Step 3: Voice & Haptic Feedback"
                messageDescription = "Configure voice assistance, volume, and haptic feedback preferences for the best interactive experience."
                interactableButton = "Open Settings"
                warningDesc = "Almost done! Click Continue to finish setup."
            }
        }

        Log.d("DEBUG", "Entered Onboarding, step: $stepsAchieved")
        Box {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                welcomeMessage()
                infoCard(messageInstruction, messageDescription)
                buttonInteract(interactableButton, stepsAchieved, context)
                continueInteract(warningDesc) {
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
    fun infoCard(messInstruction: String, messDesc: String) {
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
    fun buttonInteract(buttDesc: String, stepsAchieved: Int, context: Context) {
        Button(
            onClick = {
                Log.d("DEBUG", "Clicked Action Button for step $stepsAchieved")
                when (stepsAchieved) {
                    0 -> {
                        try {
                            val intent = android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Log.e("DEBUG", "Error opening accessibility settings", e)
                        }
                    }
                    1 -> {
                        Calibrate().calibrateNew()
                        Log.d("DEBUG", "Calibration triggered")
                    }
                    else -> {
                        Log.d("DEBUG", "Settings action clicked")
                    }
                }
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
    fun continueInteract(warningDesc: String, onContinueClick: () -> Unit) {
        Column {
            Text(
                text = warningDesc,
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
                onClick = onContinueClick,
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
