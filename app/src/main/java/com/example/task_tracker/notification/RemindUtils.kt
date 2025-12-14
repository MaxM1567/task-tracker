package com.example.task_tracker.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object RemindManager {

    private const val WORK_NAME = "remind"

    fun scheduleNext(context: Context) {
        try {
            val delay = millisUntilNextSlot()

            val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                //.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    work
                )
        } catch (e: Exception) {
            Log.e("Error", "scheduleNext(): $e")
        }
    }

    private fun millisUntilNextSlot(): Long {
        val now = LocalDateTime.now()
        val slots = listOf(8, 14, 20)

        val target = slots
            .map { now.withHour(it).withMinute(0).withSecond(0) }
            .firstOrNull { it.isAfter(now) }
            ?: now.plusDays(1).withHour(8).withMinute(0).withSecond(0)

        return Duration.between(now, target).toMillis()
    }

    fun stopReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}