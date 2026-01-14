package com.example.task_tracker.viewmodel.homevm

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_tracker.data.room.TaskRepository
import com.example.task_tracker.data.room.task.Task
import com.example.task_tracker.service.TimerService
import com.example.task_tracker.ui.screens.home.FormattedDate
import com.example.task_tracker.ui.screens.home.getFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
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

    // ------------- TIMER LOGIC -------------
    private val _timerOwner = MutableStateFlow<Long?>(null)
    val timerOwner = _timerOwner.asStateFlow()

    private val _seconds = MutableStateFlow<Int>(0)
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
        bindService()
    }

    private fun bindService() {
        Intent(application, TimerService::class.java).also { intent ->
            application.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun observeTimer() {
        viewModelScope.launch {
            timerService?.seconds?.collect { newSeconds ->
                _seconds.value = newSeconds
                endCountdown()
            }
        }
        viewModelScope.launch {
            timerService?.isRunning?.collect { running ->
                _isRunning.value = running
            }
        }
        viewModelScope.launch {
            timerService?.timerOwner?.collect { timerOwner ->
                _timerOwner.value = timerOwner
            }
        }
    }

    fun testStartTimer(taskId: Long, startTime: Int) {
        viewModelScope.launch {
            if (_timerOwner.value != null) {
                // Сохранение
                val task = taskRepository.getTaskById(_timerOwner.value as Long)
                updateTask(task.copy(remainingTime = _seconds.value.toLong()))

                _timerOwner.value = null
                _seconds.value = 0

                // Остановка последнего таймера
                application.startService(
                    Intent(application, TimerService::class.java).apply {
                        action = TimerService.Companion.ACTION_STOP
                    }
                )
            }

            delay(1)

            // Старт нового таймера
            _timerOwner.value = taskId
            _seconds.value = startTime

            if (startTime > 0 && !_isRunning.value) {
                application.startForegroundService(
                    Intent(application, TimerService::class.java).apply {
                        action = TimerService.Companion.ACTION_START
                        putExtra(TimerService.Companion.EXTRA_SECONDS, startTime)
                        putExtra(TimerService.Companion.EXTRA_OWNER, taskId)
                    }
                )
            }
        }
    }

    fun testStopTimer() {
        viewModelScope.launch {
            if (_timerOwner.value != null) {
                // Сохранение
                val task = taskRepository.getTaskById(_timerOwner.value as Long)
                updateTask(task.copy(remainingTime = _seconds.value.toLong()))

                _timerOwner.value = null
                _seconds.value = 0

                // Остановка последнего таймера
                application.startService(
                    Intent(application, TimerService::class.java).apply {
                        action = TimerService.Companion.ACTION_STOP
                    }
                )
            }
        }
    }

    suspend fun endCountdown() {
        if (_timerOwner.value == null || _seconds.value != 0) return

        val task = taskRepository.getTaskById(_timerOwner.value as Long)
        updateTask(task.copy(remainingTime = 0))

        _timerOwner.value = null
        _seconds.value = 0
    }

    override fun onCleared() {
        application.unbindService(connection)
        super.onCleared()
    }
}