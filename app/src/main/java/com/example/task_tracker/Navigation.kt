package com.example.task_tracker

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.task_tracker.ui.screens.AddTaskScreen
import com.example.task_tracker.ui.screens.Screen
import com.example.task_tracker.ui.screens.Screen.BottomScreen
import com.example.task_tracker.ui.screens.SettingScreen
import com.example.task_tracker.ui.screens.home.HomeScreen


@Composable
fun Navigation(
    viewModel: TaskViewModel,
    navController: NavController,
    pd: PaddingValues
) {
    NavHost(
        navController = navController as NavHostController,
        startDestination = BottomScreen.HomeScreen.route,
        modifier = Modifier.padding(pd),
        enterTransition = { fadeIn(animationSpec = tween(0)) },
        exitTransition = { fadeOut(animationSpec = tween(0)) }
    ) {
        composable(route = BottomScreen.HomeScreen.route) {
            HomeScreen(viewModel = viewModel)
        }
        composable(route = Screen.AddTaskScreen.route) {
            AddTaskScreen(viewModel = viewModel, navController = navController)
        }
        composable(route = BottomScreen.SettingsScreen.route) {
            SettingScreen(viewModel = viewModel)
        }
    }
}