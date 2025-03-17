package com.henzisoft.fuWatch.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun Login() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login")
        Column (
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
        }
        Button (onClick = {}, modifier = Modifier.width(120.dp)) {
            Text("Login")
        }

    }
}

@Preview(device = WearDevices.SMALL_ROUND)
@Composable
fun LoginPreview() {
    Login()
}