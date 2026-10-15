package com.example.braillify.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.Calibrate
import com.example.braillify.Keyboard

private val Background = Color(0xFFF2F0F5)
private val RowBackground = Color(0xFFEDEBF2)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)
private val IconBlack = Color(0xFF111111)

@Composable
fun ProfileScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val cal = remember { Calibrate() }

    var profiles by remember { mutableStateOf(ProfileManager.getProfiles(context)) }
    var activeProfileId by remember { mutableStateOf(ProfileManager.getActiveProfileId(context)) }

    var hiddenText by remember { mutableStateOf("") }

    LaunchedEffect(Keyboard.isCalibrating) {
        profiles = ProfileManager.getProfiles(context)
        activeProfileId = ProfileManager.getActiveProfileId(context)
    }

    // Hidden TextBox for pulling up the keyboard for calibration
    Box(
        modifier = Modifier
            .size(1.dp)
            .graphicsLayer { alpha = 0f }
            .semantics { hideFromAccessibility() }
    ) {
        OutlinedTextField(
            value = hiddenText,
            onValueChange = { hiddenText = it },
            modifier = Modifier.focusRequester(focusRequester)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
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
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "‹", color = TextDark, fontSize = 28.sp)
            }

            Spacer(Modifier.weight(1f))

            Text(
                text = "Profile",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            // Balance the back arrow
            Spacer(Modifier.size(32.dp))
        }

        Spacer(Modifier.height(24.dp))

        // Profiles list
        profiles.forEach { profile ->
            val isActive = (profile.id == activeProfileId)
            ProfileRow(
                title = profile.name,
                subtitle = if (isActive) "Active" else "Tap to activate",
                isActive = isActive,
                canDelete = (!isActive && profiles.size > 1),
                onDelete = {
                    ProfileManager.deleteProfile(context, profile.id)
                    profiles = ProfileManager.getProfiles(context)
                    activeProfileId = ProfileManager.getActiveProfileId(context)
                },
                onClick = {
                    if (!isActive) {
                        ProfileManager.setActiveProfileId(context, profile.id)
                        activeProfileId = profile.id
                        cal.pullCalibratedData(context, isLandscape, profile.id)
                        Log.d("ProfileScreen", "Activated profile: ${profile.name} (${profile.id})")
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
        }

        // Add Profile (Automatically defaults to "Profile x" where x is the number and starts calibration)
        ProfileRow(
            title = "Add Profile",
            subtitle = "Create and calibrate new profile",
            isAdd = true,
            onClick = {
                var nextNum = profiles.size + 1
                while (profiles.any { it.name == "Profile $nextNum" }) {
                    nextNum++
                }
                val profileName = "Profile $nextNum"
                val createdProfile = ProfileManager.addProfile(context, profileName)
                ProfileManager.setActiveProfileId(context, createdProfile.id)

                profiles = ProfileManager.getProfiles(context)
                activeProfileId = createdProfile.id

                // Start calibration immediately for this new profile
                cal.calibrateNew(createdProfile.id)
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        )
    }
}

@Composable
private fun ProfileRow(
    title: String,
    subtitle: String? = null,
    isAdd: Boolean = false,
    isActive: Boolean = false,
    canDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFE8E3FB) else RowBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon placeholder
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (isActive) Color(0xFF5B3FE4) else IconBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isAdd) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.White, CircleShape)
                        )
                        Spacer(Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 20.dp, height = 10.dp)
                                .background(Color.White, RoundedCornerShape(6.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.size(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = if (isActive) Color(0xFF5B3FE4) else TextGray,
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            if (canDelete && onDelete != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = TextGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    ProfileScreen()
}