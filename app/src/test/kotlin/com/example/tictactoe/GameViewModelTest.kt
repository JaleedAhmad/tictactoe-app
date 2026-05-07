package com.example.tictactoe

import com.example.tictactoe.viewmodel.CellState
import com.example.tictactoe.viewmodel.GameViewModel
import com.example.tictactoe.viewmodel.Player
import com.example.tictactoe.viewmodel.GameStatus
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var viewModel: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dataStore = mockk(relaxed = true)
        every { dataStore.data } returns flowOf(emptyPreferences())
        viewModel = GameViewModel(dataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() {
        assertEquals(Player.X, viewModel.currentPlayer.value)
        assertTrue(viewModel.gameStatus.value is GameStatus.PlayerTurn)
        assertEquals(Player.X, (viewModel.gameStatus.value as GameStatus.PlayerTurn).player)
        assert(viewModel.board.all { it == CellState.EMPTY })
    }

    @Test
    fun `making a move updates board and switches player`() {
        viewModel.makeMove(0)
        assertEquals(CellState.X, viewModel.board[0])
        assertEquals(Player.O, viewModel.currentPlayer.value)
        assertTrue(viewModel.gameStatus.value is GameStatus.PlayerTurn)
        assertEquals(Player.O, (viewModel.gameStatus.value as GameStatus.PlayerTurn).player)
    }

    @Test
    fun `winning move updates score and status`() {
        // X moves
        viewModel.makeMove(0) // X at 0
        viewModel.makeMove(3) // O at 3
        viewModel.makeMove(1) // X at 1
        viewModel.makeMove(4) // O at 4
        viewModel.makeMove(2) // X at 2 -> Wins!

        assertTrue(viewModel.gameStatus.value is GameStatus.PlayerWins)
        assertEquals(Player.X, (viewModel.gameStatus.value as GameStatus.PlayerWins).player)
        assertEquals(1, viewModel.xScore.value)
    }

    @Test
    fun `draw game detection`() {
        // X O X
        // X X O
        // O X O
        val moves = listOf(0, 1, 2, 5, 3, 6, 4, 8, 7)
        moves.forEach { viewModel.makeMove(it) }

        assertTrue(viewModel.gameStatus.value is GameStatus.Draw)
    }

    @Test
    fun `reset game clears board`() {
        viewModel.makeMove(0)
        viewModel.resetGame()
        assert(viewModel.board.all { it == CellState.EMPTY })
        assertEquals(Player.X, viewModel.currentPlayer.value)
        assertTrue(viewModel.gameStatus.value is GameStatus.PlayerTurn)
    }
}
