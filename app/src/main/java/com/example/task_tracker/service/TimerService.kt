package com.example.task_tracker.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.task_tracker.MainActivity
import com.example.task_tracker.data.room.TaskRepository
import com.example.task_tracker.utils.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var taskRepository: TaskRepository

    private val _timerOwner = MutableStateFlow<Long?>(null)
    val timerOwner = _timerOwner.asStateFlow()

    private val _seconds = MutableStateFlow(0)
    val seconds = _seconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val binder = LocalBinder()
    private var job: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder = binder

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val secondsToStart = intent.getIntExtra(EXTRA_SECONDS, 0)
                val ownerId = intent.getLongExtra(EXTRA_OWNER, -1L)
                if (ownerId != -1L) {
                    startTimer(secondsToStart, ownerId)
                }
            }
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(startTimeInSeconds: Int, ownerId: Long) {
        if (startTimeInSeconds <= 0) return

        // Cancel previous job if it was running for another task
        job?.cancel()

        _timerOwner.value = ownerId
        _seconds.value = startTimeInSeconds
        _isRunning.value = true

        val endTimeMillis = System.currentTimeMillis() + (startTimeInSeconds * 1000L)
        
        startForeground(NOTIFICATION_ID, buildNotification())

        job = serviceScope.launch {
            while (System.currentTimeMillis() < endTimeMillis) {
                val remainingSeconds = ((endTimeMillis - System.currentTimeMillis()) / 1000).toInt()
                
                if (_seconds.value != remainingSeconds) {
                    _seconds.value = maxOf(0, remainingSeconds)
                    updateNotification()
                }
                delay(300)
            }

            // Persistence: Update DB when timer finishes
            onTimerFinished(ownerId)
        }
    }

    private suspend fun onTimerFinished(ownerId: Long) {
        try {
            val task = taskRepository.getTaskById(ownerId)
            taskRepository.updateTask(task.copy(curTimer = 0))
        } catch (e: Exception) {
            Log.e("TimerService", "Failed to update task on finish", e)
        }

        _seconds.value = 0
        _isRunning.value = false
        _timerOwner.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopTimer() {
        job?.cancel()
        _isRunning.value = false
        _timerOwner.value = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            1,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Таймер запущен")
            .setContentText("Осталось: ${formatTime(_seconds.value.toLong())}")
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val EXTRA_SECONDS = "EXTRA_SECONDS"
        const val EXTRA_OWNER = "EXTRA_OWNER"
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
    }
}
