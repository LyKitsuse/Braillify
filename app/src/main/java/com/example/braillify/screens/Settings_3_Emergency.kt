package com.example.braillify.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun SOSSubScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    var sosEnabled by remember { mutableStateOf(true) }

    var showEmergencyContact by remember { mutableStateOf(false) }
    var showSOSGesture by remember { mutableStateOf(false) }

    var savedNumber by remember { mutableStateOf(SettingsPrefs.getEmergencyNumber(context)) }
    var savedGesture by remember { mutableStateOf(SettingsPrefs.getSOSGesture(context)) }

    LaunchedEffect(showEmergencyContact, showSOSGesture) {
        if (!showEmergencyContact) {
            savedNumber = SettingsPrefs.getEmergencyNumber(context)
        }
        if (!showSOSGesture) {
            savedGesture = SettingsPrefs.getSOSGesture(context)
        }
    }

    if (showEmergencyContact) {
        EmergencyContactScreen(
            onBack = { showEmergencyContact = false }
        )
        return
    }

    if (showSOSGesture) {
        SOSGestureScreen(
            onBack = { showSOSGesture = false },
            onSave = { newGesture ->
                SettingsPrefs.setSOSGesture(context, newGesture)
                savedGesture = newGesture
                showSOSGesture = false
            }
        )
        return
    }

    BackHandler(enabled = true) { onBack() }

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_3emergency_0_icon_si_24px),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = AppStrings.get(context, "sos_title"),
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))

            // Empty space to balance the back button
            Spacer(Modifier.size(32.dp))
        }

        Spacer(Modifier.height(16.dp))

        // Hero card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_3emergency_1_icon_mi_24px),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = AppStrings.get(context, "sos_hero_title"),
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = AppStrings.get(context, "sos_hero_desc"),
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Emergency Contact
        SOSRow(
            iconRes = R.drawable.settings_3emergency_2_0emergencycontact_mi_24px,
            title = AppStrings.get(context, "sos_contact_row"),
            subtitle = savedNumber,
            onClick = { showEmergencyContact = true }
        )

        Spacer(Modifier.height(10.dp))

        // Safety Gesture row
        SOSRow(
            iconRes = R.drawable.settings_3emergency_3safetygesture_mi_24px,
            title = AppStrings.get(context, "sos_gesture_row"),
            subtitle = savedGesture,
            onClick = { showSOSGesture = true }
        )

        Spacer(Modifier.height(10.dp))

        // Enable SOS row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = RowBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PurpleLight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings_3emergency_4enablesafetygesture_mi_24px),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.get(context, "sos_enable"),
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = AppStrings.get(context, "sos_enable_sub"),
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = sosEnabled,
                    onCheckedChange = { sosEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Purple,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFBDBDBD)
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier.size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.global_info_icon_24px),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    text = AppStrings.get(context, "sos_info"),
                    color = TextDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SOSRow(
    iconRes: Int,
    title: String,
    subtitle: String,
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
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
            Text(text = "›", color = TextGray, fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SOSSubScreenPreview() {
    SOSSubScreen()
}