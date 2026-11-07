package com.example.braillify.screens

import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.braillify.R

private fun labelFor(char: String): String = when {
    char.length == 1 && char[0].isLetter() -> "Letter"
    char.length == 1 && char[0].isDigit() -> "Digit"
    else -> "Symbol"
}

@Composable
internal fun BrailleEntryDetailScreen(
    entry: BrailleEntry,
    category: String = "Letter",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    BackHandler(enabled = true) { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DictBackground)
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
                Text(text = "‹", color = DictTextDark, fontSize = 28.sp, modifier = Modifier.semantics {
                    contentDescription = "Back Button"
                })
            }
            Spacer(Modifier.size(8.dp))
            Text(
                text = AppStrings.get(context, "dict_title"),
                color = DictTextDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(16.dp))

        // Hero card
        Card(
            modifier = Modifier.fillMaxWidth().semantics { hideFromAccessibility() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleLightColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = entry.char,
                    color = DictTextDark,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { hideFromAccessibility() }
                )
                Spacer(Modifier.height(16.dp))
                BrailleCell(
                    activeDots = entry.dots,
                    dotSize = 14.dp,
                    gap = 12.dp
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "${category.ifBlank { labelFor(entry.char) }} ${entry.char}",
                    color = DictTextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "( ${entry.dots.sorted().joinToString(" ")} )",
                    color = DictTextGray,
                    fontSize = 15.sp,
                    modifier = Modifier.semantics { hideFromAccessibility() }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Info card with icon
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleLightColor),
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
                        .size(28.dp)
                        .background(PurpleColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.global_info_icon_24px),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))

                // Using string format for the info text
                val dotString = if (entry.dots.size > 1) AppStrings.get(context, "dict_dots") else AppStrings.get(context, "dict_dot")
                val dotsJoined = entry.dots.sorted().joinToString(", ")
                val infoText = String.format(
                    AppStrings.get(context, "dict_info_text"),
                    category.lowercase(),
                    entry.char,
                    if (entry.dots.size > 1) "s" else "",
                    dotsJoined
                )
                Text(
                    text = infoText,
                    color = DictTextDark,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Pattern section (hidden completely from TalkBack)
        Column(
            modifier = Modifier.clearAndSetSemantics { }
        ) {
            Text(
                text = AppStrings.get(context, "dict_pattern_title"),
                color = DictTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preview cell on the left in a white card
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        BrailleCell(
                            activeDots = entry.dots,
                            dotSize = 10.dp,
                            gap = 8.dp
                        )
                    }
                }

                Spacer(Modifier.size(20.dp))

                // Legend — dot number, colored dot, description
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DotLegendRow(1, AppStrings.get(context, "dict_dot_1"), entry.dots)
                    DotLegendRow(2, AppStrings.get(context, "dict_dot_2"), entry.dots)
                    DotLegendRow(3, AppStrings.get(context, "dict_dot_3"), entry.dots)
                    DotLegendRow(4, AppStrings.get(context, "dict_dot_4"), entry.dots)
                    DotLegendRow(5, AppStrings.get(context, "dict_dot_5"), entry.dots)
                    DotLegendRow(6, AppStrings.get(context, "dict_dot_6"), entry.dots)
                }
            }
        }
    }
}

@Composable
private fun DotLegendRow(dotNumber: Int, label: String, activeDots: Set<Int>) {
    val isActive = dotNumber in activeDots
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$dotNumber",
            color = DictTextDark,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.size(width = 16.dp, height = 16.dp)
        )
        Spacer(Modifier.size(10.dp))
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isActive) PurpleColor else Color(0xFFE0E0E0))
        )
        Spacer(Modifier.size(10.dp))
        Text(
            text = label,
            color = DictTextGray,
            fontSize = 13.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BrailleEntryDetailPreview() {
    BrailleEntryDetailScreen(
        entry = BrailleEntry("A", setOf(1))
    )
}