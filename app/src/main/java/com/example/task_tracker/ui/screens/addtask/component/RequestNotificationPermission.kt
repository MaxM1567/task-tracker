package com.example.task_tracker.ui.screens.addtask.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat

/*
@Composable
fun RequestNotificationPermission(
    onResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { /* result */ }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ->
                { /* разрешение уже есть */ }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.POST_NOTIFICATIONS) ->
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            else ->
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionStateUpdate()
    }
}
 */

@Composable
fun RequestNotificationPermission(
    onResult: (Boolean) -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            onResult(granted)
        }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

fun hasPermission(context: Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.POST_NOTIFICATIONS
) == PackageManager.PERMISSION_GRANTED