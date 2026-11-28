package com.henzisoft.puttmaster9000.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
    onConfirm: () -> Unit,
    incompleteHoles: List<Int>
) {
    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss
    ) {
        Alert(
            title = {
                Text(
                    text = "Close game?",
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
                    text = if (incompleteHoles.isEmpty()) {"All holes completed!"} else {"Some players are missing scores for holes: ${incompleteHoles.joinToString(", ")}"},
                    textAlign = TextAlign.Center,
                    color = if (incompleteHoles.isEmpty()) {Color.Unspecified} else {Color.Red}
                )
            }
        )
    }
}
