package com.example.task_tracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()

    suspend fun addTask(task: Task) = taskDao.addTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun resetTasks() {
        val tasks = getTasks().first()
        tasks.forEach { task ->
            updateTask(
                task.copy(
                    curRepetitions = task.repetitions
                )
            )
        }
    }
}