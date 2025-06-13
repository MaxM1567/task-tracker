package com.example.task_tracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.Task
import com.example.task_tracker.data.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository = Graph.taskRepository
) : ViewModel() {
    var taskTitleState by mutableStateOf("")

    fun onWishTitleChanged(newString: String) {
        taskTitleState = newString
    }

    lateinit var taskList: Flow<List<Task>>

    init {
        viewModelScope.launch {
            taskList = taskRepository.getTasks()
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTask(task = task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task = task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask(task = task)
        }
    }
}