//MainMongoDBApp.kt
package org.example.isel.tds.checkers

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.*
import isel.tds.checkers.isel.tds.checkers.model.Position
import org.example.isel.tds.checkers.storage.MongoDriver
import org.example.isel.tds.checkers.ui.*
import org.example.isel.tds.checkers.viewmodel.AppViewModel


@Composable
@Preview
private fun FrameWindowScope.CheckersApp(driver: MongoDriver, onExit: ()->Unit) {
    var selectedPosition by remember { mutableStateOf<Position?>(null) }
    var vm: AppViewModel = remember { AppViewModel(driver) }
    StateHolder.vm=vm
    var showTargets by remember { mutableStateOf(true) }
    var possibleMoves by remember { mutableStateOf<List<Position>>(emptyList()) }

    MaterialTheme {
        MenuBar {
            Menu("Game") {
                Item("Start/Join Game", onClick = vm::openStartDialog)
                Item("Refresh", onClick = vm::refresh)
                Item("Exit", onClick = {
                    vm.onExit()
                    onExit()
                })
            }
            Menu("Options") {
                Item(
                    text = "Auto-refresh",
                    onClick = {
                        vm.autoRefreshEnabled = !vm.autoRefreshEnabled // Toggle the auto-refresh
                        if (vm.autoRefreshEnabled) {
                            vm.waitForOtherSide() // Start autorefresh
                        } else {
                            vm.waitingJob?.cancel() // Stop autorefresh
                            vm.waitingJob = null // reset waitingJob to null
                        }
                    },
                    icon = if (vm.autoRefreshEnabled) {
                        rememberVectorPainter(Icons.Default.Check)
                    } else {
                        null
                    }
                )
                Item(
                    text = "Show Targets",
                    onClick = { showTargets = !showTargets },
                    icon = if (showTargets) {
                        rememberVectorPainter(Icons.Default.Check)
                    } else {
                        null
                    }
                )
            }
        }
        Column() {
            GridView(
                vm.board?.cells,
                onClickCell = { pos: Position ->
                    try {
                        if (selectedPosition == null) {
                            selectedPosition = pos
                            possibleMoves = vm.board?.getPossibleMoves(pos) ?: emptyList()
                        } else {
                            vm.play(selectedPosition!!, pos)
                            selectedPosition = null
                            possibleMoves = emptyList()
                        }
                    }
                    catch (ex: IllegalStateException) {
                            println("Error: ${ex.message}") }
                    catch (ex: Exception) {
                        println("Error: ${ex.message}, Turn: ${vm.board?.turn}")
                        selectedPosition = null }
                },
                selectedPosition = selectedPosition, // Highlight cell
                highlightedCells = if (showTargets) possibleMoves else emptyList() // Show or hide targets
            )
            StatusBar(vm.board, vm.currentName ,processTurn = vm.processTurn)
        }
        vm.inputName?.let {
            StartOrJoinDialog(
                type = it,
                onCancel = vm::closeStartOrJoinDialog,
                onAction = vm::startOrJoin
            ) }
        vm.errorMessage?.let { ErrorDialog(it, onClose = vm::hideError) }
        if (vm.isWaiting) waitingIndicator()
    }
}

object StateHolder{ //Store here the information of the vm so when I clic on the X to exit it can delete
    // the game from the MONGODB database
    var vm: AppViewModel? = null
}

fun main() = MongoDriver("checkers").use { driver ->
    application {
        Window(
            onCloseRequest = {
                StateHolder.vm?.onExit()
                exitApplication()
            },
            state = WindowState(size = DpSize.Unspecified),
            title = "Checkers"
        ) {
            CheckersApp(driver, ::exitApplication)
        }
    }
}
