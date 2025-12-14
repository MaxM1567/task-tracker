package com.example.task_tracker.data.room.task

sealed class TaskType {
    object Repeatable : TaskType()
    object StopWatch : TaskType()
}