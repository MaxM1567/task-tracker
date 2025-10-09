package com.example.task_tracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationUtils {

    private const val CHANNEL_ID = "remind_channel"
    private const val CHANNEL_NAME = "Напоминания"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Канал для напоминаний"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}