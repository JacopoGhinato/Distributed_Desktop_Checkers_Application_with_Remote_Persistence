//Game.kt
package isel.tds.checkers.isel.tds.checkers.model


data class Game(val board: Board? = null) {
    fun newBoard(): Game = Game(
        board = BoardRun(cells = BoardRun.initializeBoard(), turn = Player.W), // Player.W starts the game
    )
}

fun Game.play(from: Position, to: Position): Game {
    checkNotNull(board) { "Match not started" }
    //do the play
    val newBoard = board.play(from, to)
    // Update the state of the game
    return copy(
        board = newBoard,
    )
}

