package com.example.braillify.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.braillify.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private data class TermsItem(
    val index: String,
    val title: String,
    val preview: String,
    val body: String
)
@Composable
fun AboutTermsOfServiceScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {

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
                text = "Terms of Service",
                color = TextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ats_top_right_corner_icon_24px),
                contentDescription = null,
                tint = Purple,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Hero card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RowBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(PurpleLight, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ats_terms_of_service_hero_24px),
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Terms of Service",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Please review these terms to understand the rules and conditions for using Braillify.",
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val terms = listOf(
            TermsItem(
                index = "1.",
                title = "Acceptance of Terms",
                preview = "By using Braillify, you agree to follow these Terms of Service.",
                body = "By accessing or using Braillify, you acknowledge that you have read, " +
                        "understood, and agreed to these Terms of Service. If you do not agree " +
                        "with these terms, please discontinue use of the application."
            ),
            TermsItem(
                index = "2.",
                title = "Use of the App",
                preview = "Learn about the appropriate use of Braillify and its features.",
                body = "Braillify is intended to provide accessible Braille-related learning and " +
                        "input features. Users are expected to use the application responsibly and " +
                        "only for its intended purposes. Do not attempt to misuse, damage, or " +
                        "interfere with the application's functionality."
            ),
            TermsItem(
                index = "3.",
                title = "Intellectual Property",
                preview = "Information about Braillify's content, design, and intellectual property.",
                body = "The Braillify name, design, interface, content, and other original materials " +
                        "are the property of the Braillify development team unless otherwise stated. " +
                        "These materials may not be copied, modified, or distributed without " +
                        "appropriate permission."
            )
        )

        var selectedTerm by remember { mutableStateOf<TermsItem?>(null) }

        terms.forEach { item ->
            TermsRow(
                index = item.index,
                title = item.title,
                preview = item.preview,
                onClick = { selectedTerm = item }
            )
            Spacer(Modifier.height(12.dp))
        }

        selectedTerm?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedTerm = null },
                title = {
                    Text(
                        text = "${item.index} ${item.title}",
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(text = item.body, color = TextGray, lineHeight = 20.sp)
                },
                confirmButton = {
                    TextButton(onClick = { selectedTerm = null }) {
                        Text(text = "Close", color = Purple, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

@Composable
private fun TermsRow(
    index: String,
    title: String,
    preview: String,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$index $title",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = preview,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Text(text = "›", color = TextGray, fontSize = 22.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutTermsOfServicePreview() {
    AboutTermsOfServiceScreen()
}