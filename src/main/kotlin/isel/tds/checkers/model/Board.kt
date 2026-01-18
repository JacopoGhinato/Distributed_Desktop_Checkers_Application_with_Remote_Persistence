//board.kt
package isel.tds.checkers.isel.tds.checkers.model
import kotlin.math.absoluteValue

const val BOARD_SIZE = 8
const val BOARD_CELLS = BOARD_SIZE * BOARD_SIZE

sealed class Board(val cells: List<Player?>, var turn: Player) {
    abstract fun play(from: Position, to: Position): Board
    abstract fun isGameOver(): Boolean
    abstract fun isValidMove(from: Position, to: Position): Boolean
    abstract fun getWinner(): Player?
    abstract fun getPossibleMoves(from: Position): List<Position>
}

class BoardRun(
    cells: List<Player?> = initializeBoard(),
    turn: Player
) : Board(cells, turn) {
    override fun getWinner(): Player? {
        val whiteCount = cells.count { it == Player.W || it == Player.W_Q }
        val blackCount = cells.count { it == Player.B || it == Player.B_Q }
        return when {
            whiteCount == 0 -> Player.B
            blackCount == 0 -> Player.W
            else -> null
        }
    }

    override fun play(from: Position, to: Position): Board {
        val fromIdx = from.index
        val toIdx = to.index
        if (!isOwnPiece(from)) {
            return this // Block the action
        }

        if (hasMandatoryCapture() && !canCapture(fromIdx, toIdx)) {
            //println("Mandatory Capture") //DEBUG
            return this // Block the movement
        }

        var newBoard = this

        if (canCapture(fromIdx, toIdx)) {
            newBoard = capture(fromIdx, toIdx)

            if (newBoard.hasAnotherCapture(toIdx)) {
                //println("Turn doesn't change: the player must do another capture") //DEBUG
                return newBoard // return without changing the turn
            } else {
                return newBoard
            }
        } else if (isValidMove(Position(fromIdx), Position(toIdx))) {
            return move(fromIdx, toIdx)
        } else {
            return this // Return current board if the move is not valid
        }
    }

    fun hasAnotherCapture(toIdx: Int): Boolean {
        val toPosition = Position(toIdx)
        val row = toPosition.row
        val col = toPosition.col
        val directions = listOf( //all the directions of capture(diagonal)
            Pair(-2, -2), Pair(-2, 2), // High Diagonals
            Pair(2, -2), Pair(2, 2)    // Low Diagonals
        )

        // Check if a capture is possibile in every direction
        for ((rowOffset, colOffset) in directions) {
            val captureRow = row + rowOffset
            val captureCol = col + colOffset

            // Checking if the destination cell is valid
            if (captureRow in 0 until BOARD_SIZE && captureCol in 0 until BOARD_SIZE) {
                val capturePos = Position(captureRow, captureCol)
                // middle position
                val middleRow = row + rowOffset / 2
                val middleCol = col + colOffset / 2
                val middlePos = Position(middleRow, middleCol)
                //CHeck if there is a piece in the middle and if a piece of the other player and if the destination cell is empty
                if (isOccupied(middlePos) && !isOwnPiece(middlePos) && cells[capturePos.index] == null) {
                    // Possible capture
                    return true
                }
            }
        }
        return false
    }

    private fun isValidPosition(pos: Position): Boolean {
        return pos.row in 0 until BOARD_SIZE && pos.col in 0 until BOARD_SIZE
    }

    private fun isOccupied(pos: Position): Boolean {
        return cells[pos.index] != null // Use pos.index to get the cell
    }

    private fun isOwnPiece(pos: Position): Boolean {
        val piece = cells[pos.index]
        // Check pieces and KING pieces
        return piece != null && (piece == turn || piece == turn.toKing())
    }

    private fun hasMandatoryCapture(): Boolean {
        cells.forEachIndexed { index, player ->
            if (player == turn || player == turn.toKing()) {
                val position = Position(index)
                if (hasAnotherCapture(index)) {
                    return true
                }
            }
        }
        return false
    }

    override fun getPossibleMoves(from: Position): List<Position> {
        val moves = mutableListOf<Position>()
        val piece = cells[from.index] ?: return moves // If the cell is empty, return no moves

        // If a capture is mandatory, only return the valid capture moves
        if (hasMandatoryCapture()) {
            // Only check for capture moves
            val directions = listOf(
                Pair(-2, -2), Pair(-2, 2), // High Diagonals
                Pair(2, -2), Pair(2, 2)    // Low Diagonals
            )

            // Iterate through possible capture directions
            for ((rowOffset, colOffset) in directions) {
                val row = from.row
                val col = from.col
                val captureRow = row + rowOffset
                val captureCol = col + colOffset

                // Check if the destination cell is valid
                if (captureRow in 0 until BOARD_SIZE && captureCol in 0 until BOARD_SIZE) {
                    val capturePos = Position(captureRow, captureCol)

                    // Calculate middle position
                    val middleRow = row + rowOffset / 2
                    val middleCol = col + colOffset / 2
                    val middlePos = Position(middleRow, middleCol)

                    // Check if the middle position contains an opponent's piece and if the capture destination is empty
                    if (isOccupied(middlePos) && !isOwnPiece(middlePos) && cells[capturePos.index] == null) {
                        // Add valid capture move
                        moves.add(capturePos)
                    }
                }
            }
        } else {
            // Normal move logic (allowing regular moves)
            val directions = when (piece) {
                Player.W, Player.B -> listOf(Pair(1, -1), Pair(1, 1), Pair(-1, -1), Pair(-1, 1))
                Player.W_Q, Player.B_Q -> (1..BOARD_SIZE).flatMap { dist ->
                    listOf(Pair(dist, dist), Pair(dist, -dist), Pair(-dist, dist), Pair(-dist, -dist))
                }

                else -> emptyList()
            }

            // Iterate through all possible positions for regular moves
            for (row in 0 until BOARD_SIZE) {
                for (col in 0 until BOARD_SIZE) {
                    val toPosition = Position(row, col)
                    if (isValidMove(from, toPosition)) {
                        moves.add(toPosition)
                    }
                }
            }
        }
        return moves
    }

    override fun isValidMove(from: Position, to: Position): Boolean {
        if (!isValidPosition(from) || !isValidPosition(to)) {
            println("Not valid Position: from $from to $to")
            return false
        }

        if (!isOwnPiece(from)) {
            //println("The piece at the from position $from is not owned by  current player.") //DEBUG
            return false
        }
        if (isOccupied(to)) {
            return false
        }
        val rowDiff = to.row - from.row
        val colDiff = to.col - from.col
        val piece = cells[from.index]
        if (piece == Player.W_Q || piece == Player.B_Q) {
            // Queen movement: check if the move is diagonal
            if (rowDiff.absoluteValue == colDiff.absoluteValue) {
                val stepRow = rowDiff / rowDiff.absoluteValue  // Direction of row
                val stepCol = colDiff / colDiff.absoluteValue  // Direction of column
                var currentRow = from.row + stepRow
                var currentCol = from.col + stepCol
                while (currentRow != to.row && currentCol != to.col) {
                    val currentPos = Position(currentRow, currentCol)
                    // If the cell is occupied by an opponent's piece, we allow a capture
                    if (isOccupied(currentPos)) {
                        // If this is an opponent's piece and the next square is empty, it’s a capture
                        if (!isOwnPiece(currentPos)) {
                            val nextPos = Position(currentRow + stepRow, currentCol + stepCol)
                            if (isValidPosition(nextPos) && !isOccupied(nextPos)) {
                                return true // Allow for capture
                            }
                        }
                        // Otherwise, it's a blocked cell
                        println("Movement not valid: cell ($currentRow, $currentCol) is occupied.")
                        return false
                    }
                    currentRow += stepRow
                    currentCol += stepCol
                }
                return true // Valid diagonal move for a queen
            }
        }
        // Restrict movement for non-queen pieces
        if (piece == Player.W && rowDiff >= 0) {
            return false
        }
        if (piece == Player.B && rowDiff <= 0) {
            return false
        }
        // Logic for regular pieces (1 diagonal step or capture)
        if (rowDiff.absoluteValue == 1 && colDiff.absoluteValue == 1) {
            return true
        }
        if (rowDiff.absoluteValue == 2 && colDiff.absoluteValue == 2) {
            val middleRow = from.row + rowDiff / 2
            val middleCol = from.col + colDiff / 2
            val middlePos = Position(middleRow, middleCol)

            if (isOccupied(middlePos) && !isOwnPiece(middlePos)) {
                return true
            }
        }
        return false
    }

    override fun isGameOver(): Boolean = getWinner() != null
    private fun move(from: Int, to: Int): Board {
        val newCells = cells.toMutableList()
        newCells[to] = cells[from]
        newCells[from] = null

        val toPosition = Position(to)
        if ((turn == Player.W && toPosition.row == 0)) {
            newCells[to] = Player.W_Q // Promote to white queen
        } else if ((turn == Player.B && toPosition.row == BOARD_SIZE - 1)) {
            newCells[to] = Player.B_Q // Promote to black queen
        }
        return BoardRun(newCells, turn.other())
    }

    private fun canCapture(from: Int, to: Int): Boolean {
        val over = (from + to) / 2
        if (!isValidPosition(over)) return false
        if (cells[over] == null || isOwnPiece(Position(over))) return false
        if (cells[to] != null) return false

        // Verifica che il salto sia su una diagonale corretta per la cattura
        val rowDiff = (to / BOARD_SIZE) - (from / BOARD_SIZE)
        val colDiff = (to % BOARD_SIZE) - (from % BOARD_SIZE)

        // Verifica per una pedina normale
        if (rowDiff.absoluteValue == 2 && colDiff.absoluteValue == 2) {
            return true
        }
        // Verify queen jump in diagonal
        val piece = cells[from]
        if (piece == Player.W_Q || piece == Player.B_Q) {
            val rowStep = rowDiff / rowDiff.absoluteValue
            val colStep = colDiff / colDiff.absoluteValue
            // Verify if in the diagonal there could be a capture
            var currentRow = (from / BOARD_SIZE) + rowStep
            var currentCol = (from % BOARD_SIZE) + colStep
            while (currentRow != (to / BOARD_SIZE) && currentCol != (to % BOARD_SIZE)) {
                val currentPos = Position(currentRow, currentCol)
                if (isOccupied(currentPos)) {
                    // If the cell has an enemy pieces its a possible capture
                    if (!isOwnPiece(currentPos)) {
                        val nextRow = currentRow + rowStep
                        val nextCol = currentCol + colStep
                        val nextPos = Position(nextRow, nextCol)
                        if (isValidPosition(nextPos) && !isOccupied(nextPos)) {
                            return true
                        }
                    }
                    break // If there is not an enemy piece
                }
                currentRow += rowStep
                currentCol += colStep
            }
        }
        return false
    }

    private fun capture(from: Int, to: Int): BoardRun {
        val newCells = cells.toMutableList()
        val over = (from + to) / 2 // Idx of the opponent piece capture
        newCells[to] = cells[from]
        newCells[from] = null
        newCells[over] = null

        // Check for promotion after capture
        val toPosition = Position(to)
        if ((turn == Player.W && toPosition.row == 0)) {
            newCells[to] = Player.W_Q // Promote
        } else if ((turn == Player.B && toPosition.row == BOARD_SIZE - 1)) {
            newCells[to] = Player.B_Q // Promote
        }
        //Another capture?
        val nextTurn = if (hasAnotherCapture(to)) turn else turn.other()
        return BoardRun(newCells, nextTurn)
    }

    companion object {
        fun initializeBoard(): List<Player?> {
            val initialSetup = listOf(
                null, Player.B, null, Player.B, null, Player.B, null, Player.B,
                Player.B, null, Player.B, null, Player.B, null, Player.B, null,
                null, Player.B, null, Player.B, null, Player.B, null, Player.B,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null,
                null, Player.W, null, Player.W, null, Player.W, null, Player.W,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null
            )
            //FINAL SETUP:  //I use it to start the game in a more advanced phase(USED TO CHECK SOME MOVEMENTS)
            val finalboard = listOf(
                null, null, null, null, null, null, null, null,
                null, null, Player.B_Q, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, Player.W, null, null,
                null, null, Player.B, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            )

            //return finalboard
            return initialSetup
        }
    }

    private fun isValidPosition(pos: Int): Boolean = pos in 0 until BOARD_CELLS
}
class BoardDraw(cells: List<Player?>) : Board(cells, Player.NONE) {
    override fun play(from: Position, to: Position): Board = this
    override fun isGameOver(): Boolean = true
    override fun isValidMove(from: Position, to: Position): Boolean {
        return false // No valid moves in a drawn state
    }
    override fun getWinner(): Player? = null
    override fun getPossibleMoves(from: Position): List<Position> {
        return emptyList()
    }
}
