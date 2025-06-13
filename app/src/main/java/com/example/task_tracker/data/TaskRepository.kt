package com.example.task_tracker.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun addTask(task: Task) {
        taskDao.addTask(task)
    }

    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()
}