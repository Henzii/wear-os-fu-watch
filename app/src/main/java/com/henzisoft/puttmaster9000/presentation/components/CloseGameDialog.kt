package com.henzisoft.puttmaster9000.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Alert
import androidx.wear.compose.material.dialog.Dialog
import com.henzisoft.puttmaster9000.presentation.theme.primaryButtonColors
import com.henzisoft.puttmaster9000.presentation.theme.secondaryButtonColors

@Composable
fun CloseGameDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss
    ) {
        Alert(
            title = {
                Text(
                    text = "Are you sure?",
                    textAlign = TextAlign.Center
                )
            },
            negativeButton = {
                Button(
                    onClick = onDismiss,
                    colors = secondaryButtonColors()
                ) {
                    Text("Cancel", fontSize = 12.sp)
                }
            },
            positiveButton = {
                Button(
                    onClick = {
                        onDismiss()
                        onConfirm()
                    },
                    colors = primaryButtonColors()
                ) {
                    Text("Do it!", fontSize = 12.sp)
                }
            },
            content = {
                Text(
                    text = "After closing the game you will no longer be able to enter scores.",
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}
