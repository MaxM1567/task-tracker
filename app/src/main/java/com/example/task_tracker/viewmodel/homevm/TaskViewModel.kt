package com.example.task_tracker.viewmodel.homevm

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.data_store.DataStoreManager
import com.example.task_tracker.data.room.TaskRepository
import com.example.task_tracker.data.room.task.Task
import com.example.task_tracker.service.TimerService
import com.example.task_tracker.ui.screens.home.FormattedDate
import com.example.task_tracker.ui.screens.home.getFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val dataStoreManager: DataStoreManager,
    private val application: Application
) : ViewModel() {

    private val _uiDateState = MutableStateFlow(FormattedDate(0, "", ""))
    val uiDateState = _uiDateState.asStateFlow()

    val taskList: StateFlow<List<Task>> = taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // ------------- СОСТОЯНИЕ ТАЙМЕРА -------------
    private val _timerOwner = MutableStateFlow<Long?>(null)
    val timerOwner = _timerOwner.asStateFlow()

    private val _seconds = MutableStateFlow(0)
    val seconds = _seconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var timerService: TimerService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            timerService = (binder as TimerService.LocalBinder).getService()
            observeTimer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
        }
    }

    init {
        updateDate()
        updateUI()
        bindService()
    }

    private fun bindService() {
        Intent(application, TimerService::class.java).also { intent ->
            application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun observeTimer() {
        val service = timerService ?: return
        viewModelScope.launch {
            combine(
                service.seconds,
                service.isRunning,
                service.timerOwner
            ) { seconds, running, owner ->
                TimerState(seconds, running, owner)
            }.collect { state ->
                _seconds.value = state.seconds
                _isRunning.value = state.running
                _timerOwner.value = state.owner
            }
        }
    }

    private data class TimerState(val seconds: Int, val running: Boolean, val owner: Long?)

    // ------------- УПРАВЛЕНИЕ ЗАДАЧАМИ -------------

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.deleteTask(task)
        }
    }

    fun resetTasks() {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepository.resetTasks()
        }
    }

    fun updateUI() {
        viewModelScope.launch {
            if (dataStoreManager.checkIsNewDay()) {
                resetTasks()
                updateDate()
            }
        }
    }

    fun updateDate() {
        viewModelScope.launch {
            _uiDateState.value = getFormattedDate()
        }
    }

    // ------------- ЛОГИКА ТАЙМЕРА -------------

    fun startTimer(taskId: Long, startTime: Int) {
        if (startTime <= 0) return

        viewModelScope.launch {
            // Если запущен другой таймер — сохраняем его и останавливаем
            val currentOwner = _timerOwner.value
            if (currentOwner != null && currentOwner != taskId) {
                saveProgressToDb(currentOwner, _seconds.value.toLong())
                sendCommandToService(TimerService.ACTION_STOP)
            }

            val intent = Intent(application, TimerService::class.java).apply {
                action = TimerService.ACTION_START
                putExtra(TimerService.EXTRA_SECONDS, startTime)
                putExtra(TimerService.EXTRA_OWNER, taskId)
            }
            application.startForegroundService(intent)
        }
    }

    fun stopTimer() {
        viewModelScope.launch {
            _timerOwner.value?.let { ownerId ->
                saveProgressToDb(ownerId, _seconds.value.toLong())
                sendCommandToService(TimerService.ACTION_STOP)
            }
        }
    }

    private suspend fun saveProgressToDb(taskId: Long, remainingTime: Long) {
        try {
            val task = taskRepository.getTaskById(taskId)
            taskRepository.updateTask(task.copy(curTimer = remainingTime))
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Save error", e)
        }
    }

    private fun sendCommandToService(action: String) {
        application.startService(Intent(application, TimerService::class.java).apply {
            this.action = action
        })
    }

    override fun onCleared() {
        try {
            application.unbindService(connection)
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Unbind error", e)
        }
        super.onCleared()
    }
}
