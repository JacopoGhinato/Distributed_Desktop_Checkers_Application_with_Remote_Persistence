package org.example.isel.tds.checkers

import isel.tds.checkers.isel.tds.checkers.model.Position
import isel.tds.checkers.isel.tds.checkers.model.BoardRun
import isel.tds.checkers.isel.tds.checkers.model.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTest {
    @Test
    fun `Test valid move and turn change`() {
        val sut = BoardRun(
            cells = listOf(
                null, Player.B, null, Player.B, null, Player.B, null, Player.B,
                Player.B, null, Player.B, null, Player.B, null, Player.B, null,
                null, Player.B, null, Player.B, null, Player.B, null, Player.B,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null,
                null, Player.W, null, Player.W, null, Player.W, null, Player.W,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null
            ),
            turn = Player.W // Initial turn is white
        )

        // Play from 'a3' (5,0) to 'b4' (4,1), is a valid play for white player
        val from = Position(5, 0) // a3
        val to = Position(4, 1)   // b4

        assertTrue(sut.isValidMove(from, to))

        val newBoard = sut.play(from, to)

        assertEquals(Player.B, newBoard.turn)

        assertTrue(newBoard.cells[4 * 8 + 1] == Player.W)
        assertTrue(newBoard.cells[5 * 8 + 0] == null)
    }

    @Test
    fun `Test capture move with consecutive captures`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, Player.W, null, null, null, null,
                null, null, null, null, Player.B, null, Player.B, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )
        val from = Position(2, 3)
        val to = Position(4, 5)
        val result = sut.play(from, to) as BoardRun
        assertEquals(Player.W, result.turn) // Check that the turn doesn't change
        assertNull(result.cells[from.index])
        assertEquals(Player.W, result.cells[to.index])
    }

    @Test
    fun `Test game over with all pieces of one player captured`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, Player.W, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.B
        )
        assertTrue(sut.isGameOver())
        assertEquals(Player.W, sut.getWinner())
    }

    @Test
    fun `Test consecutive captures are handled correctly`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, Player.W, null, null, null, null,
                null, null, null, null, Player.B, null, Player.B, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )
        val from = Position(2, 3) // c3
        val to = Position(4, 5)   // e5
        val result = sut.play(from, to) as BoardRun
        assertEquals(Player.W, result.turn) // Should stay the same for further captures

        val fromSecondCapture = Position(4, 5) // e5
        val toSecondCapture = Position(6, 3) // g3
        val finalResult = result.play(fromSecondCapture, toSecondCapture) as BoardRun
        assertEquals(Player.W, finalResult.turn)
    }

    @Test
    fun `Test move queen for many diagonal positions`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, Player.W_Q, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, Player.W_Q, null, null, Player.B, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )


        val from = Position(43)
        val to = Position(25)
        val result = sut.play(from, to) as BoardRun

        assertEquals(Player.W.toKing(), result.cells[to.index])
        assertNull(result.cells[from.index], "The original position should be empty after the move")

        val expectedState = listOf(
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, Player.W_Q, null, null,
            null, null, null, null, null, null, null, null,
            null, Player.W_Q, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, Player.B, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null
        )
        assertEquals(expectedState, result.cells)
    }

    @Test
    fun `Test move with promotion`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, Player.W, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, Player.W_Q, null, null, Player.B, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        // Move a white piece from f7  to g8
        val from = Position(13)
        val to = Position(6)
        val result = sut.play(from, to) as BoardRun

        assertEquals(Player.W.toKing(), result.cells[to.index])

        val expectedState = listOf(
            null, null, null, null, null, null, Player.W.toKing(), null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, null, Player.W_Q, null, null, Player.B, null,
            null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null
        )

        assertEquals(expectedState, result.cells)
    }

    @Test
    fun `Test invalid move - not own piece`() {
        val sut = BoardRun(
            cells = listOf(
                Player.B, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null,
                null, Player.W, null, Player.W, null, Player.W, null, Player.W,
                Player.W, null, Player.W, null, Player.W, null, Player.W, null
            ),
            turn = Player.W
        )
        val from = Position(0, 0) // a1 (Black piece)
        val to = Position(1, 1)   // b2 (Empty)
        assertFalse(sut.isValidMove(from, to))
    }

    @Test
    fun `Test invalid move destination square not empty`() {
        val sut = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, Player.B, null, null, null, null, null,
                null, null, null, Player.W, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        val from = Position(3, 3) // d4 (White piece)
        val to = Position(4, 2)   // c5 (Occupied by Black piece)

        assertFalse(
            sut.isValidMove(from, to),
        )
    }
    @Test
    fun `Test pawn cannot move backward`() {
        val board = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Player.W, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        val from = Position(3, 4) // e4
        val invalidMove = Position(4, 3) // d5
        assertFalse(board.isValidMove(from, invalidMove))
    }
    @Test
    fun `Test pawn captures a queen`() {
        val board = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, Player.B_Q, null, null,
                null, null, null, null, Player.W, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, Player.B, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        val from = Position(3, 4) // e4 white
        val to = Position(1, 6)   // g6 afetr caputure

        // capture valid move
        assertTrue(board.isValidMove(from, to))

        val result = board.play(from, to) as BoardRun

        // Queen must be removed
        assertNull(result.cells[21])

        //chech white piece moved
        assertEquals(Player.W, result.cells[14])

        // TUrn changed
        assertEquals(Player.B, result.turn)
    }
    @Test
    fun `Test Invalid Move Mandatory Capture`() {
        val board = BoardRun(
            cells = listOf(
                null, Player.B_Q, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Player.B, null, null, null,
                null, Player.W, null, null, null, Player.W, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.B
        )

        val from = Position(28)
        val to = Position(33)
        val newBoard = board.play(from, to)

        assertEquals(board, newBoard)
    }
    @Test
    fun `Test Reverse Capture`() {
        val board = BoardRun(
            cells = listOf(
                null, Player.B_Q, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Player.B, null, null, null,
                null, Player.W, null, null, null, Player.W, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.B
        )

        val from = Position(28)
        val to = Position(46)

        val newBoard = board.play(from, to)

        assertTrue(newBoard is BoardRun)


        assertNull(newBoard.cells[28])

        assertEquals(Player.W, newBoard.turn)
    }
    @Test
    fun `Test Queen Double Capture`() {
        val board = BoardRun(
            cells = listOf(
                null, Player.W_Q, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, Player.B, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, Player.B, null, null, null, Player.B, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        val from = Position(2, 1) // c3
        val to = Position(4, 4)   // e5, capture
        val result = board.play(from, to) as BoardRun
        assertEquals(Player.W, result.turn) // Should stay the same for further captures

        // Now we simulate another capture
        val fromSecondCapture = Position(4, 4) // e5
        val toSecondCapture = Position(7, 1) // g3 (Capture another piece)
        val finalResult = result.play(fromSecondCapture, toSecondCapture) as BoardRun
        assertEquals(Player.W, finalResult.turn) // Should still be W

    }
    @Test
    fun `Test queen capture`() {
        val board = BoardRun(
            cells = listOf(
                null, null, null, null, null, null, null, null,
                null, null, null, null, Player.W_Q, null, null, null,
                null, null, null, null, null, Player.B, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, Player.W, null, Player.B,
                null, null, null, null, null, null, null, null
            ),
            turn = Player.W
        )

        val from = Position(12) // e7
        val to = Position(30)   // f6 (capture)
        val result = board.play(from, to) as BoardRun

        assertNull(result.cells[21]) // d6 (captured piece)
        assertEquals(Player.W_Q, result.cells[30])
        assertEquals(Player.B, result.turn)
    }
}


