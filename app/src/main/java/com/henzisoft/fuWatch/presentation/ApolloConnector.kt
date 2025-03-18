package com.henzisoft.fuWatch.presentation

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.henzisoft.fuWatch.GetMeQuery
import com.henzisoft.fuWatch.GetOpenGamesQuery
import com.henzisoft.fuWatch.LoginMutation
import com.henzisoft.fuWatch.SetScoreMutation
import com.henzisoft.fuWatch.fragment.GameFragment
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

const val SERVER_URL = "http://10.0.2.2:4000"

class ApolloConnector(private val context: Context) {
    private val token = TokenManager.getToken(context)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(SERVER_URL)
        .addHttpHeader(
            name = "Authorization",
            value = "bearer $token"
        )
        .okHttpClient(okHttpClient)
        .build()

    suspend fun getMe(): String? {
        val response = apolloClient.query(GetMeQuery()).execute()
        return response.data?.getMe?.name
    }

    suspend fun login(username: String, password: String): String? {
        val response = apolloClient.mutation(LoginMutation(username, password)).execute()
        return response.data?.login
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