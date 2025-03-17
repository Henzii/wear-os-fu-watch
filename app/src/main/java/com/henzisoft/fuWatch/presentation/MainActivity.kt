/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.henzisoft.fuWatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.henzisoft.fuWatch.presentation.game.Game
import com.henzisoft.fuWatch.presentation.theme.FuWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val token = TokenManager.getToken(this)

        setContent {
            WearApp(token)
        }
    }
}

@Composable
fun WearApp(token: String?) {
    val navController = rememberNavController()
    val startDestination = if (token == null) "login" else "main"
    FuWatchTheme {
        NavHost(navController = navController, startDestination) {
            composable("main") {
                Main(navController)
            }
            composable("game") {
                Game()
            }
            composable("login") {
                Login()
            }
        }
    }
}

@Composable
fun Main(
    navController: NavController
) {
    val mainViewModel = viewModel<MainViewModel>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(text = "Hello, ${mainViewModel.name}!")
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {}, modifier = Modifier.weight(0.5f)) {
                Text(text = "Login")
            }
            Button(
                onClick = {
                    navController.navigate("game")
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Game")
            }
        }
    }
}
