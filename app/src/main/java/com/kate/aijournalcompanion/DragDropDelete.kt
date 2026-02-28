package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Feature:
 * - Drag a journal entry onto the Trash area to delete it.
 *
 * Concepts:
 * - Compose gestures (drag)
 * - Layout coordinates (window rect hit test)
 * - State updates (remove item from list)
 * =========================================================
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun DragDropDeleteArea(
    entries: List<JournalEntry>,
    onDelete: (JournalEntry) -> Unit
) {
    var trashRect by remember { mutableStateOf<Rect?>(null) }
    val trashHeight = 70.dp

    Box(modifier = Modifier.fillMaxWidth()) {

        // Entries list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = trashHeight + 12.dp)
        ) {
            entries.forEach { entry ->
                DraggableJournalEntry(
                    entry = entry,
                    trashRectProvider = { trashRect },
                    onDropOnTrash = { onDelete(entry) }
                )
            }
        }

        // Trash Zone
        TrashZone(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(trashHeight)
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .onGloballyPositioned { coords ->
                    trashRect = coords.boundsInWindow()
                }
        )
    }
}

@Composable
private fun TrashZone(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.50f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Drag here to delete",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun DraggableJournalEntry(
    entry: JournalEntry,
    trashRectProvider: () -> Rect?,
    onDropOnTrash: () -> Unit
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    var itemCoords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var lastFingerWindowPos by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .zIndex(if (isDragging) 1f else 0f)
            .onGloballyPositioned { coords ->
                itemCoords = coords
            }
            .offset { IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt()) }
            .alpha(if (isDragging) 0.92f else 1f)
            // If you DON'T have entry.id, replace entry.id with entry.hashCode()
            .pointerInput(entry.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        lastFingerWindowPos = null
                    },
                    onDragEnd = {
                        isDragging = false

                        val trash = trashRectProvider()
                        val finger = lastFingerWindowPos

                        if (trash != null && finger != null && trash.contains(finger)) {
                            onDropOnTrash()
                        }

                        dragOffset = Offset.Zero
                        lastFingerWindowPos = null
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                        lastFingerWindowPos = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount

                        val coords = itemCoords
                        lastFingerWindowPos = if (coords != null) {
                            val base = coords.localToWindow(change.position)

                            Offset(base.x + dragOffset.x, base.y + dragOffset.y)
                        } else null

                    }
                )
            }
    ) {
        GlassJournalEntryCard(entry = entry)
    }
}