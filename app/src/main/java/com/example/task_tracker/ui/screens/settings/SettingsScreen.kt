package com.example.task_tracker.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingScreen() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val settings by settingsViewModel.settings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
            text = "Настройки",
            style = MaterialTheme.typography.titleMedium
        )
        SettingRow(
            title = "Уведомления",
            description = "Напоминать о привычках",
            value = settings.notifications,
            action = { newValue -> settingsViewModel.updateNotifications(newValue) }
        )
    }
}


@Composable
fun SettingRow(
    title: String,
    description: String,
    value: Boolean,
    action: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { action(!value) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title, fontSize = 20.sp
                )
                Text(text = description, fontSize = 14.sp, color = Color.Gray)
            }
        }
        Switch(
            checked = value,
            onCheckedChange = action
        )
    }
}