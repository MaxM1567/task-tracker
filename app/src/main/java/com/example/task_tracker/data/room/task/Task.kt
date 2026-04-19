package com.example.task_tracker.data.room.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task-table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val taskType: TaskType,

    @ColumnInfo(name = "repetitions")
    val repetitions: Int = 1,

    @ColumnInfo(name = "cur-repetitions")
    val curRepetitions: Int = 1,

    @ColumnInfo(name = "timer")
    val timer: Long = 0L,

    @ColumnInfo(name = "cur-timer")
    val curTimer: Long = 0L
)
