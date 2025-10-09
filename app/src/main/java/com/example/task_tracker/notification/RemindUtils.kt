package com.example.task_tracker.notification

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object RemindManager {

    private const val REMINDER_TAG = "remind"

    fun startReminders(context: Context) {
        stopReminders(context)
        listOf(8, 14, 20).forEach { enqueueReminder(context, it) }
    }

    fun enqueueReminder(context: Context, hour: Int) {
        val work = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(hoursUntil(hour), TimeUnit.MILLISECONDS)
            .addTag(REMINDER_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "reminder_$hour",
            ExistingPeriodicWorkPolicy.UPDATE,
            work
        )
    }

    private fun hoursUntil(hour: Int): Long {
        val now = LocalDateTime.now()
        var target = now.withHour(hour).withMinute(0).withSecond(0).withNano(0)
        if (now.isAfter(target)) target = target.plusDays(1)
        return Duration.between(now, target).toMillis()
    }

    fun stopReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(REMINDER_TAG)
    }
}