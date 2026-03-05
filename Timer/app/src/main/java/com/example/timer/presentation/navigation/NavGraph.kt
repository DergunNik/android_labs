package com.example.timer.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.timer.presentation.screen.EditScreen
import com.example.timer.presentation.screen.MainScreen
import com.example.timer.presentation.screen.SettingsScreen
import com.example.timer.presentation.screen.TimerScreen

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(
                onNavigateToTimer = { id ->
                    navController.navigate(Screen.Timer.createRoute(id))
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.Edit.createRoute(id))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Timer.route,
            arguments = listOf(navArgument("sequenceId") { type = NavType.StringType })
        ) {
            TimerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument("sequenceId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            EditScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    androidx.compose.material3.Text(text = title, modifier = androidx.compose.ui.Modifier.padding(16.dp))
}