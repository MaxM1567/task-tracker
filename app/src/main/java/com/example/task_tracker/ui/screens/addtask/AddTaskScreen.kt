package com.example.task_tracker.ui.screens.addtask

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.task_tracker.viewmodel.homevm.TaskViewModel
import com.example.task_tracker.data.room.task.Task
import com.example.task_tracker.data.room.task.TaskType
import com.example.task_tracker.ui.components.ScreenTitle
import com.example.task_tracker.ui.screens.addtask.component.RequestNotificationPermission
import com.example.task_tracker.ui.screens.addtask.component.hasPermission
import kotlinx.coroutines.launch

@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var stopwatchEnable by remember { mutableStateOf(false) }
    var taskTime by remember { mutableStateOf(0L) }
    var notifyPermissionGranted by remember { mutableStateOf(hasPermission(context)) }

    if (!notifyPermissionGranted) {
        RequestNotificationPermission { granted -> notifyPermissionGranted = granted }
    }

    ScreenTitle(text = "Задача", modifier = Modifier.padding(start = 16.dp, top = 16.dp))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = viewModel.taskTitleState,
                onValueChange = { viewModel.onWishTitleChanged(it) },
                label = { Text("Название") },
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = {
                    FilledIconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            if (viewModel.taskTitleState.isNotEmpty()) {
                                viewModel.addTask(
                                    if (stopwatchEnable) {
                                        Task(
                                            title = viewModel.taskTitleState.trim(),
                                            curRepetitions = viewModel.taskRepetitions,
                                            taskType = TaskType.StopWatch,
                                            remainingTime = taskTime
                                        )
                                    } else {
                                        Task(
                                            title = viewModel.taskTitleState.trim(),
                                            curRepetitions = viewModel.taskRepetitions,
                                            repetitions = viewModel.taskRepetitions,
                                            taskType = TaskType.Repeatable
                                        )
                                    }
                                )
                                scope.launch {
                                    navController.navigateUp()
                                }

                                viewModel.taskTitleState = ""
                                viewModel.taskRepetitions = 1
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Добавить задачу"
                        )
                    }
                }
            )
        }
        Spacer(Modifier.padding(8.dp))

        if (!stopwatchEnable) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Количество повторений", fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledIconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            if (viewModel.taskRepetitions - 1 >= 1) {
                                viewModel.taskRepetitions--
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Убавить"
                        )
                    }
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "${viewModel.taskRepetitions}"
                    )
                    FilledIconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            viewModel.taskRepetitions++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Добавить"
                        )
                    }
                }
            }
            Spacer(Modifier.padding(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
        ) {
            if (!notifyPermissionGranted) {
                Text(
                    text = "(Необходимо дать разрешение на отправку уведомлений!)",
                    color = Color.Red
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Контроль времени",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Отведите время конкретному делу",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.width(8.dp))

                Switch(
                    checked = stopwatchEnable,
                    onCheckedChange = { stopwatchEnable = !stopwatchEnable },
                    enabled = notifyPermissionGranted
                )
            }

            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Времени на выполнение",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Сколько вы планируете уделить времени этому делу",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.width(16.dp))

                OutlinedTextField(
                    modifier = Modifier.width(80.dp),
                    value = taskTime.toString(),
                    onValueChange = { taskTime = it.toLongOrNull() ?: 0L },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = CircleShape
                )
            }
        }
    }
}
