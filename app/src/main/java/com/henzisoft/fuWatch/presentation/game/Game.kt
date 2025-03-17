package com.henzisoft.fuWatch.presentation.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text

@Composable
fun Game() {
   val gameViewModel = viewModel<GameViewModel>()

    if (gameViewModel.gameState.value == GameState.NO_OPEN_GAMES) {
        InfoScreen("No open games")
    } else if (gameViewModel.gameState.value == GameState.ERROR) {
        InfoScreen("Error just happened :(")
    } else {
        GameView()
    }
}

@Composable
fun InfoScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text)
    }
}

