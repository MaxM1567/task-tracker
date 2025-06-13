package com.example.task_tracker.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.task_tracker.Screen
import com.example.task_tracker.TaskViewModel
import com.example.task_tracker.data.Task
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeView(
    viewModel: TaskViewModel, navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(all = 20.dp), onClick = {
                    navController.navigate(Screen.AddScreen.route)
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }) { padding ->
        Column {
            val taskList = viewModel.taskList.collectAsState(initial = listOf())
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                            1 -> { // Свайп вправо
                                viewModel.updateTask(task.copy(isDone = !task.isDone))
                                swipeableState.animateTo(0)
                            }

                            -1 -> { // Свайп влево
                                viewModel.deleteTask(task)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .border(
                width = if (task.isDone) 4.dp else 0.dp,
                color = if (task.isDone) Color.Green else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                fontSize = 16.sp
            )
        }
    }
}