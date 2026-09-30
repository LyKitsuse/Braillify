package com.example.braillify.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

enum class NavTab(val label: String) {
    HOME("Home"),
    KEYBOARD("Keyboard"),
    SETTINGS("Settings")
}

@Composable
fun NavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .zIndex(1f)
            .shadow(
                elevation = 12.dp,
                clip = false // Set to true if you want to clip the internal content to the shape
            )
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = Color.White
    ) {
        NavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {},
                label = { Text(tab.label, color = Color.Black) }
            )
        }
    }
}
