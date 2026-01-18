//PlayerView.kt ALRDEY OK
package org.example.isel.tds.checkers.ui


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import isel.tds.checkers.isel.tds.checkers.model.Player

@Composable
fun PlayerView (
    size: Dp= 100.dp,
    player: Player?,
    onClick: ()->Unit = {},
    modifier: Modifier = Modifier.size(size)
){
    if(player == null){
        Box( modifier.clickable(onClick = {
            //println("Empty cell clicked") //DEBUG
            onClick()})
        )
    }else{
        val filename = when(player){
            Player.W -> "piece_w"
            Player.B -> "piece_b"
            Player.W_Q -> "piece_wk"
            Player.B_Q -> "piece_bk"
            Player.NONE -> TODO()
        }
        Image(painter = painterResource("$filename.png"),
            contentDescription = "Player $player $filename",
            modifier = modifier.clickable(onClick = {
                onClick()
            })
        )
    }
}