package com.example.task_tracker

import android.app.Application

class TaskTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}