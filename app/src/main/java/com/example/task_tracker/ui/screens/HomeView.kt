package com.example.task_tracker.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.task_tracker.Screen
import com.example.task_tracker.TaskViewModel
import com.example.task_tracker.data.Task

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
                    TaskCard(task)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .border(
                width = if (task.isDone) 4.dp else 0.dp,
                color = if (task.isDone) Color.Green else Color.Transparent,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = task.title,
                fontSize = 16.sp
            )
        }
    }
}