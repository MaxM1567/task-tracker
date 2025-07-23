package com.example.task_tracker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.Task
import com.example.task_tracker.data.TaskRepository
import com.example.task_tracker.ui.screens.home.FormattedDate
import com.example.task_tracker.ui.screens.home.getFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val application: Application
) : ViewModel() {

    private val _uiDateState = MutableStateFlow(FormattedDate(0, "", ""))
    val uiDateState: StateFlow<FormattedDate> = _uiDateState

    var taskTitleState by mutableStateOf("")
    var taskRepetitions by mutableIntStateOf(1)

    init {
        updateDate()
    }

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

    fun isNewDay(): Boolean {
        val prefs = application.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastResetDate = prefs.getString("last_reset_date", "")

        return if (lastResetDate != currentDate) {
            prefs.edit { putString("last_reset_date", currentDate) }
            true
        } else {
            false
        }
    }

    fun updateUI() {
        if (isNewDay()) {
            resetTasks()
        }
        updateDate()
    }

    fun resetTasks() {
        try {
            viewModelScope.launch {
                val tasks = taskRepository.getTasks().first()
                tasks.forEach { task ->
                    taskRepository.updateTask(
                        task.copy(
                            curRepetitions = task.repetitions
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("TaskDailyReset", "Ошибка сброса задач", e)
        }

    }

    fun updateDate() {
        viewModelScope.launch {
            _uiDateState.emit(getFormattedDate())
        }
    }
}