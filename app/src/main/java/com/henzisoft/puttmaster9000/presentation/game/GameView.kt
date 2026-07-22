package com.henzisoft.puttmaster9000.presentation.game

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedModifier
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.background
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.padding
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.curvedText
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.ambient.AmbientAware
import com.google.android.horologist.compose.ambient.AmbientState
import com.henzisoft.puttmaster9000.fragment.GameFragment
import com.henzisoft.puttmaster9000.presentation.theme.FuWatchTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GameView() {
    val viewModel = viewModel<GameViewModel>()
    val game = viewModel.gameData.value!!

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
                    modifier = Modifier.fillMaxSize(),
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
                        text = "Hole ${viewModel.selectedRound.intValue + 1}\n\nPar: ${game.pars?.getOrNull(viewModel.selectedRound.intValue) ?: 3}",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
        } else {
            GameContent(
                game = game,
                selectedRound = viewModel.selectedRound.intValue,
                onSetScore = { playerId, score ->
                    viewModel.setScore(game.id, playerId, viewModel.selectedRound.intValue, score)
                },
                onPreviousRound = { viewModel.previousRound() },
                onNextRound = { viewModel.nextRound() }
            )
        }
    }
}

@Composable
fun GameContent(
    game: GameFragment,
    selectedRound: Int,
    onSetScore: (playerId: String, score: Int) -> Unit,
    onPreviousRound: () -> Unit,
    onNextRound: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberScalingLazyListState()
    val par = game.pars?.getOrNull(selectedRound) ?: 3
    val holeNumber = selectedRound + 1

    fun scrollToIndex(
        index: Int,
        delayMillis: Long = 500
    ) {
        coroutineScope.launch {
            delay(delayMillis)
            listState.animateScrollToItem(index)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, _, _ ->
                    if (pan.x > 40 && selectedRound > 0) {
                        onPreviousRound()
                    } else if (pan.x < -40 && selectedRound < (game.pars?.size ?: 0) - 1) {
                        onNextRound()
                    }
                }
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            CurvedLayout(
                modifier = Modifier.fillMaxSize(),
            ) {
                curvedText(
                    text = "#$holeNumber  Par: $par",
                    style = CurvedTextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    modifier = CurvedModifier
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(before = 2.dp, after = 2.dp, inner = 0.dp, outer = 0.dp)
                )
            }
        }
        PositionIndicator(scalingLazyListState = listState)
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            autoCentering = AutoCenteringParams(itemIndex = 1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(0.dp),
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
                    selectedRound,
                    par = par,
                    onSetScore = { score ->
                        onSetScore(scorecard.user!!.id, score)
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
                            onPreviousRound()
                            scrollToIndex(1)
                        },
                        text = "<",
                        enabled = selectedRound > 0
                    )
                    PrevNextButton(
                        onClick = {
                            onNextRound()
                            scrollToIndex(1)
                        },
                        text = ">",
                        enabled = selectedRound < (game.pars?.size ?: 9) - 1
                    )
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
    val mockGame = GameFragment(
        id = "1",
        course = "Paloheinä",
        layout = "Evergreen",
        pars = listOf(3, 3, 3, 4, 3, 3, 3, 3, 3),
        isOpen = true,
        scorecards = listOf(
            GameFragment.Scorecard(
                user = GameFragment.User(id = "u1", name = "Adam"),
                scores = listOf(3, 2, 4),
                plusminus = 0
            ),
            GameFragment.Scorecard(
                user = GameFragment.User(id = "u2", name = "Barney"),
                scores = listOf(2, 2),
                plusminus = -2
            ),
            GameFragment.Scorecard(
                user = GameFragment.User(id = "u3", name = "Charlie"),
                scores = listOf(4, 4),
                plusminus = 2
        )
        )
    )

    FuWatchTheme {
        GameContent(
            game = mockGame,
            selectedRound = 0,
            onSetScore = { _, _ -> },
            onPreviousRound = {},
            onNextRound = {}
        )
    }
}
