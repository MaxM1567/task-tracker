package com.example.task_tracker.ui.screens.home.taskcard

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.example.task_tracker.data.room.task.Task
import kotlin.math.roundToInt

/*
private const val SWIPE_LEFT = -1
private const val SWIPE_IDLE = 0
private const val SWIPE_RIGHT = 1

private val LEFT_ANCHOR = -1100f
private val RIGHT_ANCHOR = 350f

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskCard(
    task: Task,
    onUpdate: (Task) -> Unit,
    onDelete: () -> Unit
) {
    val swipeAbleState = rememberSwipeableState(initialValue = SWIPE_IDLE)
    val anchors = mapOf(SWIPE_IDLE to SWIPE_IDLE, LEFT_ANCHOR to SWIPE_LEFT, RIGHT_ANCHOR to SWIPE_RIGHT)
    val bgColor by animateColorAsState(
        if (swipeAbleState.offset.value < -50) Color.Red else Color.Transparent,
        label = "bgColorAnimation"
    )

    val textColor = if (task.curRepetitions == 0) Color.DarkGray else Color.Unspecified

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .swipeable(
                state = swipeAbleState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
            .offset { IntOffset(swipeAbleState.offset.value.roundToInt(), 0) }
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (task.curRepetitions == 0)
                    MaterialTheme.colorScheme.surfaceContainerHighest.darken(0.3f)
                else
                    MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            modifier = Modifier
                .fillMaxSize()
                .height(85.dp)
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    color = textColor,
                )


                val postscript = when (if (task.curRepetitions == 0) task.repetitions else task.curRepetitions) {
                    in 2..4 -> "раза"
                    else -> "раз"
                }

                Text(
                    text = "${if (task.curRepetitions == 0) task.repetitions else task.curRepetitions} $postscript",
                    modifier = Modifier.padding(end = 8.dp),
                    color = textColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.curRepetitions == 0) TextDecoration.LineThrough else null
                )
            }
        }
    }

    LaunchedEffect(swipeAbleState.currentValue) {
        when (swipeAbleState.currentValue) {
            1 -> {
                if (task.curRepetitions - 1 >= 0) {
                    onUpdate(task.copy(curRepetitions = task.curRepetitions - 1))
                } else {
                    onUpdate(task.copy(curRepetitions = task.repetitions))
                }
                swipeAbleState.animateTo(0)
            }

            -1 -> {
                onDelete()
            }
        }
    }
}


fun Color.darken(factor: Float = 0.2f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = hsl[2] * (1f - factor)
    return Color(ColorUtils.HSLToColor(hsl))
}*/

private const val SWIPE_LEFT = -1
private const val SWIPE_IDLE = 0
private const val SWIPE_RIGHT = 1

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TaskCard(
    task: Task,
    onUpdate: (Task) -> Unit,
    onDelete: () -> Unit
) {
    val swipeState = rememberSwipeableState(initialValue = SWIPE_IDLE)

    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    val anchors = remember {
        mapOf(
            0f to SWIPE_IDLE,
            -screenWidthPx * 1f to SWIPE_LEFT,
            screenWidthPx * 0.40f to SWIPE_RIGHT
        )
    }

    val bgColor by animateColorAsState(
        targetValue = if (swipeState.offset.value < -50) Color.Red else Color.Transparent,
        label = "bgColor"
    )

    val isFinished = task.curRepetitions == 0
    val textColor = if (isFinished) Color.DarkGray else Color.Unspecified
    val displayReps = if (isFinished) task.repetitions else task.curRepetitions

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
            .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) }
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    if (isFinished)
                        MaterialTheme.colorScheme.surfaceContainerHighest.darken(0.3f)
                    else
                        MaterialTheme.colorScheme.surfaceContainerHighest
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    color = textColor
                )

                if (task.repetitions > 1) {
                    Text(
                        text = "$displayReps ${repetitionsPostfix(displayReps)}",
                        color = textColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    LaunchedEffect(swipeState.currentValue) {
        when (swipeState.currentValue) {
            SWIPE_RIGHT -> {
                onUpdate(task.decrease())
                swipeState.animateTo(SWIPE_IDLE)
            }

            SWIPE_LEFT -> onDelete()
        }
    }
}

private fun repetitionsPostfix(value: Int): String =
    if (value % 10 == 2 || value % 10 == 3 || value % 10 == 4) "раза" else "раз"

private fun Task.decrease(): Task =
    if (curRepetitions > 0) copy(curRepetitions = curRepetitions - 1)
    else copy(curRepetitions = repetitions)

fun Color.darken(factor: Float = 0.2f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = hsl[2] * (1f - factor)
    return Color(ColorUtils.HSLToColor(hsl))
}