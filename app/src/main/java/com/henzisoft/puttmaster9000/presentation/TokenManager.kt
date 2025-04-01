package com.henzisoft.puttmaster9000.presentation

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

val Context.dataStore by preferencesDataStore(name = "fuDisc_token")

object TokenManager {
    private val TOKEN_KEY = stringPreferencesKey("token")

    suspend fun saveToken (context: Context, token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    fun getToken(context: Context): String? {
        return runBlocking {
            context.dataStore.data.first()[TOKEN_KEY]
        }

    }
}
