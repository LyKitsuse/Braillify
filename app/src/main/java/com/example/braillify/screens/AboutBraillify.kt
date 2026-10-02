package com.example.braillify.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.braillify.R
import androidx.compose.foundation.Image
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
private val RowBackground = Color(0xFFEDEBF2)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF8A8A8A)
private val IconPlaceholder = Color(0xFF111111)

@Composable
fun AboutBraillifyScreen(onBack: () -> Unit = {}) {
    var showDevelopers by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

    BackHandler(enabled = showDevelopers || showPrivacy || showTerms) {
        when {
            showDevelopers -> showDevelopers = false
            showPrivacy    -> showPrivacy = false
            showTerms      -> showTerms = false
        }
    }

    when {
        showDevelopers -> AboutDeveloperScreen(onBack = { showDevelopers = false })
        showPrivacy -> AboutPrivacyPolicyScreen(onBack = { showPrivacy = false })
        showTerms -> AboutTermsOfServiceScreen(onBack = { showTerms = false })
        else -> AboutMain(
            onBack = onBack,
            onDevelopers = { showDevelopers = true },
            onPrivacy = { showPrivacy = true },
            onTerms = { showTerms = true }
        )
    }
}

@Composable
private fun AboutMain(
    onBack: () -> Unit,
    onDevelopers: () -> Unit,
    onPrivacy: () -> Unit,
    onTerms: () -> Unit
) {
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
                text = "About Braillify",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ab_top_right_icon_24px),
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // App logo + name + version
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.braillify_logo),
                contentDescription = "Braillify Logo",
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Braillify",
                color = TextDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Version 1.0.0",
                color = TextGray,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.height(20.dp))

        // Description
        Text(
            text = "Braillify is a mobile application designed to help visually impaired users read " +
                    "and write using Braille, with the support of voice assistance and machine learning " +
                    "technology.",
            color = TextGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(28.dp))

        // Info rows
        InfoRow(
            title = "Developer",
            subtitle = "Meet the developers of Braillify",
            iconRes = R.drawable.ab_developer_icon_24px,
            onClick = onDevelopers
        )
        Spacer(Modifier.height(12.dp))
        InfoRow(
            title = "Privacy Policy",
            subtitle = "Learn how we protect your data.",
            iconRes = R.drawable.ab_privacy_policy_icon_24px,
            onClick = onPrivacy
        )
        Spacer(Modifier.height(12.dp))
        InfoRow(
            title = "Terms of Service",
            subtitle = "Read our terms and conditions.",
            iconRes = R.drawable.ab_terms_of_service_24px,
            onClick = onTerms
        )
    }
}

@Composable
private fun InfoRow(
    title: String,
    subtitle: String,
    iconRes: Int,
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PurpleLight, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Purple,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
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

@Composable
internal fun AboutHeader(title: String, onBack: () -> Unit) {
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
            text = title,
            color = TextDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AboutBraillifyPreview() {
    AboutBraillifyScreen()
}