//MainGridView.kt
//NON MODIFICARE
package org.example.isel.tds.checkers

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import isel.tds.checkers.isel.tds.checkers.model.*
import org.example.isel.tds.checkers.ui.GridView
import org.example.isel.tds.checkers.ui.StatusBar

@Composable
@Preview
private fun GridApp() {
    var board: Board by remember { mutableStateOf(BoardRun(turn = Player.W)) }
    var selectedFrom: Position? by remember { mutableStateOf(null) }
    var message: String? by remember { mutableStateOf(null) }

    MaterialTheme {
        Column {
            GridView(board.cells,
                onClickCell = { clickedPosition: Position ->
                    val pieceAtPosition = board.cells[clickedPosition.index]

                    when (selectedFrom) {
                        null -> {
                            // First clic select the piece
                            if (pieceAtPosition != null && pieceAtPosition == board.turn) { //checking if the piece clicked is valid
                                selectedFrom = clickedPosition
                                message = null
                            } else {
                                selectedFrom = null
                            }
                        }
                        else -> {
                            // Second clic: make the move
                            try {
                                board = board.play(from = selectedFrom!!, to = clickedPosition)
                                selectedFrom = null
                                message = null
                            } catch (ex: Exception) {
                                selectedFrom = null
                            }
                        }
                    }
                }
            )
            StatusBar(board, message)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication,
        state = WindowState(size = DpSize.Unspecified)
    ) {
        GridApp()
    }
}
