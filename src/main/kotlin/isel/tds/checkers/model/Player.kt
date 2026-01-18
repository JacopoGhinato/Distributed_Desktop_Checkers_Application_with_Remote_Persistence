//Player.kt
package isel.tds.checkers.isel.tds.checkers.model

enum class Player(val symbol: Char) {
    W('w'),   // White player with symbol 'w'
    B('b'),   // Black player with symbol 'b'
    W_Q('W'),  // White queen with symbol 'W'
    B_Q('B'),  // Black queen with symbol 'B'
    NONE(' ') ;// empty cells

    // Returns the opposite player
    fun other(): Player = when (this) {
       // W -> B
       // B -> W
        W, W_Q -> B
        B, B_Q -> W
        NONE -> NONE
    }

    fun toKing(): Player = when (this) {
        W -> W_Q
        B -> B_Q
        else -> this // If it is already a "KING"/"QUEEN", return it as it is
    }
}
