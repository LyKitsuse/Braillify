package com.example.braillify.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.braillify.R

// Label is removed from the enum, only the icon is stored
enum class NavTab(val iconRes: Int) {
    HOME(R.drawable.navbar_1home_mi_24px),
    KEYBOARD(R.drawable.navbar_2keyboard_mi_24px),
    SETTINGS(R.drawable.navbar_3settings_mi_24px)
}

@Composable
fun NavBar(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavTab.entries.forEach { tab ->
            // Fetch translated label based on the tab
            val labelKey = when (tab) {
                NavTab.HOME -> "nav_home"
                NavTab.KEYBOARD -> "nav_keyboard"
                NavTab.SETTINGS -> "nav_settings"
            }
            val displayLabel = AppStrings.get(context, labelKey)

            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = displayLabel,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(displayLabel) }
            )
        }
    }
}