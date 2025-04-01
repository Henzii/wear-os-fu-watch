package com.henzisoft.puttmaster9000.presentation.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.henzisoft.puttmaster9000.GameUpdateSubscription
import com.henzisoft.puttmaster9000.fragment.GameFragment
import com.henzisoft.puttmaster9000.presentation.ApolloConnector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class GameState {
    INIT, NO_OPEN_GAMES, ERROR, READY, LOADING
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private var lastCallTime = 0L
    val selectedRound = mutableIntStateOf(0)
    val gameData = mutableStateOf<GameFragment?>(null)
    val gameState = mutableStateOf(GameState.INIT)

    private var isSubscriptionActive by mutableStateOf<Boolean>(false)
    private lateinit var subscription: GameUpdateSubscription

    private val apolloConnector = ApolloConnector(application.applicationContext)

    fun refetchIfSubscriptionError() {
        if (gameData.value?.id != null && !isSubscriptionActive) {
            fetchOpenGames(false)
        }
    }

    private suspend fun subscribeToGame(gameId: String) {
        viewModelScope.launch {
            try {
                subscription = GameUpdateSubscription(gameId)
                apolloConnector.subscriptionClient.subscription(subscription).toFlow()
                    .collect { response ->
                        val responseGame = response.data?.gameUpdated?.game?.gameFragment
                        if (responseGame != null) {
                            gameData.value = responseGame

                            // If the game is closed
                            if (responseGame.isOpen == false) {
                                gameState.value = GameState.NO_OPEN_GAMES
                            }
                        }
                    }
                isSubscriptionActive = true
            } catch (e: ApolloException) {
                println("Subscription failed")
            }

            isSubscriptionActive = false
        }
    }

    private fun throttled(): Boolean {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastCallTime > 500) {
            lastCallTime = currentTime
            return true
        }

        return false
    }

    private fun refetchOpenGames() {
        viewModelScope.launch {
            delay(15_000)
            fetchOpenGames(false)
        }
    }

    fun fetchOpenGames(showLoadingState: Boolean = false) {
        viewModelScope.launch {
            if (showLoadingState) gameState.value = GameState.LOADING
            try {
                val gamesData = apolloConnector.fetchOpenGames()
                if (gamesData.data?.getGames?.games?.isNotEmpty() == true) {
                    gameData.value = gamesData.data?.getGames?.games?.get(0)?.gameFragment
                    gameState.value = GameState.READY
                    subscribeToGame(gameData.value!!.id)
                } else {
                    gameState.value = GameState.NO_OPEN_GAMES
                    refetchOpenGames()
                }
            } catch (e: ApolloException) {
                gameState.value = GameState.ERROR
            }
        }
    }

    fun setScore(gameId: String, playerId: String, hole: Int, value: Int) {
        viewModelScope.launch {
            try {
                apolloConnector.setScore(gameId, playerId, hole, value)
            } catch (e: ApolloException) {
                println("Failed to set score")
            }

        }
    }

    fun nextRound() {
        if (throttled()) {
            selectedRound.intValue += 1
        }
    }

    fun previousRound() {
        if (throttled() && selectedRound.intValue > 0) {
            selectedRound.intValue -= 1
        }

    }
}