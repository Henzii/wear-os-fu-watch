/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.henzisoft.puttmaster9000.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.henzisoft.puttmaster9000.presentation.game.Game
import com.henzisoft.puttmaster9000.presentation.theme.FuWatchTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted or not — nothing else to do here */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        GameNotificationManager.createNotificationChannel(this)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&  // Permission is not needed on older versions
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            WearApp()
        }
    }

    override fun onStart() {
        super.onStart()
        GameNotificationManager.show(this)
    }

    override fun onStop() {  // Stop the ongoing activity if the user leaves the app.
        super.onStop()
        GameNotificationManager.dismiss(this)
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