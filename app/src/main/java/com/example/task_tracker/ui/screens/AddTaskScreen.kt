package com.example.task_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.task_tracker.TaskViewModel
import com.example.task_tracker.data.Task
import kotlinx.coroutines.launch

@Composable
fun AddTaskScreen(
    navController: NavController
) {
    val viewModel: TaskViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    Column {
        Text(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 16.dp),
            text = "Задача",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
        )
        /*
        Text(
            modifier = Modifier.padding(start = 10.dp, end = 8.dp, bottom = 16.dp),
            text = "Создайте новую задачу",
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        */
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
                                    Task(
                                        title = viewModel.taskTitleState.trim(),
                                        curRepetitions = viewModel.taskRepetitions,
                                        repetitions = viewModel.taskRepetitions
                                    )
                                )
                                scope.launch {
                                    navController.navigateUp()
                                }
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Количество повторений:")
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
    }
}
/*
@Composable
fun TaskTextField(
    modifier: Modifier,
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label) },
        shape = CircleShape,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}
*/