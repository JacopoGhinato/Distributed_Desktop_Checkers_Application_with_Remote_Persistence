// GameSerializer.kt
package isel.tds.checkers.isel.tds.checkers.storage

import isel.tds.checkers.isel.tds.checkers.model.Game
import isel.tds.checkers.isel.tds.checkers.model.Player

class GameSerializer(private val boardSerializer: BoardSerializer): Serializer<Game> {
    override fun serialize(game: Game): String {
        val boardData = game.board?.let { boardSerializer.serialize(it) } ?: "null"
        return "$boardData"  // Saves Board data
    }

    override fun deserialize(data: String): Game {
        val board = if (data == "null") null else boardSerializer.deserialize(data)
        return Game(board = board)
    }
}