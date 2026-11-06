package com.example.braillify.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal val PurpleColor = Color(0xFF5B3FE4)
internal val PurpleLightColor = Color(0xFFE8E3FB)
internal val DictBackground = Color(0xFFF2F0F5)
internal val DictTextDark = Color(0xFF1A1A1A)
internal val DictTextGray = Color(0xFF8A8A8A)
internal val CardWhite = Color(0xFFFFFFFF)
internal val DotInactive = Color(0xFFE0DEEA)
internal data class BrailleEntry(val char: String, val dots: Set<Int>)

private val alphabet = listOf(
    BrailleEntry("A", setOf(1)),
    BrailleEntry("B", setOf(1, 2)),
    BrailleEntry("C", setOf(1, 4)),
    BrailleEntry("D", setOf(1, 4, 5)),
    BrailleEntry("E", setOf(1, 5)),
    BrailleEntry("F", setOf(1, 2, 4)),
    BrailleEntry("G", setOf(1, 2, 4, 5)),
    BrailleEntry("H", setOf(1, 2, 5)),
    BrailleEntry("I", setOf(2, 4)),
    BrailleEntry("J", setOf(2, 4, 5)),
    BrailleEntry("K", setOf(1, 3)),
    BrailleEntry("L", setOf(1, 2, 3)),
    BrailleEntry("M", setOf(1, 3, 4)),
    BrailleEntry("N", setOf(1, 3, 4, 5)),
    BrailleEntry("O", setOf(1, 3, 5)),
    BrailleEntry("P", setOf(1, 2, 3, 4)),
    BrailleEntry("Q", setOf(1, 2, 3, 4, 5)),
    BrailleEntry("R", setOf(1, 2, 3, 5)),
    BrailleEntry("S", setOf(2, 3, 4)),
    BrailleEntry("T", setOf(2, 3, 4, 5)),
    BrailleEntry("U", setOf(1, 3, 6)),
    BrailleEntry("V", setOf(1, 2, 3, 6)),
    BrailleEntry("W", setOf(2, 4, 5, 6)),
    BrailleEntry("X", setOf(1, 3, 4, 6)),
    BrailleEntry("Y", setOf(1, 3, 4, 5, 6)),
    BrailleEntry("Z", setOf(1, 3, 5, 6))
)

private val digits = listOf(
    BrailleEntry("1", setOf(1)),
    BrailleEntry("2", setOf(1, 2)),
    BrailleEntry("3", setOf(1, 4)),
    BrailleEntry("4", setOf(1, 4, 5)),
    BrailleEntry("5", setOf(1, 5)),
    BrailleEntry("6", setOf(1, 2, 4)),
    BrailleEntry("7", setOf(1, 2, 4, 5)),
    BrailleEntry("8", setOf(1, 2, 5)),
    BrailleEntry("9", setOf(2, 4)),
    BrailleEntry("0", setOf(2, 4, 5))
)

private val punctuations = listOf(
    BrailleEntry(".", setOf(2, 5, 6)),
    BrailleEntry(",", setOf(2)),
    BrailleEntry("?", setOf(2, 3, 6)),
    BrailleEntry("!", setOf(2, 3, 5)),
    BrailleEntry(";", setOf(2, 3)),
    BrailleEntry(":", setOf(2, 5)),
    BrailleEntry("-", setOf(3, 6)),
    BrailleEntry("'", setOf(3)),
    BrailleEntry("(", setOf(1,2,6)),
    BrailleEntry(")", setOf(3,4,5)),
    BrailleEntry("\"", setOf(3,5,6))
)

private enum class Tab(val label: String) {
    ALPHABETS("Alphabets"),
    DIGITS("Digits"),
    PUNCTUATIONS("Punctuations")
}

@Composable
fun BrailleDictionaryScreen(onBack: () -> Unit = {}) {
    var selectedTab by remember { mutableStateOf(Tab.ALPHABETS) }
    var selectedEntry by remember { mutableStateOf<BrailleEntry?>(null) }

    selectedEntry?.let { entry ->
        BrailleEntryDetailScreen(
            entry = entry,
            category = when (selectedTab) {
                Tab.ALPHABETS -> "Letter"
                Tab.DIGITS -> "Digit"
                Tab.PUNCTUATIONS -> "Symbol"
            },
            onBack = { selectedEntry = null }
        )
        return
    }

    BackHandler(enabled = true) { onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DictBackground)
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

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Tab.entries.forEach { tab ->
                TabPill(
                    label = tab.label,
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
                Spacer(Modifier.size(10.dp))
            }
        }

        Spacer(Modifier.height(20.dp))

        val (title, items) = when (selectedTab) {
            Tab.ALPHABETS -> "Alphabets (A–Z)" to alphabet
            Tab.DIGITS -> "Digits (0–9)" to digits
            Tab.PUNCTUATIONS -> "Punctuations" to punctuations
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = DictTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics { hideFromAccessibility() }
            )
            Text(
                text = "${items.size} characters",
                color = DictTextGray,
                fontSize = 13.sp,
                modifier = Modifier.semantics {
                    hideFromAccessibility()
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { entry ->
                BrailleEntryCard(
                    char = entry.char,
                    dots = entry.dots,
                    onClick = { selectedEntry = entry },
                )
            }
        }
    }
}

@Composable
private fun TabPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) PurpleColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else DictTextGray,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun BrailleEntryCard(
    char: String,
    dots: Set<Int>,
    onClick: () -> Unit
) {
    val sortedDots = dots.sorted().joinToString(", ")
    val dotLabel = if (dots.size == 1) "dot" else "dots"
    val charName = when (char) {
        "." -> "Period"
        "," -> "Comma"
        "?" -> "Question mark"
        "!" -> "Exclamation mark"
        ";" -> "Semicolon"
        ":" -> "Colon"
        "-" -> "Hyphen"
        "'" -> "Apostrophe"
        "(" -> "Open parenthesis"
        ")" -> "Close parenthesis"
        "\"" -> "Quote"
        else -> char
    }
    val accessibilityDescription = "$charName, $dotLabel $sortedDots"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = char,
                color = DictTextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.semantics {
                    contentDescription = accessibilityDescription
                }
            )
            Spacer(Modifier.height(8.dp))
            BrailleCell(activeDots = dots)
        }
    }
}

@Composable
internal fun BrailleCell(
    activeDots: Set<Int>,
    modifier: Modifier = Modifier,
    dotSize: Dp = 6.dp,
    gap: Dp = 5.dp
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(gap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            Dot(active = 1 in activeDots, size = dotSize)
            Dot(active = 4 in activeDots, size = dotSize)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            Dot(active = 2 in activeDots, size = dotSize)
            Dot(active = 5 in activeDots, size = dotSize)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            Dot(active = 3 in activeDots, size = dotSize)
            Dot(active = 6 in activeDots, size = dotSize)
        }
    }
}

@Composable
private fun Dot(active: Boolean, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (active) PurpleColor else DotInactive)
    )
}

@Preview(showBackground = true)
@Composable
fun BrailleDictionaryPreview() {
    BrailleDictionaryScreen()
}