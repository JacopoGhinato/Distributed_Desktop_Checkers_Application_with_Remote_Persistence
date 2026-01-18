//BoardSerializer.kt
package isel.tds.checkers.isel.tds.checkers.storage

import isel.tds.checkers.isel.tds.checkers.model.Board
import isel.tds.checkers.isel.tds.checkers.model.BoardRun
import isel.tds.checkers.isel.tds.checkers.model.Player


class BoardSerializer {
    // Serializes the Board object to a string
    fun serialize(board: Board): String {
        val turn = board.turn.name
        val cells = board.cells.joinToString(",") { it?.name ?: "null" }
        return "$turn|$cells"  // Use a delimitator to seprate turn and cells
    }

    // Deserializes a string into a BoardRun object
    fun deserialize(data: String): BoardRun {
        val parts = data.split("|")
        val turn = Player.valueOf(parts[0])  // First element is turn
        val cells = parts[1].split(",").map { if (it == "null") null else Player.valueOf(it) }
        return BoardRun(cells, turn)  // Ricreate BoardRun with the right turn
    }
}