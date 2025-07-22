package com.example.task_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.task_tracker.data.TaskRepository
import com.example.task_tracker.ui.screens.main_screen.MainScreen
import com.example.task_tracker.ui.theme.Task_trackerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var taskRepository: TaskRepository

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task_trackerTheme {
                val viewModel: TaskViewModel = hiltViewModel()

                MainScreen(
                    viewModel = viewModel,
                    navController = rememberNavController()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateUI()
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.updateUI()
    }
}
