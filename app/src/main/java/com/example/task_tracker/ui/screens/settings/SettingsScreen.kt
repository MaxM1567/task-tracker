package com.example.task_tracker.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.task_tracker.notification.RemindManager
import com.example.task_tracker.ui.components.ScreenTitle
import com.example.task_tracker.ui.screens.settings.components.SettingRow
import com.example.task_tracker.ui.screens.settings.components.handleNotificationToggle

@Composable
fun SettingScreen() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settings.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ScreenTitle(text = "Настройки", modifier = Modifier.padding(start = 16.dp, top = 16.dp))

        SettingRow(
            title = "Уведомления",
            description = "Напоминать о привычках",
            value = settings.notifications,
            action = { newValue ->
                handleNotificationToggle(
                    context = context,
                    enable = newValue,
                    onAllow = {
                        settingsViewModel.updateNotifications(true)
                        RemindManager.scheduleNext(context)
                    },
                    onDeny = {
                        settingsViewModel.updateNotifications(false)
                        RemindManager.stopReminders(context)
                    }
                )
            }
        )
    }
}