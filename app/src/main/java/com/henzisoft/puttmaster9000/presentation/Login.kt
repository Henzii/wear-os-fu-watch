package com.henzisoft.puttmaster9000.presentation

import android.app.Activity
import android.app.RemoteInput
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import androidx.wear.compose.material.dialog.Dialog
import androidx.wear.input.RemoteInputIntentHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Login(
    navController: NavController
) {
    val context = LocalContext.current
    val apolloConnector = ApolloConnector(context)
    var loginState by remember { mutableStateOf<QueryState>(QueryState.INIT) }
    var showDialog by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val usernameLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = RemoteInput.getResultsFromIntent(result.data)
            username = results?.getCharSequence("username")?.toString() ?: username
        }
    }

    val passwordLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = RemoteInput.getResultsFromIntent(result.data)
            password = results?.getCharSequence("password")?.toString() ?: password
        }
    }

    Dialog(showDialog = showDialog, onDismissRequest = { showDialog = false }) {
        Confirmation(onTimeout = { showDialog = false }, durationMillis = 5000L) {
            val text = when (loginState) {
                QueryState.ERROR -> "Internet error"
                QueryState.FAIL -> "Wrong username or password"
                else -> ""
            }
            Text(text, textAlign = TextAlign.Center)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login")
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InputChip(
                label = "Username",
                value = username,
                isPassword = false,
                onClick = {
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    val remoteInputs = listOf(
                        RemoteInput.Builder("username")
                            .setLabel("Username")
                            .build()
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    usernameLauncher.launch(intent)
                }
            )
            InputChip(
                label = "Password",
                value = password,
                isPassword = true,
                onClick = {
                    val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                    val remoteInputs = listOf(
                        RemoteInput.Builder("password")
                            .setLabel("Password")
                            .build()
                    )
                    RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)
                    passwordLauncher.launch(intent)
                }
            )
        }
        Button (onClick = {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val token = apolloConnector.login(username, password)
                    if (token == null) {
                        loginState = QueryState.FAIL
                        showDialog = true
                    } else {
                        TokenManager.saveToken(context, token)
                        navController.navigate("game") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                } catch (e: Exception) {
                    println(e.cause)
                    loginState = QueryState.ERROR
                    showDialog = true
                }
            }
        }, modifier = Modifier.width(120.dp)) {
            Text("Login")
        }

    }
}

@Composable
fun InputChip(
    label: String,
    value: String,
    isPassword: Boolean,
    onClick: () -> Unit
) {
    Chip(
        onClick = onClick,
        label = { Text(label) },
        secondaryLabel = {
            Text(
                text = if (isPassword) "•".repeat(value.length) else value,
                color = Color.LightGray
            )
        },
        colors = ChipDefaults.secondaryChipColors(),
        modifier = Modifier.width(140.dp)
    )
}
