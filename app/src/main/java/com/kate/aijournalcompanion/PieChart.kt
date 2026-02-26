package com.kate.aijournalcompanion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EmotionPieChart(entries: List<JournalEntry>) {
    if (entries.isEmpty()) return

    val emotionCounts = entries.groupingBy { it.emotion.trim().uppercase() }
        .eachCount()

    val total = emotionCounts.values.sum().toFloat()
    if (total <= 0f) return

    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFF44336),
        Color(0xFF2196F3),
        Color(0xFFFFC107),
        Color(0xFF9C27B0),
        Color(0xFFFF5722),
        Color(0xFF00BCD4),
        Color(0xFF795548)
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        var startAngle = 0f
        var colorIndex = 0

        emotionCounts.forEach { (_, count) ->
            val sweepAngle = (count / total) * 360f

            // ✅ drawArc is available inside Canvas draw scope
            drawArc(
                color = colors[colorIndex % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            startAngle += sweepAngle
            colorIndex++
        }
    }
}