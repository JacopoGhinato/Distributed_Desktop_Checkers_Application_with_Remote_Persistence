//Position.kt
package isel.tds.checkers.isel.tds.checkers.model

@JvmInline
value class Position private constructor(val index: Int) {

    val row: Int get() = index / BOARD_SIZE     //with this I can get the number of the row (in case I need it)
    val col: Int get() = index % BOARD_SIZE
    override fun toString() = "$index"

    companion object {
        val values = List(BOARD_CELLS) { Position(it) }

        operator fun invoke(index: Int): Position { //checking if the move is made inside the limits
            require(index in 0 until BOARD_CELLS) { "Index must be within bounds" } // Add this line for bounds checking
            return values[index]
        }
    }
}

fun Position(row: Int, col: Int): Position {
    require(row in 0 until BOARD_SIZE && col in 0 until BOARD_SIZE)
    return Position(row * BOARD_SIZE + col)
}

