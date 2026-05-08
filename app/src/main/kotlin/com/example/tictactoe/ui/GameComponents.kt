package com.example.tictactoe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tictactoe.R
import com.example.tictactoe.viewmodel.GameViewModel
import com.example.tictactoe.viewmodel.CellState
import com.example.tictactoe.viewmodel.GameStatus
import com.example.tictactoe.viewmodel.Player

@Composable
fun GameBoard(viewModel: GameViewModel, onMoveMade: () -> Unit = {}) {
    Surface(
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            for (row in 0 until 3) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    for (col in 0 until 3) {
                        val index = row * 3 + col
                        Cell(
                            state = viewModel.board.value[index],
                            onClick = {
                                val wasEmpty = viewModel.board.value[index] == CellState.EMPTY
                                viewModel.makeMove(index)
                                // Only trigger feedback if the cell was actually empty and the move was valid
                                if (wasEmpty && viewModel.board.value[index] != CellState.EMPTY) {
                                    onMoveMade()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Cell(state: CellState, onClick: () -> Unit) {
    val containerColor = when (state) {
        CellState.EMPTY -> MaterialTheme.colorScheme.surface
        CellState.X -> MaterialTheme.colorScheme.primaryContainer
        CellState.O -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when (state) {
        CellState.EMPTY -> Color.Transparent
        CellState.X -> MaterialTheme.colorScheme.primary
        CellState.O -> MaterialTheme.colorScheme.secondary
        else -> Color.Transparent
    }
    val borderColor = if (state == CellState.EMPTY) {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        Color.Transparent
    }

    Surface(
        modifier = Modifier
            .size(100.dp)
            .clickable(enabled = state == CellState.EMPTY, onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        border = if (state == CellState.EMPTY) androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null,
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = when (state) {
                    CellState.X -> "X"
                    CellState.O -> "O"
                    else -> ""
                },
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 40.sp,
                    color = contentColor
                )
            )
        }
    }
}

@Composable
fun StatusToast(viewModel: GameViewModel) {
    val status = viewModel.gameStatus.value
    val message = when (status) {
        is GameStatus.PlayerTurn -> {
            val name = if (status.player == Player.X) viewModel.playerXName.value else viewModel.playerOName.value
            stringResource(R.string.turn, name)
        }
        else -> ""
    }

    if (message.isNotEmpty()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ResetButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 32.dp)
    ) {
        Text(
            text = stringResource(R.string.reset),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 18.sp)
        )
    }
}

@Composable
fun ResultOverlay(
    viewModel: GameViewModel,
    onPlayAgain: () -> Unit
) {
    val status = viewModel.gameStatus.value
    if (status is GameStatus.PlayerWins || status is GameStatus.Draw) {
        Dialog(onDismissRequest = { /* Don't dismiss */ }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when (status) {
                            is GameStatus.PlayerWins -> {
                                val name = if (status.player == Player.X) viewModel.playerXName.value else viewModel.playerOName.value
                                stringResource(R.string.wins, name)
                            }
                            else -> stringResource(R.string.draw)
                        },
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(stringResource(R.string.play_again))
                    }
                }
            }
        }
    }
}
