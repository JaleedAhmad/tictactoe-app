package com.example.tictactoe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tictactoe.R
import com.example.tictactoe.viewmodel.Difficulty
import com.example.tictactoe.viewmodel.GameMode
import com.example.tictactoe.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeSelectionScreen(
    viewModel: GameViewModel,
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showDifficultyDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 48.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(R.string.select_mode),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(64.dp))

            ModeButton(
                title = stringResource(R.string.pvp_mode),
                icon = Icons.Default.Person,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = {
                    viewModel.setGameMode(GameMode.PVP)
                    onNavigateToGame()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ModeButton(
                title = stringResource(R.string.pvc_mode),
                icon = Icons.Default.Build,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = {
                    showDifficultyDialog = true
                }
            )
        }
        }
    }

    // Difficulty picker dialog
    if (showDifficultyDialog) {
        AlertDialog(
            onDismissRequest = { showDifficultyDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.cpu_difficulty),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DifficultyOptionButton(
                        label = stringResource(R.string.difficulty_easy),
                        description = stringResource(R.string.easy_desc),
                        isSelected = viewModel.cpuDifficulty.value == Difficulty.EASY,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = {
                            viewModel.setCpuDifficulty(Difficulty.EASY)
                            viewModel.setGameMode(GameMode.PVC)
                            showDifficultyDialog = false
                            onNavigateToGame()
                        }
                    )
                    DifficultyOptionButton(
                        label = stringResource(R.string.difficulty_medium),
                        description = stringResource(R.string.medium_desc),
                        isSelected = viewModel.cpuDifficulty.value == Difficulty.MEDIUM,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = {
                            viewModel.setCpuDifficulty(Difficulty.MEDIUM)
                            viewModel.setGameMode(GameMode.PVC)
                            showDifficultyDialog = false
                            onNavigateToGame()
                        }
                    )
                    DifficultyOptionButton(
                        label = stringResource(R.string.difficulty_hard),
                        description = stringResource(R.string.hard_desc),
                        isSelected = viewModel.cpuDifficulty.value == Difficulty.HARD,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        onClick = {
                            viewModel.setCpuDifficulty(Difficulty.HARD)
                            viewModel.setGameMode(GameMode.PVC)
                            showDifficultyDialog = false
                            onNavigateToGame()
                        }
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDifficultyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DifficultyOptionButton(
    label: String,
    description: String,
    isSelected: Boolean,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    val borderStroke = if (isSelected) {
        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else null

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.medium,
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ModeButton(
    title: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.large,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
