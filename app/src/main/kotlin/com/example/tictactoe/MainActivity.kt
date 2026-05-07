package com.example.tictactoe

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.tictactoe.viewmodel.GameViewModel
import com.example.tictactoe.ui.theme.TicTacToeTheme
import com.example.tictactoe.ui.TicTacToeNavHost

private val Context.dataStore by preferencesDataStore(name = "tictactoe_prefs")

class MainActivity : ComponentActivity() {
    
    private val viewModel: GameViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(dataStore) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by viewModel.isDarkMode
            val darkTheme = isDarkMode ?: isSystemInDarkTheme()
            
            TicTacToeTheme(darkTheme = darkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    TicTacToeNavHost(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
