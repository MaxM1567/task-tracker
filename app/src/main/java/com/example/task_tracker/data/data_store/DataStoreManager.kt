package com.example.task_tracker.data.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val dataStore: DataStore<Preferences>) {
    suspend fun saveSettings(settingsData: SettingsData) {
        dataStore.edit { pref ->
            pref[booleanPreferencesKey("notifications")] = settingsData.notifications
        }
    }

    fun getSettings(): Flow<SettingsData> = dataStore.data.map { pref ->
        SettingsData(
            notifications = pref[booleanPreferencesKey("notifications")] ?: false
        )
    }
}