package com.example.task_tracker.ui.screens.home

import android.util.Log
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.task_tracker.TaskViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen() {
    val viewModel: TaskViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val curDate = getFormattedDate()
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp),
            text = "${curDate.day} ${curDate.month.uppercase()}",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 8.dp, bottom = 16.dp),
            text = curDate.dayOfWeek,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        val taskList = viewModel.taskList.collectAsState(initial = listOf())
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(taskList.value, key = { task -> task.id }) { task ->
                val swipeableState = rememberSwipeableState(initialValue = 0)
                val anchors = mapOf(0f to 0, -1100f to -1, 350f to 1)
                val bgColor by animateColorAsState(
                    if (swipeableState.offset.value < -50) Color.Red else Color.Transparent,
                    label = "bgColorAnimation"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .swipeable(
                            state = swipeableState,
                            anchors = anchors,
                            thresholds = { _, _ -> FractionalThreshold(0.5f) },
                            orientation = Orientation.Horizontal
                        )
                        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                ) {
                    TaskCard(task = task)
                }

                LaunchedEffect(swipeableState.currentValue) {
                    when (swipeableState.currentValue) {
                        1 -> {
                            if (task.curRepetitions - 1 >= 0) {
                                Log.d("ABOBA", "До $task")
                                viewModel.updateTask(task.copy(curRepetitions = task.curRepetitions - 1))
                                Log.d("ABOBA", "После $task")
                            } else {
                                viewModel.updateTask(task.copy(curRepetitions = task.repetitions))
                            }
                            swipeableState.animateTo(0)
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