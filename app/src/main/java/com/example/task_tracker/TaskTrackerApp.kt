package com.example.task_tracker

import android.app.Application
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        checkAndResetTasks()
    }

    private fun checkAndResetTasks() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastResetDate = prefs.getString("last_reset_date", "")

        if (lastResetDate != currentDate) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val taskRepository = Graph.taskRepository
                    taskRepository.resetDailyTasks()
                    prefs.edit { putString("last_reset_date", currentDate) }
                }
            } catch (e: Exception) {
                Log.e("TaskDailyReset", "Ошибка сброса задач", e)
            }
        }
    }
}