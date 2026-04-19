package com.example.task_tracker

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import com.example.task_tracker.data.data_store.DataStoreManager
import com.example.task_tracker.ui.screens.mainscreen.MainScreen
import com.example.task_tracker.ui.theme.Task_trackerTheme
import com.example.task_tracker.viewmodel.homevm.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //@Inject
    //lateinit var taskRepository: TaskRepository
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Task_trackerTheme {
                MainScreen(
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
