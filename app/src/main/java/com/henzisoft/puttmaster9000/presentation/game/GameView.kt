package com.henzisoft.puttmaster9000.presentation.game

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameView() {
    val viewModel = viewModel<GameViewModel>()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()
    val game = viewModel.gameData.value!!
    val scrollState = rememberScrollState()
    val par = game.pars?.get(viewModel.selectedRound.intValue) ?: 3
    val holeNumber = viewModel.selectedRound.intValue + 1

    fun scrollToIndex(
        index: Int,
        delayMillis: Long = 500
    ) {
        coroutineScope.launch {
            delay(delayMillis)
            listState.animateScrollToItem(index)
        }
    }

    AmbientAware { ambientStateUpdate ->
        if (ambientStateUpdate.ambientState !== AmbientState.Interactive) {
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&  // Permission is not needed on older versions
                ContextCompat.checkSelfPermission(
                    LocalContext.current,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Box(
                    modifier  = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Notification permission is required for always-on mode",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hole $holeNumber\n\nPar: $par",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, _, _ ->
                            if (pan.x > 40 && viewModel.selectedRound.intValue > 0) {
                                viewModel.previousRound()
                            } else if (pan.x < -40 && viewModel.selectedRound.intValue < game.pars!!.size - 1) {
                                viewModel.nextRound()
                            }
                        }
                    },
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    text = "#$holeNumber  Par: $par",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .zIndex(1f)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(2.dp)
                )
                PositionIndicator(scalingLazyListState = listState)
                ScalingLazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    autoCentering = AutoCenteringParams(itemIndex = 1),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = game.course,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    itemsIndexed(game.scorecards) { scorecardIndex, scorecard ->
                        Scorecard(
                            scorecard,
                            viewModel.selectedRound.intValue,
                            par = par,
                            onSetScore = { score ->
                                viewModel.setScore(
                                    game.id,
                                    scorecard.user!!.id,
                                    viewModel.selectedRound.intValue,
                                    score
                                )
                                if (scorecardIndex < game.scorecards.size) {
                                    scrollToIndex(scorecardIndex + 2)
                                }
                            }
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PrevNextButton(
                                onClick = {
                                    viewModel.previousRound()
                                    scrollToIndex(1)
                                },
                                text = "<",
                                enabled = viewModel.selectedRound.intValue > 0
                            )
                            PrevNextButton(
                                onClick = {
                                    viewModel.nextRound()
                                    scrollToIndex(1)
                                },
                                text = ">",
                                enabled = viewModel.selectedRound.intValue < (game.pars?.size
                                    ?: 9) - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrevNextButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(20, 30, 62)),
        enabled = enabled
    ) {
        Text(text, fontSize = 30.sp)
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun GamePreview() {
    Game()
}
