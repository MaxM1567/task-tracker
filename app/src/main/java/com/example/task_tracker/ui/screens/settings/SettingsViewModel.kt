package com.example.task_tracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.data_store.DataStoreManager
import com.example.task_tracker.data.data_store.SettingsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    val settings = dataStoreManager.getSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SettingsData(notifications = false)
    )

    fun updateNotifications(enable: Boolean) = viewModelScope.launch {
        dataStoreManager.saveSettings(
            SettingsData(notifications = enable)
        )
    }
}