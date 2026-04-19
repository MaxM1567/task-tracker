package com.example.task_tracker.utils

import kotlin.text.format

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    val seconds = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}