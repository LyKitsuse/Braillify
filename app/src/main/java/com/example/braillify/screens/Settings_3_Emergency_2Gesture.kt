package com.example.braillify.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
private val ACardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)
private val RadioUnselected = Color(0xFFBDBDBD)

@Composable
fun SOSGestureScreen(
    onBack: () -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    val context = LocalContext.current

    // We keep the saved English value so it doesn't break the SOS detection logic,
    // but we display the translated version in the UI.
    val option0TitleRaw = "Tap 6 Dots 5 times, and Then Swipe Up then Swipe Down"
    val option1TitleRaw = "Triple Tap, Swipe, and Then Triple Tap"

    val option0TitleDisplay = AppStrings.get(context, "sg_opt0_title")
    val option0Subtitle = AppStrings.get(context, "sg_opt0_sub")
    val option1TitleDisplay = AppStrings.get(context, "sg_opt1_title")
    val option1Subtitle = AppStrings.get(context, "sg_opt1_sub")

    val currentSaved = remember { SettingsPrefs.getSOSGesture(context) }
    val initialSelection = if (currentSaved == option1TitleRaw) 1 else 0

    var selectedOption by remember { mutableIntStateOf(initialSelection) }

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
                text = AppStrings.get(context, "sg_title"),
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // Hero Icon
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PurpleLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_3emergency_3safetygesture_mi_24px),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Title & Subtitle
        Text(
            text = AppStrings.get(context, "sg_hero_title"),
            color = TextDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = AppStrings.get(context, "sg_hero_desc"),
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(32.dp))

        // Options
        GestureOption(
            title = option0TitleDisplay,
            subtitle = option0Subtitle,
            isSelected = selectedOption == 0,
            onClick = { selectedOption = 0 }
        )

        Spacer(Modifier.height(12.dp))

        GestureOption(
            title = option1TitleDisplay,
            subtitle = option1Subtitle,
            isSelected = selectedOption == 1,
            onClick = { selectedOption = 1 }
        )

        Spacer(Modifier.height(24.dp))

        // Save Button
        Button(
            onClick = {
                val selectedTitle = if (selectedOption == 1) option1TitleRaw else option0TitleRaw
                onSave(selectedTitle)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Purple)
        ) {
            Text(AppStrings.get(context, "sg_save"), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GestureOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Purple else Color.Transparent
    val backgroundColor = if (isSelected) PurpleLight.copy(alpha = 0.5f) else ACardWhite

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Purple else RadioUnselected,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Purple, CircleShape)
                    )
                }
            }

            Spacer(Modifier.size(16.dp))

            Column {
                if (title.isNotEmpty()) {
                    Text(
                        text = title,
                        color = TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (subtitle.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SOSGesturePreview() {
    SOSGestureScreen()
}