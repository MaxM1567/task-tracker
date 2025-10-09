package com.example.task_tracker.ui.screens.settings.components

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts


fun handleNotificationToggle(
    context: Context,
    enable: Boolean,
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    if (!enable) {
        onDeny()
        return
    }
    requestNotificationPermission(
        context = context,
        onGranted = onAllow,
        onDenied = {
            Toast.makeText(context, "Разрешение на уведомления запрещено", Toast.LENGTH_SHORT)
                .show()
            onDeny()
        }
    )
}

fun requestNotificationPermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        onGranted()
        return
    }
    val launcher = (context as ComponentActivity)
        .activityResultRegistry
        .register("notif_perm", ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onGranted() else onDenied()
        }
    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
}