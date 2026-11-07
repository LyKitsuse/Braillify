package com.example.braillify.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    var showSOS by remember { mutableStateOf(false) }

    BackHandler(enabled = showVoiceSpeed || showVoiceVolume || showAbout || showSOS) {
        when {
            showVoiceSpeed  -> showVoiceSpeed = false
            showVoiceVolume -> showVoiceVolume = false
            showAbout       -> showAbout = false
            showSOS         -> showSOS = false
        }
    }

    var voiceSpeed by remember { mutableStateOf(SettingsPrefs.getVoiceSpeed(context)) }
    var voiceVolume by remember { mutableFloatStateOf(SettingsPrefs.getVoiceVolume(context)) }

    var landscapeTyping by remember { mutableStateOf(SettingsPrefs.getLandscapeTyping(context)) }
    var sequentialMode by remember { mutableStateOf(SettingsPrefs.getSequentialMode(context)) }

    var hapticOn by remember { mutableStateOf(SettingsPrefs.getHapticOn(context)) }

    onSubScreenChanged(showVoiceSpeed || showVoiceVolume || showAbout || showSOS)

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
        showSOS   -> SOSSubScreen(onBack = { showSOS = false })
        else -> SettingsMain(
            onBack = onBack,
            voiceSpeed = voiceSpeed,
            voiceVolume = voiceVolume,
            hapticOn = hapticOn,
            landscapeTyping = landscapeTyping,
            sequentialMode = sequentialMode,
            onHapticChanged = {
                hapticOn = it
                SettingsPrefs.setHapticOn(context, it)
            },
            onLandscapeTypingChanged = {
                landscapeTyping = it
                SettingsPrefs.setLandscapeTyping(context, it)
            },
            onSequentialModeChanged = {
                sequentialMode = it
                SettingsPrefs.setSequentialMode(context, it)
            },
            onVoiceSpeed = { showVoiceSpeed = true },
            onVoiceVolume = { showVoiceVolume = true },
            onAboutClick = { showAbout = true },
            onSOSClick = { showSOS = true }
        )
    }
}

@Composable
private fun SettingsMain(
    onBack: () -> Unit,
    voiceSpeed: String,
    voiceVolume: Float,
    hapticOn: Boolean,
    landscapeTyping: Boolean,
    sequentialMode: Boolean,
    onHapticChanged: (Boolean) -> Unit,
    onLandscapeTypingChanged: (Boolean) -> Unit,
    onSequentialModeChanged: (Boolean) -> Unit,
    onVoiceSpeed: () -> Unit,
    onVoiceVolume: () -> Unit,
    onAboutClick: () -> Unit,
    onSOSClick: () -> Unit
) {
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }
    var showInputModeDialog by remember { mutableStateOf(false) }

    var currentLanguage by remember { mutableStateOf(SettingsPrefs.getLanguage(context)) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    // Sticky header
    val scrollState = rememberScrollState()
    val isScrolled = scrollState.value > 80

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Main Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Regular Header
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
                    Text(
                        text = "‹",
                        color = TextDark,
                        fontSize = 28.sp,
                        modifier = Modifier.semantics {
                            contentDescription = "Back Button"
                        })
                }
                Spacer(Modifier.size(8.dp))
                Text(
                    text = AppStrings.get(context, "set_title"),
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.graphicsLayer {

                        alpha = if (isScrolled) 0f else 1f
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle(AppStrings.get(context, "set_voice_assistance"))
            Spacer(Modifier.height(8.dp))

            SettingsRow(
                title = AppStrings.get(context, "set_voice_speed"),
                subtitle = AppStrings.get(context, "vs_" + voiceSpeed.lowercase().replace(" ", "")),
                onClick = onVoiceSpeed
            )
            Spacer(Modifier.height(10.dp))
            SettingsRow(
                title = AppStrings.get(context, "set_voice_volume"),
                subtitle = "${(voiceVolume * 100).toInt()}%",
                onClick = onVoiceVolume
            )

            Spacer(Modifier.height(24.dp))
            SectionTitle(AppStrings.get(context, "set_keyboard_prefs"))
            Spacer(Modifier.height(8.dp))

            SwitchRow(
                title = AppStrings.get(context, "set_landscape"),
                subtitle = AppStrings.get(context, "set_landscape_desc"),
                checked = landscapeTyping,
                onCheckedChange = onLandscapeTypingChanged
            )

            Spacer(Modifier.height(10.dp))

            SettingsRow(
                title = AppStrings.get(context, "set_input_mode"),
                subtitle = if (sequentialMode) AppStrings.get(
                    context,
                    "set_input_sequential"
                ) else AppStrings.get(context, "set_input_simultaneous"),
                onClick = {
                    Log.d("DEBUG", "Input Mode clicked, setting dialog true")
                    showInputModeDialog = true
                }
            )

            Spacer(Modifier.height(24.dp))
            SectionTitle(AppStrings.get(context, "set_emergency"))
            Spacer(Modifier.height(8.dp))

            SettingsRow(
                title = AppStrings.get(context, "set_safety_feature"),
                subtitle = AppStrings.get(context, "set_safety_desc"),
                onClick = onSOSClick
            )

            Spacer(Modifier.height(24.dp))
            SectionTitle(AppStrings.get(context, "set_app_settings"))
            Spacer(Modifier.height(8.dp))

            // Language
            SettingsRow(
                title = AppStrings.get(context, "set_language"),
                subtitle = if (currentLanguage == "Tagalog") AppStrings.get(
                    context,
                    "set_tagalog"
                ) else AppStrings.get(context, "set_english"),
                onClick = { showLanguageDialog = true }
            )
            Spacer(Modifier.height(10.dp))

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
                        text = AppStrings.get(context, "set_haptic"),
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
                title = AppStrings.get(context, "set_about"),
                onClick = onAboutClick
            )
            Spacer(Modifier.height(10.dp))

            SettingsRow(
                title = AppStrings.get(context, "set_exit"),
                trailingIconRes = R.drawable.settings_4ap_4_exit_24px,
                onClick = { showExitDialog = true }
            )

        }

        // Header
        AnimatedVisibility(
            visible = isScrolled,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background.copy(alpha = 0.95f))
                    .padding(top = 24.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = AppStrings.get(context, "set_title"),
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Exit Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = AppStrings.get(context, "dialog_exit_title"),
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = AppStrings.get(context, "dialog_exit_desc"),
                    color = TextGray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    Log.d("DEBUG", "Exit Braillify confirmed")
                    (context as? android.app.Activity)?.finish()
                }) {
                    Text(text = AppStrings.get(context, "dialog_exit_confirm"), color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = AppStrings.get(context, "dialog_cancel"), color = TextGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Input Mode Dialog
    if (showInputModeDialog) {
        AlertDialog(
            onDismissRequest = { showInputModeDialog = false },
            title = {
                Text(
                    text = AppStrings.get(context, "dialog_input_title"),
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    RadioOptionRow(
                        label = AppStrings.get(context, "set_input_simultaneous"),
                        selected = !sequentialMode,
                        onClick = {
                            onSequentialModeChanged(false)
                            showInputModeDialog = false
                        }
                    )
                    RadioOptionRow(
                        label = AppStrings.get(context, "set_input_sequential"),
                        selected = sequentialMode,
                        onClick = {
                            onSequentialModeChanged(true)
                            showInputModeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInputModeDialog = false }) {
                    Text(text = AppStrings.get(context, "dialog_cancel"), color = TextGray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Language Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Text(
                    text = AppStrings.get(context, "dialog_lang_title"),
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    RadioOptionRow(
                        label = AppStrings.get(context, "set_english"),
                        selected = currentLanguage == "English",
                        onClick = {
                            currentLanguage = "English"
                            SettingsPrefs.setLanguage(context, "English")
                            showLanguageDialog = false
                        }
                    )
                    RadioOptionRow(
                        label = AppStrings.get(context, "set_tagalog"),
                        selected = currentLanguage == "Tagalog",
                        onClick = {
                            currentLanguage = "Tagalog"
                            SettingsPrefs.setLanguage(context, "Tagalog")
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(text = AppStrings.get(context, "dialog_cancel"), color = TextGray)
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

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Purple,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}

@Composable
private fun SliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = RowBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Purple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(4.dp))
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Purple,
                    inactiveTrackColor = PurpleLight
                )
            )
        }
    }
}

@Composable
private fun RadioOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Purple,
                unselectedColor = Color(0xFFBDBDBD)
            )
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = label,
            color = TextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    SettingsScreen()
}