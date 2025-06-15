package com.example.task_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.task_tracker.TaskViewModel
import com.example.task_tracker.data.Task
import kotlinx.coroutines.launch

@Composable
fun AddTaskScreen(
    viewModel: TaskViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            WishTextField(
                label = "Название",
                value = viewModel.taskTitleState,
                onValueChanged = { viewModel.onWishTitleChanged(it) })

            Button(onClick = {
                viewModel.addTask(
                    Task(title = viewModel.taskTitleState.trim())
                )
                scope.launch {
                    navController.navigateUp()
                }
            }) {
                Text("Добавить задачу")
            }
        }
    }
}

@Composable
fun WishTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}