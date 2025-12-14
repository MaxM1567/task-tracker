package com.example.task_tracker

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.room.task.Task
import com.example.task_tracker.data.room.TaskRepository
import com.example.task_tracker.ui.screens.home.FormattedDate
import com.example.task_tracker.ui.screens.home.getFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
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

    var stopWatchOwner by mutableStateOf<Long?>(null)
        private set

    var stopWatchValue by mutableStateOf(0L)
        private set

    private var timerJob: Job? = null

    lateinit var taskList: Flow<List<Task>>

    init {
        updateDate()
        viewModelScope.launch {
            taskList = taskRepository.getTasks()
        }
    }

    fun onWishTitleChanged(newString: String) {
        taskTitleState = newString
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.addTask(task = task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(task = task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask(task = task)
        }
    }

    fun resetTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.resetTasks()
        }
    }

    fun updateUI() {
        viewModelScope.launch {
            if (isNewDay()) {
                resetTasks()
                updateDate()
            }
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

    fun updateDate() {
        viewModelScope.launch {
            _uiDateState.emit(getFormattedDate())
        }
    }

    fun startTimer(task: Task) {
        // Остановить текущий таймер с сохранением
        stopTimer(save = true)

        stopWatchOwner = task.id
        stopWatchValue = task.timeSpent

        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                stopWatchValue += 1
            }
        }
    }

    fun stopTimer(save: Boolean = true) {
        val ownerId = stopWatchOwner ?: return
        val timeToSave = stopWatchValue

        timerJob?.cancel()
        timerJob = null
        stopWatchOwner = null

        if (save) {
            viewModelScope.launch {
                val task = taskRepository.getTaskById(ownerId)
                taskRepository.updateTask(task.copy(timeSpent = timeToSave))
            }
        }
    }
}