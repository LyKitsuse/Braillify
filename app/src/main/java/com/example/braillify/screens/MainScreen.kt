package com.example.braillify.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.braillify.R
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFF5B3FE4)
private val PurpleLight = Color(0xFFE8E3FB)
private val Background = Color(0xFFF2F0F5)
private val CardWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)

class MainScreen {
    @Preview(showBackground = true)
    @Composable
    fun MainScreenUI() {
        var selectedTab by remember { mutableStateOf(NavTab.HOME) }
        var showNotes by remember { mutableStateOf(false) }
        var showPractice by remember { mutableStateOf(false) }
        var showProfile by remember { mutableStateOf(false) }
        var settingsSubOpen by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize().background(Background)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                when {
                    showPractice -> PracticeBrailleScreen(
                        onBack = { showPractice = false }
                    )

                    showNotes -> NotesScreen(
                        onBack = { showNotes = false },
                        onAddNote = { Log.d("DEBUG", "Add Note") },
                        onNoteClick = { Log.d("DEBUG", "Opened ${it.title}") }
                    )

                    showProfile -> ProfileScreen(
                        onBack = { showProfile = false }
                    )

                    //NAVBAR CONTENT
                    selectedTab == NavTab.HOME -> HomeContent(
                        onNotesClick = { showNotes = true },
                        onPracticeClick = { showPractice = true },
                        onProfileClick = { showProfile = true }
                    )

                    selectedTab == NavTab.KEYBOARD -> { /* TODO */
                    }

                    selectedTab == NavTab.SETTINGS -> SettingsScreen(
                        onBack = { selectedTab = NavTab.HOME },
                        onSubScreenChanged = { settingsSubOpen = it }
                    )
                }
            }

            // Hide NavBar on sub-screens (Notes, Practice, or Settings sub-screen)
            if (!showNotes && !showPractice && !settingsSubOpen) {
                NavBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        }
    }

    @Composable
    fun HomeContent(
        onPracticeClick: () -> Unit = {},
        onNotesClick: () -> Unit = {},
        onProfileClick: () -> Unit = {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Braillify",
                color = Purple,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.braillify_logo),
                        contentDescription = "Braillify Logo",
                        modifier = Modifier
                            .size(64.dp)
                            .background(PurpleLight, RoundedCornerShape(16.dp))
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Tap to Start",
                        color = TextDark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Open the Braille Keyboard and start typing.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = { Log.d("DEBUG", "Start tapped") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple)
                    ) {
                        Text(
                            text = "Start",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Practice Braille",
                    subtitle = "Practice Braille Code",
                    iconRes = R.drawable.ms_practice_braille_24px,
                    onClick = onPracticeClick
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Notes",
                    subtitle = "Saved Notes: 99",
                    iconRes = R.drawable.ms_notes_24px,
                    onClick = onNotesClick
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Recalibrate",
                    subtitle = "Adjust Tap Points",
                    iconRes = R.drawable.ms_recalibrate_24px,
                    onClick = { Log.d("DEBUG", "Recalibrate tapped") }
                )
                FeatureCard(
                    modifier = Modifier.weight(1f),
                    title = "Profile",
                    subtitle = "Go to Saved Profiles",
                    iconRes = R.drawable.ms_profile_24px,
                    onClick = onProfileClick
                )
            }
        }
    }

    @Composable
    fun FeatureCard(
        modifier: Modifier = Modifier,
        title: String,
        subtitle: String,
        iconRes: Int? = null,
        onClick: () -> Unit
    ) {
        Card(
            onClick = onClick,
            modifier = modifier.aspectRatio(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                //Call Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(PurpleLight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconRes != null) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            tint = Purple,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = title,
                        color = TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = TextGray,
                        fontSize = 12.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}