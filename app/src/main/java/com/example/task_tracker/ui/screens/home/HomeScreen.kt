package com.example.task_tracker.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.task_tracker.viewmodel.homevm.TaskViewModel
import com.example.task_tracker.data.room.task.TaskType
import com.example.task_tracker.ui.components.ScreenTitle
import com.example.task_tracker.ui.screens.home.taskcard.TaskCard
import com.example.task_tracker.ui.screens.home.taskcard.TimerTaskCard

@Composable
fun HomeScreen(viewModel: TaskViewModel) {
    val date by viewModel.uiDateState.collectAsState()
    val taskList = viewModel.taskList.collectAsState(initial = listOf())

    val timerOwner by viewModel.timerOwner.collectAsState()
    val seconds by viewModel.seconds.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenTitle(
            text = "${date.day} ${date.month.uppercase()}",
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Text(
            text = date.dayOfWeek,
            modifier = Modifier.padding(start = 20.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        LazyColumn(Modifier.fillMaxSize()) {
            items(taskList.value, key = { it.id }) { task ->
                when (task.taskType) {

                    is TaskType.Repeatable -> {
                        TaskCard(
                            task = task,
                            onUpdate = viewModel::updateTask,
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }

                    is TaskType.StopWatch -> {
                        val isRunning = task.id == (timerOwner ?: -1)

                        TimerTaskCard(
                            task = task,
                            isTimerRunning = isRunning,
                            time = if (isRunning) seconds.toLong() else task.remainingTime,
                            onUpdate = {
                                if (isRunning) {
                                    viewModel.testStopTimer()
                                } else {
                                    viewModel.testStartTimer(
                                        taskId = task.id,
                                        startTime = task.remainingTime.toInt()
                                    )
                                }
                            },
                            onDelete = {
                                if (isRunning) {
                                    viewModel.testStopTimer()
                                }
                                viewModel.deleteTask(task)
                            }
                        )
                    }
                }
            }
        }
    }
}