//statusbar.kt:
package org.example.isel.tds.checkers.ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import isel.tds.checkers.isel.tds.checkers.model.*

@Composable
fun StatusBar(board: Board?,gameName: String? = "No Game",  message: String? = null, processTurn: Player? = null) {
    Row(
        modifier = Modifier
            .width(GRID_WIDTH)
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Left: Game Name
        Text(
            text = "GAME: ${gameName ?: "No Game"}         ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        // Center: Board Turn
        val winner = board?.getWinner()
        val (text, player) = when {
            board == null -> "Match Not Started" to null
            winner != null -> "Winner" to winner
            board is BoardDraw -> "Draw" to null
            board is BoardRun -> "TURN GAME:" to board.turn
            else -> "Game Running" to null
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)

            player?.let {
                PlayerView(size = 32.dp, player = it) // Display player image for board turn
            }
        }

        // Right: Process Turn
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "    PROCESS TURN",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(end = 4.dp)
            )
            processTurn?.let { //display the piece near Turn
                PlayerView(size = 32.dp, player = it) // Display player image for process turn
            }
        }
    }
    }
