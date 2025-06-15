package com.example.task_tracker

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.task_tracker.ui.screens.AddTaskScreen
import com.example.task_tracker.ui.screens.home.HomeScreen


@Composable
fun Navigation(
    viewModel: TaskViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable(Screen.AddTaskScreen.route) {
            AddTaskScreen(viewModel = viewModel, navController = navController)
        }
    }
}