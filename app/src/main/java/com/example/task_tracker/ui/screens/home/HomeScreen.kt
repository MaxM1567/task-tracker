package com.example.task_tracker.ui.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.task_tracker.TaskViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: TaskViewModel) {
    val currentValue by viewModel.uiDateState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = "${currentValue.day} ${currentValue.month.uppercase()}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(start = 20.dp, bottom = 16.dp),
            text = currentValue.dayOfWeek,
            style = MaterialTheme.typography.bodyMedium
        )
        val taskList = viewModel.taskList.collectAsState(initial = listOf())
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(taskList.value, key = { task -> task.id }) { task ->
                val swipeAbleState = rememberSwipeableState(initialValue = 0)
                val anchors = mapOf(0f to 0, -1100f to -1, 350f to 1)
                val bgColor by animateColorAsState(
                    if (swipeAbleState.offset.value < -50) Color.Red else Color.Transparent,
                    label = "bgColorAnimation"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .swipeable(
                            state = swipeAbleState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.5f) },
                            orientation = Orientation.Horizontal
                        )
                        .offset { IntOffset(swipeAbleState.offset.value.roundToInt(), 0) }
                ) {
                    TaskCard(task = task)
                }

                LaunchedEffect(swipeAbleState.currentValue) {
                    when (swipeAbleState.currentValue) {
                        1 -> {
                            if (task.curRepetitions - 1 >= 0) {
                                viewModel.updateTask(task.copy(curRepetitions = task.curRepetitions - 1))
                            } else {
                                viewModel.updateTask(task.copy(curRepetitions = task.repetitions))
                            }
                            swipeAbleState.animateTo(0)
                        }

                        -1 -> {
                            viewModel.deleteTask(task)
                        }
                    }
                }
            }
        }
    }
}