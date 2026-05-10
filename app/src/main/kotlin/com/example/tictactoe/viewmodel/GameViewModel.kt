package com.example.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class Player { X, O }
enum class CellState { EMPTY, X, O }
enum class GameMode { PVP, PVC, ONLINE }
enum class Difficulty { EASY, MEDIUM, HARD }

sealed class GameStatus {
    object Initial : GameStatus()
    data class PlayerTurn(val player: Player) : GameStatus()
    data class PlayerWins(val player: Player) : GameStatus()
    object Draw : GameStatus()
}

data class MatchRecord(
    val winner: String,
    val timestamp: Long
)

class GameViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {

    var board = mutableStateOf<List<CellState>>(List(9) { CellState.EMPTY })
        private set

    var currentPlayer = mutableStateOf(Player.X)
        private set
    var xScore = mutableStateOf(0)
        private set
    var oScore = mutableStateOf(0)
        private set
    var gameStatus = mutableStateOf<GameStatus>(GameStatus.PlayerTurn(Player.X))
        private set

    var isLoading = mutableStateOf(false)
        private set
    
    var gameMode = mutableStateOf(GameMode.PVP)
        private set
    var playerXName = mutableStateOf("Player X")
        private set
    var playerOName = mutableStateOf("Player O")
        private set
    var isDarkMode = mutableStateOf<Boolean?>(null) // null means follow system
        private set
    var isSoundEnabled = mutableStateOf(true)
        private set
    var isVibrationEnabled = mutableStateOf(true)
        private set
    
    var cpuDifficulty = mutableStateOf(Difficulty.MEDIUM)
        private set

    // Online Multiplayer State
    var roomCode = mutableStateOf("")
        private set
    var isHost = mutableStateOf(false)
        private set
    var isConnected = mutableStateOf(false)
        private set
    var connectionError = mutableStateOf("")
        private set
    var onlinePlayer = mutableStateOf<Player?>(null) // Which player am I in the online match?
        private set
    
    private val firebaseManager = com.example.tictactoe.util.FirebaseManager()
    
    val matchHistory = mutableStateListOf<MatchRecord>()

    private val X_SCORE_KEY = intPreferencesKey("x_score")
    private val O_SCORE_KEY = intPreferencesKey("o_score")
    private val HISTORY_KEY = stringPreferencesKey("match_history")
    private val X_NAME_KEY = stringPreferencesKey("x_name")
    private val O_NAME_KEY = stringPreferencesKey("o_name")
    private val GAME_MODE_KEY = stringPreferencesKey("game_mode")
    private val DARK_MODE_KEY = stringPreferencesKey("dark_mode")
    private val SOUND_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("sound")
    private val VIBRATION_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("vibration")
    private val DIFFICULTY_KEY = stringPreferencesKey("cpu_difficulty")

    init {
        loadData()
        viewModelScope.launch {
            firebaseManager.messageFlow.collect { message ->
                handleNetworkMessage(message)
            }
        }
    }

    fun setGameMode(mode: GameMode) {
        if (gameMode.value == GameMode.ONLINE && mode != GameMode.ONLINE) {
            leaveGame()
        }
        gameMode.value = mode
        resetGame()
        saveData()
    }

    fun hostGame() {
        setGameMode(GameMode.ONLINE)
        isHost.value = true
        onlinePlayer.value = Player.X
        roomCode.value = (10000..99999).random().toString()
        isConnected.value = false
        connectionError.value = ""
        isLoading.value = true
        
        firebaseManager.connect(roomCode.value) { success, errorMsg ->
            isLoading.value = false
            if (success) {
                isConnected.value = true
                gameStatus.value = GameStatus.Initial // Waiting for opponent
            } else {
                connectionError.value = "Failed to connect: $errorMsg"
            }
        }
    }

    fun joinGame(code: String) {
        if (code.isBlank()) return
        setGameMode(GameMode.ONLINE)
        isHost.value = false
        onlinePlayer.value = Player.O
        roomCode.value = code
        isConnected.value = false
        connectionError.value = ""
        gameStatus.value = GameStatus.Initial // reset before connecting
        isLoading.value = true
        
        firebaseManager.connect(code) { success, errorMsg ->
            if (success) {
                // Tell the host we joined
                firebaseManager.sendMessage(com.example.tictactoe.util.GameMessage("JOIN", player = "O")) { messageSent ->
                    isLoading.value = false
                    if (messageSent) {
                        isConnected.value = true
                        gameStatus.value = GameStatus.PlayerTurn(Player.X)
                    } else {
                        connectionError.value = "Failed to send join message. Check database rules."
                    }
                }
            } else {
                isLoading.value = false
                connectionError.value = "Failed to connect: $errorMsg"
            }
        }
    }

    fun leaveGame() {
        if (isConnected.value) {
            firebaseManager.sendMessage(com.example.tictactoe.util.GameMessage("QUIT"))
            firebaseManager.disconnect()
        }
        isConnected.value = false
        roomCode.value = ""
        onlinePlayer.value = null
        if (gameMode.value == GameMode.ONLINE) {
            gameMode.value = GameMode.PVP
            resetGame()
        }
    }

    private fun handleNetworkMessage(message: com.example.tictactoe.util.GameMessage) {
        when (message.type) {
            "JOIN" -> {
                if (isHost.value) {
                    gameStatus.value = GameStatus.PlayerTurn(Player.X)
                }
            }
            "MOVE" -> {
                val playerWhoseMoveItWas = if (message.player == "X") Player.X else Player.O
                if (playerWhoseMoveItWas != onlinePlayer.value) {
                    executeMove(message.moveIndex, isNetworkMove = true)
                }
            }
            "RESTART" -> {
                resetGame(isNetworkReset = true)
            }
            "QUIT" -> {
                leaveGame()
                connectionError.value = "Opponent disconnected"
            }
        }
    }

    fun updatePlayerNames(xName: String, oName: String) {
        playerXName.value = xName.ifBlank { "Player X" }
        playerOName.value = oName.ifBlank { "Player O" }
        saveData()
    }

    fun toggleDarkMode(enabled: Boolean) {
        isDarkMode.value = enabled
        saveData()
    }

    fun toggleSound(enabled: Boolean) {
        isSoundEnabled.value = enabled
        saveData()
    }

    fun toggleVibration(enabled: Boolean) {
        isVibrationEnabled.value = enabled
        saveData()
    }

    fun setCpuDifficulty(difficulty: Difficulty) {
        cpuDifficulty.value = difficulty
        saveData()
    }

    fun makeMove(index: Int) {
        val currentStatus = gameStatus.value
        if (index !in 0..8 || board.value[index] != CellState.EMPTY || currentStatus is GameStatus.PlayerWins || currentStatus is GameStatus.Draw) return
        
        // Block player move if it's CPU's turn in PVC mode
        if (gameMode.value == GameMode.PVC && currentPlayer.value == Player.O) return
        
        // Block player move if it's not their turn in ONLINE mode
        if (gameMode.value == GameMode.ONLINE && currentPlayer.value != onlinePlayer.value) return
        if (gameMode.value == GameMode.ONLINE && !isConnected.value) return
        if (gameMode.value == GameMode.ONLINE && currentStatus == GameStatus.Initial) return // Waiting for opponent

        executeMove(index, isNetworkMove = false)
    }

    private fun executeMove(index: Int, isNetworkMove: Boolean = false) {
        if (gameMode.value == GameMode.ONLINE && !isNetworkMove) {
            firebaseManager.sendMessage(com.example.tictactoe.util.GameMessage("MOVE", moveIndex = index, player = currentPlayer.value.name))
        }
        val newBoard = board.value.toMutableList()
        newBoard[index] = if (currentPlayer.value == Player.X) CellState.X else CellState.O
        board.value = newBoard
        
        if (checkWinner()) {
            val winnerName = if (currentPlayer.value == Player.X) playerXName.value else playerOName.value
            if (currentPlayer.value == Player.X) xScore.value++ else oScore.value++
            
            board.value = List(9) { CellState.EMPTY }
            gameStatus.value = GameStatus.PlayerWins(currentPlayer.value)
            recordMatch(winnerName)
            saveData()
        } else if (board.value.none { it == CellState.EMPTY }) {
            board.value = List(9) { CellState.EMPTY }
            gameStatus.value = GameStatus.Draw
            recordMatch("Draw")
            saveData()
        } else {
            currentPlayer.value = if (currentPlayer.value == Player.X) Player.O else Player.X
            gameStatus.value = GameStatus.PlayerTurn(currentPlayer.value)
            
            // Trigger CPU move if in PVC mode and it's O's turn
            if (gameMode.value == GameMode.PVC && currentPlayer.value == Player.O) {
                viewModelScope.launch {
                    delay(600) // Small delay for better UX
                    val cpuIndex = getBestMove()
                    if (cpuIndex != -1) executeMove(cpuIndex)
                }
            }
        }
    }

    private fun getBestMove(): Int {
        return when (cpuDifficulty.value) {
            Difficulty.EASY -> {
                val emptyIndices = board.value.indices.filter { board.value[it] == CellState.EMPTY }
                if (emptyIndices.isNotEmpty()) emptyIndices.random() else -1
            }
            Difficulty.MEDIUM -> {
                // Current smart but not perfect logic
                val winMove = findWinningMove(CellState.O)
                if (winMove != -1) return winMove
                
                val blockMove = findWinningMove(CellState.X)
                if (blockMove != -1) return blockMove
                
                if (board.value[4] == CellState.EMPTY) return 4
                
                val emptyIndices = board.value.indices.filter { board.value[it] == CellState.EMPTY }
                if (emptyIndices.isNotEmpty()) emptyIndices.random() else -1
            }
            Difficulty.HARD -> {
                minimax(board.value, CellState.O).index
            }
        }
    }

    private data class Move(val index: Int, val score: Int)

    private fun minimax(currentBoard: List<CellState>, player: CellState): Move {
        val availableIndices = currentBoard.indices.filter { currentBoard[it] == CellState.EMPTY }

        // Check terminal states
        if (checkWin(currentBoard, CellState.X)) return Move(-1, -10)
        if (checkWin(currentBoard, CellState.O)) return Move(-1, 10)
        if (availableIndices.isEmpty()) return Move(-1, 0)

        val moves = mutableListOf<Move>()

        for (index in availableIndices) {
            val nextBoard = currentBoard.toMutableList()
            nextBoard[index] = player
            
            val score = if (player == CellState.O) {
                minimax(nextBoard, CellState.X).score
            } else {
                minimax(nextBoard, CellState.O).score
            }
            
            moves.add(Move(index, score))
        }

        return if (player == CellState.O) {
            moves.maxBy { it.score }
        } else {
            moves.minBy { it.score }
        }
    }

    private fun checkWin(b: List<CellState>, p: CellState): Boolean {
        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        return lines.any { line -> line.all { b[it] == p } }
    }

    private fun findWinningMove(playerCell: CellState): Int {
        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        for (line in lines) {
            val states = line.map { board.value[it] }
            if (states.count { it == playerCell } == 2 && states.count { it == CellState.EMPTY } == 1) {
                return line[states.indexOf(CellState.EMPTY)]
            }
        }
        return -1
    }

    private fun recordMatch(winner: String) {
        val record = MatchRecord(winner, System.currentTimeMillis())
        matchHistory.add(0, record)
        saveData()
    }

    private fun checkWinner(): Boolean {
        val lines = listOf(
            listOf(0,1,2), listOf(3,4,5), listOf(6,7,8),
            listOf(0,3,6), listOf(1,4,7), listOf(2,5,8),
            listOf(0,4,8), listOf(2,4,6)
        )
        for (line in lines) {
            val (a,b,c) = line
            if (board.value[a] != CellState.EMPTY && board.value[a] == board.value[b] && board.value[a] == board.value[c]) return true
        }
        return false
    }

    fun resetGame(isNetworkReset: Boolean = false) {
        if (gameMode.value == GameMode.ONLINE && !isNetworkReset && isConnected.value) {
            firebaseManager.sendMessage(com.example.tictactoe.util.GameMessage("RESTART"))
        }
        board.value = List(9) { CellState.EMPTY }
        currentPlayer.value = Player.X
        
        if (gameMode.value == GameMode.ONLINE) {
            if (isConnected.value) {
                // If we're host, and someone is joined, or if we joined
                gameStatus.value = if (isHost.value && onlinePlayer.value == Player.X) GameStatus.Initial else GameStatus.PlayerTurn(Player.X) 
                // Wait, if it's a restart, we both know we are connected.
                gameStatus.value = GameStatus.PlayerTurn(Player.X)
            } else {
                gameStatus.value = GameStatus.Initial
            }
        } else {
            gameStatus.value = GameStatus.PlayerTurn(Player.X)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val prefs = dataStore.data.first()
                xScore.value = prefs[X_SCORE_KEY] ?: 0
                oScore.value = prefs[O_SCORE_KEY] ?: 0
                playerXName.value = prefs[X_NAME_KEY] ?: "Player X"
                playerOName.value = prefs[O_NAME_KEY] ?: "Player O"
                gameMode.value = GameMode.valueOf(prefs[GAME_MODE_KEY] ?: GameMode.PVP.name)
                
                val darkModeStr = prefs[DARK_MODE_KEY]
                isDarkMode.value = if (darkModeStr == null) null else darkModeStr == "true"
                
                isSoundEnabled.value = prefs[SOUND_KEY] ?: true
                isVibrationEnabled.value = prefs[VIBRATION_KEY] ?: true
                
                cpuDifficulty.value = Difficulty.valueOf(prefs[DIFFICULTY_KEY] ?: Difficulty.MEDIUM.name)
                
                val historyStr = prefs[HISTORY_KEY] ?: ""
                if (historyStr.isNotEmpty()) {
                    val records = historyStr.split("|").mapNotNull {
                        val parts = it.split(";")
                        if (parts.size == 2) {
                            MatchRecord(parts[0], parts[1].toLong())
                        } else null
                    }
                    matchHistory.clear()
                    matchHistory.addAll(records)
                }
            } catch (e: Exception) {}
        }
    }

    private fun saveData() {
        viewModelScope.launch {
            try {
                dataStore.edit { pref ->
                    pref[X_SCORE_KEY] = xScore.value
                    pref[O_SCORE_KEY] = oScore.value
                    pref[X_NAME_KEY] = playerXName.value
                    pref[O_NAME_KEY] = playerOName.value
                    pref[GAME_MODE_KEY] = gameMode.value.name
                    pref[DARK_MODE_KEY] = isDarkMode.value?.toString() ?: ""
                    pref[SOUND_KEY] = isSoundEnabled.value
                    pref[VIBRATION_KEY] = isVibrationEnabled.value
                    pref[DIFFICULTY_KEY] = cpuDifficulty.value.name
                    val historyStr = matchHistory.joinToString("|") { "${it.winner};${it.timestamp}" }
                    pref[HISTORY_KEY] = historyStr
                }
            } catch (e: Exception) {}
        }
    }

    fun clearHistory() {
        matchHistory.clear()
        saveData()
    }

    fun resetAllData() {
        viewModelScope.launch {
            dataStore.edit { it.clear() }
            xScore.value = 0
            oScore.value = 0
            playerXName.value = "Player X"
            playerOName.value = "Player O"
            cpuDifficulty.value = Difficulty.MEDIUM
            matchHistory.clear()
            resetGame()
        }
    }
}
