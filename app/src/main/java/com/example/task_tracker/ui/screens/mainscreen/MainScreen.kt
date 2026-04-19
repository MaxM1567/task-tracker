package com.example.task_tracker.ui.screens.mainscreen

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.task_tracker.Navigation
import com.example.task_tracker.ui.screens.mainscreen.component.HomeBottomBar
import com.example.task_tracker.viewmodel.homevm.TaskViewModel


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = { HomeBottomBar(navController = navController) }
    ) { padding ->
        Navigation(
            navController = navController,
            pd = padding
        )
    }
}