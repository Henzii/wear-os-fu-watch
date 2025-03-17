package com.henzisoft.fuWatch.presentation.game

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import kotlinx.coroutines.delay

@Composable
fun GameView() {
    val viewModel = viewModel<GameViewModel>()
    val listState = rememberScalingLazyListState()
    var scrollToIndex by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(scrollToIndex) {
        delay(1000)
        listState.animateScrollToItem(scrollToIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    if (pan.x > 40) {
                        viewModel.previousRound()
                    } else if (pan.x < -40) {
                        viewModel.nextRound()
                    }
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = "#${viewModel.selectedRound.intValue + 1}",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .zIndex(1f)
                .background(MaterialTheme.colors.background)
        )
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            autoCentering = AutoCenteringParams(itemIndex = 1),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val game = viewModel.gameData.value!!
            item {
                Text(text = game.course)
            }
            itemsIndexed(game.scorecards) { scorecardIndex, scorecard ->
                Scorecard(
                    scorecard,
                    viewModel.selectedRound.intValue,
                    onSetScore = { score ->
                        viewModel.setScore(game.id, scorecard.user!!.id, viewModel.selectedRound.intValue, score)
                        if (scorecardIndex < game.scorecards.size) {
                            scrollToIndex = scorecardIndex + 2 // First list item is track name
                        }
                    }
                )
            }
            item {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PrevNextButton(onClick = {viewModel.previousRound()}, text = "<")
                    PrevNextButton(onClick = {viewModel.nextRound()}, text = ">")
                }
            }
        }
    }
}

@Composable
fun PrevNextButton(text: String, onClick: () -> Unit) {
    Button(
        onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(10, 10, 50)),
    ) {
        Text(text, fontSize = 30.sp)
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun GamePreview() {
    Game()
}
