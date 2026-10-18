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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
                text = "Braille Dictionary",
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
                //String
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
                        painter = painterResource(id = R.drawable.ab_top_right_icon_24px),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.size(12.dp))
                Text(
                    text = "The letter ${entry.char} is represented by " +
                            "dot${if (entry.dots.size > 1) "s" else ""} " +
                            entry.dots.sorted().joinToString(", ") + ".",
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
                text = "Braille Pattern",
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
                    DotLegendRow(1, "top left",     entry.dots)
                    DotLegendRow(2, "middle left",  entry.dots)
                    DotLegendRow(3, "bottom left",  entry.dots)
                    DotLegendRow(4, "top right",    entry.dots)
                    DotLegendRow(5, "middle right", entry.dots)
                    DotLegendRow(6, "bottom right", entry.dots)
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