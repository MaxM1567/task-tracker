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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task_tracker.data.room.task.Task
import kotlin.math.roundToInt

private const val SWIPE_LEFT = -1
private const val SWIPE_IDLE = 0
private const val SWIPE_RIGHT = 1

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimerTaskCard(
    task: Task,
    isTimerRunning: Boolean,
    time: Long,
    onUpdate: (Task) -> Unit,
    onDelete: () -> Unit
) {
    val swipeState = rememberSwipeableState(initialValue = SWIPE_IDLE)

    val density = LocalDensity.current
    val screenWidthPx = with(density) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

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
            modifier = Modifier
                .fillMaxSize()
                .height(85.dp)
                .padding(vertical = 4.dp, horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.weight(1f), text = task.title, fontSize = 18.sp)

                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = time.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isTimerRunning) Color.Green else Color.White
                )
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

fun formatTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = seconds % 3600 / 60
    val seconds = seconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

private fun Task.decrease(): Task =
    if (curRepetitions > 0) copy(curRepetitions = curRepetitions - 1)
    else copy(curRepetitions = repetitions)