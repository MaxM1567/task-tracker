package com.example.task_tracker.ui.screens.addtask

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.task_tracker.data.room.task.Task
import com.example.task_tracker.data.room.task.TaskType
import com.example.task_tracker.ui.components.ScreenTitle
import com.example.task_tracker.ui.screens.addtask.component.RequestNotificationPermission
import com.example.task_tracker.ui.screens.addtask.component.hasPermission
import com.example.task_tracker.viewmodel.addtaskvm.AddTaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    navController: NavController
) {
    val addTaskViewModel: AddTaskViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var stopwatchEnable by remember { mutableStateOf(false) }
    var taskTime by remember { mutableLongStateOf(0) }
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
                value = addTaskViewModel.taskTitleState,
                onValueChange = { addTaskViewModel.onWishTitleChanged(it) },
                label = { Text("Название") },
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = {
                    FilledIconButton(
                        modifier = Modifier.size(58.dp),
                        onClick = {
                            if (addTaskViewModel.taskTitleState.isNotEmpty()) {
                                addTaskViewModel.addTask(
                                    Task(
                                        title = addTaskViewModel.taskTitleState.trim(),
                                        taskType = if (stopwatchEnable) TaskType.StopWatch else TaskType.Repeatable,
                                        repetitions = addTaskViewModel.taskRepetitions,
                                        curRepetitions = addTaskViewModel.taskRepetitions,
                                        timer = taskTime,
                                        curTimer = taskTime
                                    )
                                )
                                scope.launch {
                                    navController.navigateUp()
                                }

                                addTaskViewModel.taskTitleState = ""
                                addTaskViewModel.taskRepetitions = 1
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
                            if (addTaskViewModel.taskRepetitions - 1 >= 1) {
                                addTaskViewModel.taskRepetitions--
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
                        text = "${addTaskViewModel.taskRepetitions}"
                    )
                    FilledIconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            addTaskViewModel.taskRepetitions++
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
                    onCheckedChange = { stopwatchEnable = it },
                    enabled = notifyPermissionGranted
                )
            }

            AnimatedVisibility(
                visible = stopwatchEnable,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
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
                                text = "Укажите длительность таймера",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        val displayTime = remember(taskTime) {
                            val h = taskTime / 3600
                            val m = (taskTime % 3600) / 60
                            String.format("%02d:%02d", h, m)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = displayTime,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    TimeWheelPicker(onTimeChange = { taskTime = it })
                }
            }
        }
    }
}

@Composable
fun TimeWheelPicker(onTimeChange: (Long) -> Unit) {
    val hoursState = rememberLazyListState(initialFirstVisibleItemIndex = 0)
    val minutesState = rememberLazyListState(initialFirstVisibleItemIndex = 0)

    val selectedHour by remember { derivedStateOf { hoursState.firstVisibleItemIndex } }
    val selectedMinute by remember { derivedStateOf { minutesState.firstVisibleItemIndex } }

    LaunchedEffect(selectedHour, selectedMinute) {
        onTimeChange((selectedHour * 3600 + selectedMinute * 60).toLong())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Подсветка выбранного ряда
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Часы
            InfiniteWheelColumn(
                state = hoursState,
                itemsCount = 24,
                label = "ч"
            )

            Text(
                text = ":",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Минуты
            InfiniteWheelColumn(
                state = minutesState,
                itemsCount = 60,
                label = "м"
            )
        }
    }
}

@Composable
fun InfiniteWheelColumn(
    state: androidx.compose.foundation.lazy.LazyListState,
    itemsCount: Int,
    label: String
) {
    LazyColumn(
        state = state,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        modifier = Modifier
            .height(120.dp)
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { Spacer(modifier = Modifier.height(40.dp)) }
        items(itemsCount) { index ->
            val isSelected = state.firstVisibleItemIndex == index
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = index.toString().padStart(2, '0'),
                    fontSize = if (isSelected) 22.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                )
                if (isSelected) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 2.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}
