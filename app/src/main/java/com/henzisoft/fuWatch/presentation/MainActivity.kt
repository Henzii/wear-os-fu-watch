/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.henzisoft.fuWatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.henzisoft.fuWatch.presentation.game.Game
import com.henzisoft.fuWatch.presentation.theme.FuWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val navController = rememberNavController()
    val token = TokenManager.getToken(LocalContext.current)
    val startDestination = if (token == null) "login" else "game"

    FuWatchTheme {
        NavHost(navController = navController, startDestination) {
            composable("game") {
                Game()
            }
            composable("login") {
                Login(navController)
            }
        }
    }
}