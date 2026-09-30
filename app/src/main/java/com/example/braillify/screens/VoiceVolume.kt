package com.example.braillify.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private fun volumeIconFor(volume: Float): Int = when {
    volume <= 0.35f -> R.drawable.vv_volume_low_24px
    volume <= 0.75f -> R.drawable.vv_volume_medium_24px
    else -> R.drawable.vv_volume_high_24px
}

@Composable
fun VoiceVolumeScreen(
    onBack: () -> Unit = {},
    initialVolume: Float = 1.0f,
    onVolumeChanged: (Float) -> Unit = {}
) {
    var volume by remember { mutableFloatStateOf(initialVolume) }
    val percent = (volume * 100).toInt()

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
                text = "Voice Volume",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.vv_top_right_icon_24px),
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Hero card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RowBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(PurpleLight, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = volumeIconFor(volume)),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "$percent%",
                    color = TextDark,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Current volume",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Slider row with low/high icons on each end
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.vv_volume_low_24px),
                contentDescription = "Low volume",
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
            Slider(
                value = volume,
                onValueChange = {
                    volume = it
                    onVolumeChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Purple,
                    inactiveTrackColor = PurpleLight
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.vv_volume_high_24px),
                contentDescription = "High volume",
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Preset buttons with matching icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PresetButton(
                modifier = Modifier.weight(1f),
                title = "Low",
                subtitle = "25%",
                iconRes = R.drawable.vv_volume_low_24px,
                onClick = {
                    volume = 0.25f
                    onVolumeChanged(0.25f)
                }
            )
            PresetButton(
                modifier = Modifier.weight(1f),
                title = "Medium",
                subtitle = "50%",
                iconRes = R.drawable.vv_volume_medium_24px,
                onClick = {
                    volume = 0.50f
                    onVolumeChanged(0.50f)
                }
            )
            PresetButton(
                modifier = Modifier.weight(1f),
                title = "High",
                subtitle = "100%",
                iconRes = R.drawable.vv_volume_high_24px,
                onClick = {
                    volume = 1.0f
                    onVolumeChanged(1.0f)
                }
            )
        }
    }
}

@Composable
private fun PresetButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RowBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(PurpleLight, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(200.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextGray,
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VoiceVolumePreview() {
    VoiceVolumeScreen()
}