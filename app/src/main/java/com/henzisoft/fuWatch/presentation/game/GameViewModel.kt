package com.henzisoft.fuWatch.presentation.game

import android.app.Application
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.exception.ApolloException
import com.henzisoft.fuWatch.GameUpdateSubscription
import com.henzisoft.fuWatch.fragment.GameFragment
import com.henzisoft.fuWatch.presentation.ApolloConnector
import kotlinx.coroutines.launch

enum class GameState {
    INIT, NO_OPEN_GAMES, ERROR, READY, LOADING
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private var lastCallTime = 0L
    val selectedRound = mutableIntStateOf(0)
    val gameData = mutableStateOf<GameFragment?>(null)
    val gameState = mutableStateOf(GameState.INIT)

    lateinit var subscription: GameUpdateSubscription

    private val apolloConnector = ApolloConnector(application.applicationContext)

    private suspend fun subscribeToGame(gameId: String) {
        subscription = GameUpdateSubscription(gameId)
        apolloConnector.subscriptionClient.subscription(subscription).toFlow()
            .collect { response ->
                if (response.data?.gameUpdated?.game?.gameFragment != null) {
                    gameData.value = response.data?.gameUpdated?.game?.gameFragment
                }
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

    init {
        viewModelScope.launch {
            gameState.value = GameState.LOADING
            try {
                val gamesData = apolloConnector.fetchOpenGames()
                if (gamesData.data?.getGames?.games?.isNotEmpty() == true) {
                    gameData.value = gamesData.data?.getGames?.games?.get(0)?.gameFragment
                    gameState.value = GameState.READY
                    subscribeToGame(gameData.value!!.id)
                } else {
                    gameState.value = GameState.NO_OPEN_GAMES
                }
            } catch (e: ApolloException) {
                println("Errori: " + e.cause)
                gameState.value = GameState.ERROR
            }
        }
    }

    fun setScore(gameId: String, playerId: String, hole: Int, value: Int) {
        viewModelScope.launch {
            try {
                apolloConnector.setScore(gameId, playerId, hole, value)
            } catch (e: ApolloException) {
                println("Errori: " + e.cause)
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