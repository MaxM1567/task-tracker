package com.example.task_tracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.example.task_tracker.data.Task

@Composable
fun TaskCard(task: Task) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isDone)
                MaterialTheme.colorScheme.surfaceContainerHighest.darken(0.3f)
            else
                MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 4.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
//            .border(
//                width = if (task.isDone) 4.dp else 0.dp,
//                color = if (task.isDone) Color.Green.copy(alpha = 0.5f) else Color.Transparent,
//                shape = RoundedCornerShape(18.dp)
//            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                fontSize = 18.sp,
                color = if (task.isDone)
                    Color.DarkGray
                else
                    Color.Unspecified,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else null
            )
        }
    }
}

fun Color.darken(factor: Float = 0.2f): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[2] = hsl[2] * (1f - factor)
    return Color(ColorUtils.HSLToColor(hsl))
}