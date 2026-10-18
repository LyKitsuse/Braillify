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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.R

private val Purple = Color(0xFF5B3FE4)
private val PurpleLight = Color(0xFFE8E3FB)
private val Background = Color(0xFFF2F0F5)
private val ACardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)

// Formats the raw 10 digits to look nice
private fun formatPhoneNumber(raw: String): String {
    if (raw.isEmpty()) return "+63 XXX XXX XXXX"
    if (raw.length <= 3) return "+63 $raw"
    if (raw.length <= 6) return "+63 ${raw.substring(0, 3)} ${raw.substring(3)}"
    return "+63 ${raw.substring(0, 3)} ${raw.substring(3, 6)} ${raw.substring(6)}"
}

// Strips ALL non-digits, drops any leading 63, and keeps exactly 10 digits
//Not sure if 10 digits are the exact numbers for all sim; at least for me it is 10 digits
private fun cleanRawNumber(input: String): String {
    val digits = input.filter { it.isDigit() }
    // If the number starts with 63 and is 12 digits long
    val withoutCode = if (digits.startsWith("63") && digits.length == 12) digits.substring(2) else digits
    return withoutCode.take(10)
}

@Composable
fun EmergencyContactScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current

    // Load and immediately clean the saved string so it only holds 10 digits max
    val savedRaw = remember {
        cleanRawNumber(SettingsPrefs.getEmergencyNumber(context))
    }

    var rawPhoneNumber by remember { mutableStateOf(savedRaw) }
    var showEditDialog by remember { mutableStateOf(false) }
    var tempPhoneNumber by remember { mutableStateOf("") }

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
            Text(
                text = "Emergency Contact",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.emergency_top_right_24px),
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Hero Icon
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(PurpleLight, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.contact_emergency_24px),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Title & Subtitle
        Text(
            text = "Emergency Contact",
            color = TextDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "This number will be called when SOS is activated.",
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Phone Number Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    tempPhoneNumber = rawPhoneNumber
                    showEditDialog = true
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ACardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.phone_number_24px),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Phone Number",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatPhoneNumber(rawPhoneNumber),
                        color = if (rawPhoneNumber.isEmpty()) TextGray else TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.edit_pencil_24px),
                    contentDescription = "Edit Phone Number",
                    tint = Purple,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            tempPhoneNumber = rawPhoneNumber
                            showEditDialog = true
                        }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                // Save the raw 10 digits directly
                SettingsPrefs.setEmergencyNumber(context, rawPhoneNumber)
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(
                text = "Save",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(24.dp))

        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ab_top_right_icon_24px),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "Make sure this is a trusted and reachable contact.",
                    color = TextDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }

    // Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Phone Number",
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = tempPhoneNumber,
                    onValueChange = { input ->
                        val digitsOnly = input.filter { it.isDigit() }
                        if (digitsOnly.length <= 10) {
                            tempPhoneNumber = digitsOnly
                        }
                    },
                    placeholder = { Text("9123456789", color = TextGray) },
                    label = { Text("Phone Number (10 digits)") },
                    prefix = { Text("+63 ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    rawPhoneNumber = tempPhoneNumber
                    showEditDialog = false
                }) {
                    Text("Confirm", color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyContactPreview() {
    EmergencyContactScreen()
}