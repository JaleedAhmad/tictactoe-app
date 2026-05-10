package com.example.tictactoe.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tictactoe.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object ModeSelection : Screen("mode_selection")
    object Game : Screen("game")
    object History : Screen("history")
    object Settings : Screen("settings")
    object OnlineMatchmaking : Screen("online_matchmaking")
}

@Composable
fun TicTacToeNavHost(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.ModeSelection.route
    ) {
        composable(Screen.ModeSelection.route) {
            ModeSelectionScreen(
                viewModel = viewModel,
                onNavigateToGame = { navController.navigate(Screen.Game.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToOnline = { navController.navigate(Screen.OnlineMatchmaking.route) }
            )
        }
        composable(Screen.Game.route) {
            GameScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.OnlineMatchmaking.route) {
            OnlineMatchmakingScreen(
                viewModel = viewModel,
                onNavigateToGame = { navController.navigate(Screen.Game.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
