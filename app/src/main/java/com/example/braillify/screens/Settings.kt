package com.example.braillify.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.R

private val Purple = Color(0xFF5B3FE4)
private val PurpleLight = Color(0xFFE8E3FB)
private val Background = Color(0xFFF2F0F5)
private val RowBackground = Color(0xFFEDEBF2)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)

@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onSubScreenChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    var showVoiceSpeed by remember { mutableStateOf(false) }
    var showVoiceVolume by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }

    // Load once from prefs; the lambda runs only on first composition
    var voiceSpeed by remember { mutableStateOf(SettingsPrefs.getVoiceSpeed(context)) }
    var voiceVolume by remember { mutableFloatStateOf(SettingsPrefs.getVoiceVolume(context)) }
    var hapticOn by remember { mutableStateOf(SettingsPrefs.getHapticOn(context)) }

    onSubScreenChanged(showVoiceSpeed || showVoiceVolume || showAbout)

    when {
        showVoiceSpeed -> VoiceSpeedScreen(
            onBack = { showVoiceSpeed = false },
            initialSpeed = voiceSpeed,
            onSpeedChanged = {
                voiceSpeed = it
                SettingsPrefs.setVoiceSpeed(context, it)
            }
        )
        showVoiceVolume -> VoiceVolumeScreen(
            onBack = { showVoiceVolume = false },
            initialVolume = voiceVolume,
            onVolumeChanged = {
                voiceVolume = it
                SettingsPrefs.setVoiceVolume(context, it)
            }
        )
        showAbout -> AboutBraillifyScreen(onBack = { showAbout = false })
        else -> SettingsMain(
            onBack = onBack,
            voiceSpeed = voiceSpeed,
            voiceVolume = voiceVolume,
            hapticOn = hapticOn,
            onHapticChanged = {
                hapticOn = it
                SettingsPrefs.setHapticOn(context, it)
            },
            onVoiceSpeed = { showVoiceSpeed = true },
            onVoiceVolume = { showVoiceVolume = true },
            onAboutClick = { showAbout = true }
        )
    }
}

@Composable
private fun SettingsMain(
    onBack: () -> Unit,
    voiceSpeed: String,
    voiceVolume: Float,
    hapticOn: Boolean,
    onHapticChanged: (Boolean) -> Unit,
    onVoiceSpeed: () -> Unit,
    onVoiceVolume: () -> Unit,
    onAboutClick: () -> Unit
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

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
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Settings",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))
        SectionTitle("Voice Assistance")
        Spacer(Modifier.height(8.dp))

        SettingsRow(
            title = "Voice Speed",
            subtitle = voiceSpeed,
            onClick = onVoiceSpeed
        )
        Spacer(Modifier.height(10.dp))
        SettingsRow(
            title = "Voice Volume",
            subtitle = "${(voiceVolume * 100).toInt()}%",
            onClick = onVoiceVolume
        )

        Spacer(Modifier.height(24.dp))
        SectionTitle("App Settings")
        Spacer(Modifier.height(8.dp))

        // Haptic Feedback
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RowBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Haptic Feedback",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = hapticOn,
                    onCheckedChange = onHapticChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Purple,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFBDBDBD)
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        SettingsRow(
            title = "About Braillify",
            onClick = onAboutClick
        )
        Spacer(Modifier.height(10.dp))

        SettingsRow(
            title = "Exit Braillify",
            trailingIconRes = R.drawable.st_exit_24px,
            onClick = { showExitDialog = true }
        )
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "Exit Braillify?",
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to exit the app?",
                    color = TextGray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    Log.d("DEBUG", "Exit Braillify confirmed")
                    (context as? android.app.Activity)?.finish()
                }) {
                    Text(text = "Exit", color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = "Cancel", color = TextGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextDark,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String? = null,
    iconRes: Int? = null,
    trailingIconRes: Int? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RowBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (iconRes != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PurpleLight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            if (trailingIconRes != null) {
                Icon(
                    painter = painterResource(id = trailingIconRes),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(text = "›", color = TextGray, fontSize = 20.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen()
}