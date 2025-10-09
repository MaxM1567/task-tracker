package com.example.task_tracker

import android.app.Application
import com.example.task_tracker.notification.NotificationUtils
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TaskTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
    }
}