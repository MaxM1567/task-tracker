package com.example.task_tracker

import android.content.Context
import androidx.room.Room
import com.example.task_tracker.data.TaskDataBase
import com.example.task_tracker.data.TaskRepository

object Graph {
    lateinit var database: TaskDataBase

    val taskRepository by lazy {
        TaskRepository(taskDao = database.taskDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(
            context = context,
            klass = TaskDataBase::class.java,
            name = "tasklist.db"
        ).build()
    }
}