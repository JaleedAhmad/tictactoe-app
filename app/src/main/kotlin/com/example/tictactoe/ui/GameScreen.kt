package com.example.tictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.tictactoe.R
import com.example.tictactoe.viewmodel.GameStatus
import com.example.tictactoe.viewmodel.GameViewModel
import com.example.tictactoe.util.GameFeedbackManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Trigger win/draw feedback when game status changes
    LaunchedEffect(viewModel.gameStatus.value) {
        when (viewModel.gameStatus.value) {
            is GameStatus.PlayerWins -> {
                GameFeedbackManager.playWinFeedback(
                    context,
                    viewModel.isSoundEnabled.value,
                    viewModel.isVibrationEnabled.value
                )
            }
            is GameStatus.Draw -> {
                GameFeedbackManager.playDrawFeedback(
                    context,
                    viewModel.isSoundEnabled.value,
                    viewModel.isVibrationEnabled.value
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Menu",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List, 
                            contentDescription = "Match History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings, 
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ScoreBoard(viewModel = viewModel)
            
            Spacer(modifier = Modifier.weight(1f))
            
            GameBoard(
                viewModel = viewModel,
                onMoveMade = {
                    GameFeedbackManager.playMoveFeedback(
                        context,
                        viewModel.isSoundEnabled.value,
                        viewModel.isVibrationEnabled.value
                    )
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            StatusToast(viewModel = viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ResetButton(onReset = { viewModel.resetGame() })
            
            Spacer(modifier = Modifier.height(48.dp))
        }
        
        ResultOverlay(
            viewModel = viewModel,
            onPlayAgain = { viewModel.resetGame() }
        )
    }
}
