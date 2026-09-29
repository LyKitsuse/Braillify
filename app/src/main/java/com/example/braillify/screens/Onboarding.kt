package com.example.braillify.screens

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Onboarding {
    val cardTextPadding = PaddingValues(
        top = 24.dp,
        start = 24.dp,
        end = 24.dp,
        bottom = 0.dp // Optional, defaults to 0.dp
    )
    val subText: Color = Color(0xff8d8f84)
    val cardBackground: Color = Color(0xfff2efeb)

    var stepsAchieved: Int = 0;

    // Calibrate Btn
    @Preview(showBackground = true)
    @Composable
    fun OnboardingProcess() {
        // Main Screen
        Log.d("DEBUG", "Entered Onboarding")
        Box{
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            )
            {
                welcomeMessage()
                infoCard()
                buttonInteract()
                continueInteract()
            }

        }

    }

    @Composable
    fun welcomeMessage() {
        Column (
            Modifier
                .padding(cardTextPadding)
                .fillMaxWidth()
        ) {
            Text(
                text = "Braillify",
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 24.dp)
            )
            Column (
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
                    modifier = Modifier
                        .padding(cardTextPadding)
                )
                Text(
                    text = "Braillify is a mobile application designed to help visually impaired users read and write using Braille, with the support of voice assistance and machine learning technology.",
                    color = subText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(cardTextPadding)
                )
                Text(
                    text = "Before we get started. Follow the steps below.",
                    color = subText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 24.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 24.dp
                        )
                )
            }
        }

    }

    @Composable
    fun infoCard() {
        // Changes info after step, there are three steps
        Column (
            Modifier
                .fillMaxWidth()
                .padding(cardTextPadding)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(cardBackground)
        ) {
            Text(
                text = "Message Instruction",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(cardTextPadding)
            )
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                color = subText,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 24.dp,
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 24.dp
                    )
            )
        }
    }

    @Composable
    fun buttonInteract() {
        Button(
            onClick = {
                Log.d("DEBUG", "Clicked Action Button")
            },
            modifier = Modifier
                .padding(cardTextPadding)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Null Button"
            )
        }
    }

    @Composable
    fun continueInteract() {
        ElevatedButton(
            onClick = {
                Log.d("DEBUG", "Clicked Continue")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = cardBackground,      // Button background color
                contentColor = Color.Black        // Text/Icon color inside button
            ),
            modifier = Modifier
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
                .fillMaxWidth()
        ) {
            Text("Continue")
        }
    }

}