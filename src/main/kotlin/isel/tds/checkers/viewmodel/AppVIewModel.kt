//AppViewModel
package org.example.isel.tds.checkers.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import isel.tds.checkers.isel.tds.checkers.model.*
import isel.tds.checkers.isel.tds.checkers.storage.BoardSerializer
import isel.tds.checkers.isel.tds.checkers.storage.GameSerializer
import kotlinx.coroutines.*
import org.example.isel.tds.checkers.model.Name
import org.example.isel.tds.checkers.storage.MongoDriver
import org.example.isel.tds.checkers.storage.MongoStorage
import org.example.isel.tds.checkers.ui.InputName

class AppViewModel(driver: MongoDriver) {

    private val boardSerializer = BoardSerializer()
    private val gameSerializer = GameSerializer(boardSerializer)
    private val storage = MongoStorage<String, Game>("games", driver, gameSerializer)
    var processTurn: Player? by mutableStateOf(null)
    var inputName by mutableStateOf<InputName?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)

    var currentGame: Game? by mutableStateOf(null)
    var currentName: String? = null

    val board: Board? get() = currentGame?.board

    val isWaiting: Boolean get() = waitingJob != null
    var waitingJob by mutableStateOf<Job?>(null)

    var autoRefreshEnabled: Boolean by mutableStateOf(true)

    fun hideError() {
        errorMessage = null
    }

    private fun exec(action: () -> Unit) {
        try {
            action()
        } catch (e: Exception) {
            errorMessage = e.message
        }
    }

    fun play(from: Position, to: Position) {
        exec {
            if (board?.turn != processTurn) {
                throw IllegalStateException("Not your turn! You are: $processTurn")
            }
            currentGame?.let { game ->
                val updatedGame = game.play(from, to) // Make the play
                currentGame = updatedGame
                saveGame(updatedGame) // Save on MongoDB
            } ?: throw IllegalStateException("No game running!")
        }
        if (autoRefreshEnabled) { // Only wait for the other side if auto-refresh is enabled
            waitForOtherSide()
        }
    }

    fun refresh() {
        exec {
            currentName?.let { name ->
                loadGame(name)
            } ?: throw IllegalStateException("No game playing!")
        }
    }

    fun openStartDialog() {
        inputName = InputName.ForStart
    }

    fun closeStartOrJoinDialog() {
        //println("Closing start/join dialog...") //DEBUG
        inputName = null }

    fun onExit() {
        exec {
            val name = currentName
            if (name != null) {
                storage.delete(name)
                val winner = when (processTurn) {
                    Player.W -> Player.B
                    Player.B -> Player.W
                    else -> null
                }
                println("Game $name deleted. Winner: $winner") //DEBUG
            } else {
                println("No Game to be deleted") //DEBUG
            }
        }
    }

    fun start(name: Name) {
        cancelInput()
        exec {
            val game = Game().newBoard()
            processTurn = Player.W //
            storage.create(name.value, game)
            currentGame = game
            currentName = name.value
            //println("Game started with name: $currentName")
        }
        if (autoRefreshEnabled) { // Only wait for the other side if auto-refresh is enabled
            waitForOtherSide()
        }
    }

    fun join(name: Name) {
        cancelInput()
        exec {
            val game = storage.read(name.value)
            if (game == null) {
                throw IllegalStateException("Game not found!")
            }
            processTurn = Player.B
            currentGame = game
            currentName = name.value
            //println("Game joined with name: $currentName") // Debug log
        }
        if (autoRefreshEnabled) { // Only wait for the other side if auto-refresh is enabled
            waitForOtherSide()
        }
    }

    fun cancelInput() {
        inputName = null
    }

    private fun saveGame(game: Game) {
        val name = currentName ?: throw IllegalStateException("No game specified")
        storage.update(name, game)
        currentGame = game
    }

    private fun loadGame(name: String) {
        val game = storage.read(name) ?: throw IllegalStateException("-Game: $name not found \n-The other player left (YOU ARE THE WINNER!)")
        currentGame = game
        currentName = name
    }

    fun startOrJoin(name :Name){
            try {
                if (storage.read(name.value) == null) {
                    println("STARTING GAME...")
                    start(name)
                } else {
                    println("JOINING GAME...")
                    join(name)
                }
                closeStartOrJoinDialog()
            } catch (e: Exception) {
                errorMessage = e.message
            }

    }

    fun waitForOtherSide() {
        // If it's the current player's turn, no need to wait
        if (board?.turn == processTurn) return

        // Cancel any existing waiting job to avoid overlapping
        waitingJob?.cancel()

        // Start a new coroutine for periodic refresh
        waitingJob = CoroutineScope(Dispatchers.IO).launch {
            do {
                delay(200) // Wait for tot seconds
                try {
                    refresh() // Attempt to refresh the game state
                } catch (e: NoChangesException) {
                } catch (e: Exception) {
                    errorMessage = e.message
                    if (e is GameDeletedException) {
                        currentGame = null // Reset the game if it has been deleted
                    }
                }
            } while (board?.turn != processTurn) // Keep checking until it's the player's turn

            waitingJob = null // Clear the job when done
        }
    }

}

class NoChangesException : IllegalStateException("No changes")
class GameDeletedException : IllegalStateException("Game deleted")
