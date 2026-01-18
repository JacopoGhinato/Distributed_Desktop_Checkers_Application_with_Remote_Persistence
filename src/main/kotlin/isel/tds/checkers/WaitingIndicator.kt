//waitingIndicator.kt
package org.example.isel.tds.checkers

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun waitingIndicator() = CircularProgressIndicator(
    Modifier.fillMaxSize().padding(20.dp),
    strokeWidth = 30.dp
)