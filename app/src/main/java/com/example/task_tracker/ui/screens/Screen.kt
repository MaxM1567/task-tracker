package com.example.task_tracker.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
) {
    sealed class BottomScreen(
        val bRoute: String,
        val icon: ImageVector
    ) : Screen(bRoute) {
        object HomeScreen : BottomScreen(
            bRoute = "home_screen",
            icon = Icons.Default.Home
        )

        object SettingsScreen : BottomScreen(
            bRoute = "setting_screen",
            icon = Icons.Default.Settings
        )
    }

    object AddTaskScreen : Screen(
        route = "add_task_screen"
    )
}

val bottomScreens = listOf(
    Screen.BottomScreen.HomeScreen,
    Screen.BottomScreen.SettingsScreen
)