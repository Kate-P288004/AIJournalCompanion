package com.kate.aijournalcompanion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Purpose:
 * - Visualise emotion distribution
 * - Convert journal history into percentages
 * - Display results as a pie chart (donut style)
 *
 * Assessment Concepts Demonstrated:
 * - Data aggregation using Map
 * - Algorithmic percentage calculation
 * - Canvas drawing API
 * - Dynamic UI rendering from data
 * =========================================================
 */

@Composable
fun EmotionPieChart(entries: List<JournalEntry>) {

    // ---------------------------------------------------------
    // Step 1: Count emotions using helper algorithm
    // (Map<String, Int>)
    // ---------------------------------------------------------
    val counts = emotionCounts(entries)

    // ---------------------------------------------------------
    // Step 2: Calculate total items
    // Prevent divide-by-zero
    // ---------------------------------------------------------
    val total = counts.values.sum().coerceAtLeast(1)

    // ---------------------------------------------------------
    // Step 3: Colour palette
    // (Assessment UI requirement: clear visual distinction)
    // ---------------------------------------------------------
    val colors = listOf(
        Color(0xFF7EC8FF), // JOY
        Color(0xFFA8B8FF), // SADNESS
        Color(0xFFFFB3BA), // ANGER
        Color(0xFFFFDFA6), // FEAR
        Color(0xFFB5EAD7), // CALM
        Color(0xFFD6C6FF), // SURPRISE
        Color(0xFFE2F0FF)  // NEUTRAL
    )

    // ---------------------------------------------------------
    // Step 4: Draw pie chart using Canvas
    // ---------------------------------------------------------
    Canvas(
        modifier = Modifier
            .size(220.dp)
            .padding(8.dp)
    ) {

        var startAngle = -90f
        val radius = size.minDimension / 2f

        // -----------------------------------------------------
        // Step 5: Draw each emotion segment
        // -----------------------------------------------------
        counts.entries.forEachIndexed { index, entry ->

            val sweep =
                (entry.value.toFloat() / total.toFloat()) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = true
            )

            startAngle += sweep
        }

        // -----------------------------------------------------
        // Step 6: Donut center
        // -----------------------------------------------------
        drawCircle(
            color = Color.White.copy(alpha = 0.75f),
            radius = radius * 0.45f,
            center = center
        )
    }
}