package com.henzisoft.puttmaster9000.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.henzisoft.puttmaster9000.fragment.GameFragment
import androidx.wear.tooling.preview.devices.WearDevices
import com.henzisoft.puttmaster9000.presentation.components.NumpadDialog

@Composable
fun Scorecard(
    scorecard: GameFragment.Scorecard,
    selectedRound: Int,
    onSetScore: (score: Int) -> Unit,
    par: Int,
    holeBest: Int? = null
) {
    val state = rememberLazyListState()
    var pendingSelection by remember {
        mutableIntStateOf(0)
    }
    var showNumpad by remember { mutableStateOf(false) }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val density = LocalDensity.current.density
    val screenWidthPx = (screenWidthDp * density).toInt()
    val buttonWidth = 53.dp
    val buttonWidthPx = (buttonWidth.value * density).toInt()

    LaunchedEffect(scorecard) {
        pendingSelection = 0
    }

    LaunchedEffect(selectedRound) {
        val score = scorecard.scores?.getOrNull(selectedRound)
        val index =(score ?: par) - 1

        // Scroll backwards so that the index element is centered.
        // screenWidth / 2 - buttonWidth / 2 - paddings & gaps
        val offset = screenWidthPx / 2 - buttonWidthPx / 2 - 16.dp.value.toInt()

        state.scrollToItem(index, -offset)
    }

    if (showNumpad) {
        NumpadDialog(
            showDialog = showNumpad,
            onDismiss = { showNumpad = false },
            onConfirm = { score ->
                pendingSelection = score
                onSetScore(score)
                showNumpad = false
            }
        )
    }

    Column {
        val playerName = scorecard.user?.name ?: "N/A"
        val playerScore = scorecard.plusminus ?: 0
        Row {
            Text(
                text = playerName,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
            Text(
                text = when {
                    playerScore > 0 -> "+$playerScore"
                    playerScore < 0 -> playerScore.toString() // already has "-" sign
                    else -> "E" // "Even" — standard golf notation for par
                },
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = state,
        ) {
            items((1..10).toList()) {
                val selectedScore = scorecard.scores?.getOrNull(selectedRound) ?: 0
                val backgroundColor =
                    if (selectedScore == it)
                        Color(1, 110, 39)
                    else if (pendingSelection == it)
                        Color(1, 50, 10)
                    else Color.DarkGray
                Button(
                    onClick = {
                        pendingSelection = it
                        onSetScore(it)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
                    modifier = Modifier.width(buttonWidth)
                    ) {
                    Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        Text(
                            fontSize = 25.sp,
                            text = it.toString(),
                            modifier = Modifier.align(Alignment.Center)
                        )
                        if (holeBest != null && holeBest == it) {
                            Text(
                                text = "PB",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
            item {
                val selectedScore = scorecard.scores?.getOrNull(selectedRound) ?: 0
                val backgroundColor =
                    if (selectedScore > 10)
                        Color(1, 110, 39)
                    else if (pendingSelection > 10)
                        Color(1, 50, 10)
                    else Color.DarkGray
                Button(
                    onClick = {
                        showNumpad = true
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
                    modifier = Modifier.width(buttonWidth)
                ) {
                    Text(
                        fontSize = 25.sp,
                        text = if (selectedScore > 10) selectedScore.toString() else "+"
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
            scores = listOf(2, 2, 3, 3, 4, 4),
            plusminus = 5
        ),
        selectedRound = 1,
        onSetScore = { },
        par = 3,
        holeBest = 3
    )
}
