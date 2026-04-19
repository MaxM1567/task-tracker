package com.example.task_tracker.data.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataStoreManager(private val dataStore: DataStore<Preferences>) {
    
    private val LAST_RESET_DATE = stringPreferencesKey("last_reset_date")
    private val NOTIFICATIONS = booleanPreferencesKey("notifications")

    suspend fun saveSettings(settingsData: SettingsData) {
        dataStore.edit { pref ->
            pref[NOTIFICATIONS] = settingsData.notifications
        }
    }

    fun getSettings(): Flow<SettingsData> = dataStore.data.map { pref ->
        SettingsData(
            notifications = pref[NOTIFICATIONS] ?: false
        )
    }

    suspend fun checkIsNewDay(): Boolean {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val preferences = dataStore.data.first()
        val lastResetDate = preferences[LAST_RESET_DATE] ?: ""

        if (lastResetDate != currentDate) {
            dataStore.edit { pref ->
                pref[LAST_RESET_DATE] = currentDate
            }
            return true
        }
        return false
    }
}
