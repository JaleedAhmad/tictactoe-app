package com.example.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.viewmodel.GameStatus
import com.example.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineMatchmakingScreen(
    viewModel: GameViewModel,
    onNavigateToGame: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var joinCode by remember { mutableStateOf("") }
    val isConnected = viewModel.isConnected.value
    val roomCode = viewModel.roomCode.value
    val isHost = viewModel.isHost.value
    val connectionError = viewModel.connectionError.value
    val gameStatus = viewModel.gameStatus.value

    // Navigate to game screen once connected and both players are ready
    LaunchedEffect(isConnected, gameStatus) {
        if (isConnected && gameStatus is GameStatus.PlayerTurn) {
            onNavigateToGame()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Play Online") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.leaveGame()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (connectionError.isNotEmpty()) {
                Text(
                    text = connectionError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (viewModel.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text(
                    text = if (isHost) "Creating room..." else "Joining game...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.leaveGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancel")
                }
            } else if (isConnected && roomCode.isNotEmpty()) {
                // Waiting screen
                Text(
                    text = "Room Code:",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = roomCode,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                if (isHost) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text(
                        text = "Waiting for friend to join...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { viewModel.leaveGame() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancel")
                }
                
            } else {
                // Host or Join options
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Host a Game",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Create a room and share the code",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.hostGame() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Room")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("OR", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Join a Game",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = joinCode,
                            onValueChange = { joinCode = it.uppercase().take(5) },
                            label = { Text("Enter Room Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.joinGame(joinCode) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = joinCode.length == 5
                        ) {
                            Text("Join Room")
                        }
                    }
                }
            }
        }
    }
}
