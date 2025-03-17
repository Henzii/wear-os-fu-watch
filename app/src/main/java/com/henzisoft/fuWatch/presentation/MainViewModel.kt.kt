package com.henzisoft.fuWatch.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {
    val apolloConnector = ApolloConnector(application)
    var name by mutableStateOf("")
    init {
        viewModelScope.launch {
            val nameResponse = apolloConnector.getMe()
            if (nameResponse != null) {
                name = nameResponse
            }
        }
    }
}