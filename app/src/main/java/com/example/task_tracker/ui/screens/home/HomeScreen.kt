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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.task_tracker.data.room.task.TaskType
import com.example.task_tracker.ui.components.ScreenTitle
import com.example.task_tracker.ui.screens.home.taskcard.TaskCard
import com.example.task_tracker.ui.screens.home.taskcard.TimerTaskCard
import com.example.task_tracker.viewmodel.homevm.TaskViewModel

@Composable
fun HomeScreen() {
    val taskViewModel: TaskViewModel = hiltViewModel()

    val date by taskViewModel.uiDateState.collectAsStateWithLifecycle()
    val taskList by taskViewModel.taskList.collectAsState(initial = listOf())

    val timerOwner by taskViewModel.timerOwner.collectAsState()
    val seconds by taskViewModel.seconds.collectAsState()

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
            items(
                items = taskList,
                key = { it.id }
            ) { task ->
                when (task.taskType) {

                    is TaskType.Repeatable -> {
                        TaskCard(
                            task = task,
                            onUpdate = taskViewModel::updateTask,
                            onDelete = { taskViewModel.deleteTask(task) }
                        )
                    }

                    is TaskType.StopWatch -> {
                        val isRunning = task.id == (timerOwner ?: -1)

                        TimerTaskCard(
                            task = task,
                            isTimerRunning = isRunning,
                            time = if (isRunning) seconds.toLong() else task.timer,
                            onUpdate = {
                                if (isRunning) {
                                    taskViewModel.stopTimer()
                                } else {
                                    taskViewModel.startTimer(
                                        taskId = task.id,
                                        startTime = task.curTimer.toInt()
                                    )
                                }
                            },
                            onDelete = {
                                if (isRunning) {
                                    taskViewModel.stopTimer()
                                }
                                taskViewModel.deleteTask(task)
                            }
                        )
                    }
                }
            }
        }
    }
}