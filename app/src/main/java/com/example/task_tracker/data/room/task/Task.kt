package com.example.task_tracker.data.room.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task-table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "task-title")
    val title: String,

    @ColumnInfo(name = "task-repetitions")
    val repetitions: Int = 1,

    @ColumnInfo(name = "task-cur-repetitions")
    val curRepetitions: Int = 1,

    @ColumnInfo(name = "task-type")
    val taskType: TaskType,

    @ColumnInfo(name = "task-time-spent")
    val remainingTime: Long = 0L
)
