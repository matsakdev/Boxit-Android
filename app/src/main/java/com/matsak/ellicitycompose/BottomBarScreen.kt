package com.matsak.ellicitycompose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.matsak.ellicitycompose.screens.EllicityScreens

sealed class BottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: EllicityScreens
) {
    object Systems : BottomBarScreen(
        title = "SYSTEMS",
        icon = Icons.Default.Home,
        route = EllicityScreens.SystemsScreen
    )

    object Statistics : BottomBarScreen(
        title = "STATISTICS",
        icon = Icons.Default.Person,
        route = EllicityScreens.StatisticsScreen
    )

    object Settings : BottomBarScreen(
        title = "SETTINGS",
        icon = Icons.Default.Settings,
        route = EllicityScreens.SettingsScreen
    )
}
