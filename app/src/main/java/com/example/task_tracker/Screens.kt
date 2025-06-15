package com.example.task_tracker

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AddTaskScreen : Screen("add_task_screen")
}