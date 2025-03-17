package com.henzisoft.fuWatch.presentation

import android.app.Application
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.henzisoft.fuWatch.GetMeQuery
import com.henzisoft.fuWatch.GetOpenGamesQuery
import com.henzisoft.fuWatch.SetScoreMutation
import com.henzisoft.fuWatch.fragment.GameFragment

const val SERVER_URL = "http://10.0.2.2:4000"

class ApolloConnector(private val application: Application) {
    private val token = TokenManager.getToken(application.applicationContext)
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(SERVER_URL)
        .addHttpHeader(
            name = "Authorization",
            value = "bearer $token"
        )
        .build()

    suspend fun getMe(): String? {
        val response = apolloClient.query(GetMeQuery()).execute()
        return response.data?.getMe?.name
    }

    suspend fun setScore(
        gameId: String,
        playerId: String,
        hole: Int,
        value: Int
    ): GameFragment? {
        val response = apolloClient.mutation(
            SetScoreMutation(
                gameId = gameId,
                playerId = playerId,
                hole = hole,
                value = value
            )
        ).execute()

        return response.data?.setScore?.gameFragment
    }

    suspend fun fetchOpenGames(): ApolloResponse<GetOpenGamesQuery.Data> {
        val response = apolloClient.query(GetOpenGamesQuery()).execute()
        return response
    }

}