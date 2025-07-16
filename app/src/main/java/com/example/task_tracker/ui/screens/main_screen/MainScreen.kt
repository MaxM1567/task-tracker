package com.example.task_tracker.ui.screens.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.primarySurface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.task_tracker.Navigation
import com.example.task_tracker.ui.screens.Screen
import com.example.task_tracker.ui.screens.Screen.BottomScreen
import com.example.task_tracker.ui.screens.bottomScreens


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    Scaffold(
        bottomBar = {
            HomeBottomBar(navController = navController)
        }
    ) { padding ->
        Navigation(
            navController = navController,
            pd = padding
        )
    }
}


@Composable
fun HomeBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .background(color = Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape, elevation = 8.dp, modifier = Modifier
                .weight(1f)
                .padding(
                    end = when (currentRoute) {
                        BottomScreen.HomeScreen.route -> 16.dp
                        else -> 0.dp
                    }
                )
        ) {
            BottomNavigation(
                modifier = Modifier.clip(CircleShape), elevation = 0.dp
            ) {
                bottomScreens.forEach { item ->
                    BottomNavigationItem(
                        selected = false,
                        onClick = { navController.navigate(item.bRoute) },
                        icon = { Icon(imageVector = item.icon, contentDescription = null) })
                }
            }
        }

        if (currentRoute == BottomScreen.HomeScreen.route) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTaskScreen.route) },
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colors.primarySurface
            ) {
                Icon(Icons.Default.Add, "Add")
            }
        }
    }
}