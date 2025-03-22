package com.henzisoft.fuWatch.presentation.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text

@Composable
fun Game() {
    val gameViewModel = viewModel<GameViewModel>()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            gameViewModel.refetchIfSubscriptionError()
        }
    }

    LaunchedEffect(Unit) {
        gameViewModel.fetchOpenGames(true)
    }
    if (gameViewModel.gameState.value == GameState.NO_OPEN_GAMES) {
        InfoScreen("No open games")
    } else if (gameViewModel.gameState.value == GameState.ERROR) {
        InfoScreen("Can't connect to FuDisc server :(")
    } else if (gameViewModel.gameState.value == GameState.READY && gameViewModel.gameData.value != null) {
        GameView()
    } else if (gameViewModel.gameState.value == GameState.LOADING) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                strokeWidth = 8.dp,
                indicatorColor = Color(20, 80, 30)
            )
        }
    }
}

@Composable
fun InfoScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}

