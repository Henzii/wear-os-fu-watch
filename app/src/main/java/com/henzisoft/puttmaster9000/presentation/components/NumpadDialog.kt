package com.henzisoft.puttmaster9000.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.dialog.Dialog

@Composable
fun NumpadDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var input by remember { mutableStateOf("") }
    val isRound = LocalConfiguration.current.isScreenRound

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isRound) 16.dp else 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = input.ifEmpty { "Score" },
                style = MaterialTheme.typography.title2,
                color = if (input.isEmpty()) Color.Gray else Color.White,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            val buttons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "<-")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = if (isRound) 8.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                    ) {
                        row.forEach { char ->
                            Button(
                                onClick = {
                                    when (char) {
                                        "C" -> input = ""
                                        "<-" -> if (input.isNotEmpty()) input = input.dropLast(1)
                                        else -> if (input.length < 3) input += char
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f),
                                colors = if (char == "C" || char == "<-")
                                    ButtonDefaults.secondaryButtonColors()
                                else
                                    ButtonDefaults.primaryButtonColors()
                            ) {
                                Text(text = char, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val score = input.toIntOrNull()
                    if (score != null && score != 0) {
                        onConfirm(score)
                    }
                },
                enabled = input.isNotEmpty(),
                colors = ButtonDefaults.secondaryButtonColors(),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(0.7f)
                    .height(34.dp)
            ) {
                Text("OK", fontSize = 16.sp, style = MaterialTheme.typography.button)
            }
        }
    }
}
