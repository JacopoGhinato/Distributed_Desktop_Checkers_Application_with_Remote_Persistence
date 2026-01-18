//GridView.kt
package org.example.isel.tds.checkers.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import isel.tds.checkers.isel.tds.checkers.model.BOARD_SIZE
import isel.tds.checkers.isel.tds.checkers.model.BoardRun
import isel.tds.checkers.isel.tds.checkers.model.Player
import isel.tds.checkers.isel.tds.checkers.model.Position

val CELL_SIZE = 60.dp
val LINE_WIDTH = 3.dp
val GRID_WIDTH = CELL_SIZE * BOARD_SIZE + LINE_WIDTH * (BOARD_SIZE -1)

@Composable
fun GridView(
    moves: List<Player?>?, // The list of players' pieces on the board
    onClickCell: (Position) -> Unit, // Function that is called on cell click
    selectedPosition: Position? = null, // Position of the selected piece
            highlightedCells: List<Position> = emptyList() // Add this parameter
) {
    Column(
        modifier = Modifier
            .size(GRID_WIDTH)
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(BOARD_SIZE) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(BOARD_SIZE) { col ->
                    val pos = Position(row * BOARD_SIZE + col) // Get the current position
                    val isSelected = pos == selectedPosition // Check if this position is selected
                    val isHighlighted = pos in highlightedCells // Check if this position is highlighted
                    val isDarkSquare = (row + col) % 2 != 0
                    val squareColor = when {
                        isSelected -> Color.Yellow // Highlight if selected
                        isHighlighted -> Color.Cyan // Highlight possible moves
                        isDarkSquare -> Color.DarkGray
                        else -> Color.LightGray
                    }
                    // Get the player piece at this position
                    val playerAtPosition = moves?.get(pos.index)
                    // Check if there is a piece at this position
                    PlayerView(
                        size = CELL_SIZE,
                        player = playerAtPosition,
                        onClick = {
                            onClickCell(pos)
                        },
                        modifier = Modifier
                            .size(CELL_SIZE)
                            .background(
                                if (isSelected) Color.Yellow else squareColor // Highlight if selected
                            )
                            .clickable(onClick = {
                                onClickCell(pos)
                            })
                    )
                }
            }
        }
    }
}