package com.henzisoft.fuWatch.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.henzisoft.fuWatch.fragment.GameFragment
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun Scorecard(
    scorecard: GameFragment.Scorecard,
    selectedRound: Int,
    onSetScore: (score: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        val playerName = scorecard.user?.name ?: "N/A"
        Text(
            text = playerName,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items((1..10).toList()) {
                val selectedScore = scorecard.scores?.getOrNull(selectedRound) ?: 0
                val backgroundColor =
                    if (selectedScore == it)
                        Color(1, 110, 39)
                    else Color.DarkGray
                Button(
                    onClick = {
                        onSetScore(it)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),

                    ) {
                    Text(
                        fontSize = 25.sp,
                        text = it.toString()
                    )
                }
            }
        }
    }
}

@Preview (device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ScorecardPreview() {
    Scorecard(
        scorecard = GameFragment.Scorecard(
            user = GameFragment.User(name = "Testi", id="123"),
            scores = listOf(2, 2, 3, 3, 4, 4)
        ),
        selectedRound = 1,
        onSetScore = { }
    )
}