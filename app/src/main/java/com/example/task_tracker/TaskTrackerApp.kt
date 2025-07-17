package com.example.task_tracker

import android.app.Application
import android.util.Log
import androidx.core.content.edit
import com.example.task_tracker.data.TaskRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class TaskTrackerApp : Application() {
    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onCreate() {
        super.onCreate()
        checkAndResetTasks()
    }

    private fun checkAndResetTasks() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastResetDate = prefs.getString("last_reset_date", "")

        if (lastResetDate != currentDate) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    val tasks = taskRepository.getTasks().first()
                    tasks.forEach { task ->
                        taskRepository.updateTask(task.copy(
                            curRepetitions = task.repetitions)
                        )
                    }
                    prefs.edit { putString("last_reset_date", currentDate) }
                }
            } catch (e: Exception) {
                Log.e("TaskDailyReset", "Ошибка сброса задач", e)
            }
        }
    }
}