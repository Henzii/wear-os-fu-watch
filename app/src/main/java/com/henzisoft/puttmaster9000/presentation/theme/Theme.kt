package com.henzisoft.puttmaster9000.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.ButtonColors
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

val PrimaryColor = Color(20, 30, 62)
val SecondaryColor = Color.DarkGray

private val FuWatchColorPalette = Colors(
    primary = PrimaryColor,
    onPrimary = Color.White,
    secondary = SecondaryColor,
    onSecondary = Color.White,
)

@Composable
fun FuWatchTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = FuWatchColorPalette,
        content = content,
    )
}

@Composable
fun primaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    backgroundColor = MaterialTheme.colors.primary,
    contentColor = MaterialTheme.colors.onPrimary,
    disabledBackgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.35f),
    disabledContentColor = Color.White.copy(alpha = 0.5f)
)

@Composable
fun secondaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    backgroundColor = MaterialTheme.colors.secondary,
    contentColor = MaterialTheme.colors.onSecondary,
    disabledBackgroundColor = MaterialTheme.colors.secondary.copy(alpha = 0.35f),
    disabledContentColor = MaterialTheme.colors.onSecondary.copy(alpha = 0.5f)
)
