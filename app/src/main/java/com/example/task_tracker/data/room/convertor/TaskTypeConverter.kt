package com.example.task_tracker.data.room.convertor

import androidx.room.TypeConverter
import com.example.task_tracker.data.room.task.TaskType

class TaskTypeConverter {
    @TypeConverter
    fun fromTaskType(taskType: TaskType): String {
        return when (taskType) {
            is TaskType.StopWatch -> "StopWatch"
            is TaskType.Repeatable -> "Repeatable"
        }
    }

    @TypeConverter
    fun toTaskType(taskType: String): TaskType {
        return when (taskType) {
            "StopWatch" -> TaskType.StopWatch
            "Repeatable" -> TaskType.Repeatable
            else -> throw IllegalArgumentException("Unknown task type")
        }
    }
}