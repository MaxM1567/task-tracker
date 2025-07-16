package com.example.task_tracker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    suspend fun addTask(task: Task) = taskDao.addTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun resetDailyTasks() = taskDao.resetDailyTasks()

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
}