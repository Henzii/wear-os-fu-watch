package com.henzisoft.puttmaster9000.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Confirmation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.wear.compose.material.dialog.Dialog

@Composable
fun Login(
    navController: NavController
) {
    val context = LocalContext.current
    val apolloConnector = ApolloConnector(context)
    var loginState by remember { mutableStateOf<QueryState>(QueryState.INIT) }
    var showDialog by remember { mutableStateOf(false) }
    var username by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
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
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InputField(
                value = username,
                onValueChange = {newValue -> username = newValue},
                isPassword = false
            )
            InputField(
                value = password,
                onValueChange = {newValue -> password = newValue},
                isPassword = true
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
                        navController.navigate("game")
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
fun InputField(
    value: String,
    onValueChange: (newValue: String) -> Unit,
    isPassword: Boolean
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.background(Color.DarkGray).height(25.dp).padding(top = 4.dp).padding(horizontal = 4.dp),
        textStyle = TextStyle(color = Color.White),
        singleLine = true,
        visualTransformation = if (isPassword == true) PasswordVisualTransformation() else VisualTransformation.None
    )
}
