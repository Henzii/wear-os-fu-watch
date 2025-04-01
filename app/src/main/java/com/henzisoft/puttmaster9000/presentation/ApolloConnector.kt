package com.henzisoft.puttmaster9000.presentation

import android.content.Context
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.network.okHttpClient
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import com.henzisoft.puttmaster9000.GetOpenGamesQuery
import com.henzisoft.puttmaster9000.LoginMutation
import com.henzisoft.puttmaster9000.SetScoreMutation
import com.henzisoft.puttmaster9000.fragment.GameFragment
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

const val SERVER_URL = "https://fudisc-server.henzi.fi"

class ApolloConnector(context: Context) {
    private val token = TokenManager.getToken(context)
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl(SERVER_URL)
        .okHttpClient(okHttpClient)
        .addHttpHeader(
            name = "Authorization",
            value = "bearer $token"
        )
        .build()

    val subscriptionClient = ApolloClient.Builder()
        .serverUrl(SERVER_URL)
        .addHttpHeader(
            name = "Authorization",
            value = "bearer $token"
        )
        .subscriptionNetworkTransport(
            WebSocketNetworkTransport.Builder()
                .protocol(GraphQLWsProtocol.Factory())
                .serverUrl(SERVER_URL)
                .build()
        ).build()

/*    suspend fun getMe(): String? {
        val response = apolloClient.query(GetMeQuery()).execute()
        return response.data?.getMe?.name
    }
 */

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