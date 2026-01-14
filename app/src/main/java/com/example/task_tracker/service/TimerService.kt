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
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerService : Service() {
    private val _timerOwner = MutableStateFlow<Long?>(null)
    val timerOwner = _timerOwner.asStateFlow()

    private val _seconds = MutableStateFlow(0)
    val seconds = _seconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var initialSeconds = 0
    private val binder = LocalBinder()
    private var job: Job? = null

    override fun onBind(intent: Intent?): IBinder = binder

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (_isRunning.value == false) {
                    val startTime = intent.getIntExtra(EXTRA_SECONDS, 0)

                    val ownerId = intent.getLongExtra(EXTRA_OWNER, -1L)
                    if (ownerId != -1L) _timerOwner.value = ownerId

                    startTimer(startTime)
                }
            }
            ACTION_STOP -> stopTimer()
        }
        return START_NOT_STICKY
    }

    private fun startTimer(startTime: Int) {
        if (startTime <= 0) return
        initialSeconds = startTime
        _seconds.value = startTime
        _isRunning.value = true

        startForeground(NOTIFICATION_ID, buildNotification())

        job = CoroutineScope(Dispatchers.Default).launch {
            while (_seconds.value > 0) {
                delay(1000)
                _seconds.value--
                updateNotification()
            }
            // Timer finished
            _isRunning.value = false
            _seconds.value = initialSeconds
            _timerOwner.value = null // Reset owner when finished
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun stopTimer() {
        job?.cancel()
        _isRunning.value = false
        _seconds.value = initialSeconds // Reset to initial value
        _timerOwner.value = null // Reset owner when stopped
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, TimerService::class.java).setAction(ACTION_STOP)

        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Таймер работает")
            .setContentText("Осталось секунд: ${_seconds.value}")
            .addAction(
                android.R.drawable.ic_media_pause,
                "Стоп",
                stopPendingIntent
            )
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Фоновый процесс",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
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
