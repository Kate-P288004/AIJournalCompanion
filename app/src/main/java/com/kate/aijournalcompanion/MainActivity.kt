package com.kate.aijournalcompanion

/**
 * =========================================================
 * Student: Kate Odabas
 * Project: AI Journal Companion (AT2 – OOP3)
 *
 * Purpose:
 * - Main Compose UI screen for the journal app
 * - Sends journal text to backend (FastAPI) for emotion + advice
 * - Stores history in memory and allows:
 *   - Sorting using selected algorithm (Bubble / Insertion / Selection)
 *   - Searching using selected data structure (Binary Tree / HashMap / Doubly Linked List)
 *   - Visualisation of emotion distribution (Pie chart + legend)
 *
 * Assessment Concepts Demonstrated:
 * - Android Compose UI
 * - Networking via Retrofit client
 * - Algorithms: sorting + searching
 * - Data structures: Binary Tree, HashMap, Doubly Linked List
 * - Data using Canvas
 * =========================================================
 */

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.kate.aijournalcompanion.ui.theme.AIJournalCompanionTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIJournalCompanionTheme {
                JournalScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen() {

    var journalText by remember { mutableStateOf("") }
    var emotion by remember { mutableStateOf("") }
    var advice by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Sorting UI state
    var sortMethod by remember { mutableStateOf("Bubble") }
    var sortExpanded by remember { mutableStateOf(false) }

    // Search UI state
    var searchMethod by remember { mutableStateOf("Binary Tree") }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchEmotion by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<JournalEntry>() }

    // Dialogs
    var showChart by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }
    var nextId by remember { mutableStateOf(0) }

    // Tools panel
    var toolsExpanded by remember { mutableStateOf(false) }

    // Journal history stored in memory during app session
    val journalEntries = remember { mutableStateListOf<JournalEntry>() }

    val scope = rememberCoroutineScope()

    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFDDF1FF), // icy blue
                        Color(0xFFECE7FF), // soft purple
                        Color(0xFFF4FAFF)  // cold white
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AnimatedGradientTopBar(
                    title = "AI Journal Companion",
                    subtitle = "Track, Reflect, Grow",
                    onChart = { showChart = true },
                    chartEnabled = journalEntries.isNotEmpty(),
                    onHelp = { showHelp = true }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                // ---------- Input Card ----------
                GlassCard(modifier = Modifier.fillMaxWidth()) {

                    Text("Write your journal entry", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("What happened today? How did you feel?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            focusedContainerColor = Color.White.copy(alpha = 0.28f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.20f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                if (journalText.isBlank()) {
                                    emotion = "ERROR"
                                    advice = "Please type something first."
                                    return@launch
                                }

                                try {
                                    isLoading = true

                                    val response = ApiClient.api.analyze(JournalRequest(journalText))

                                    emotion = response.emotion
                                    advice = response.advice

                                    journalEntries.add(
                                        JournalEntry(
                                            id = nextId,
                                            text = journalText,
                                            emotion = response.emotion,
                                            advice = response.advice
                                        )
                                    )
                                    nextId++

                                } catch (_: Exception) {
                                    emotion = "ERROR"
                                    advice = "Could not connect to backend."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Analyze")
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        )
                    }
                }

                // ---------- Latest Result ----------
                if (emotion.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Latest result", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text("Emotion: ${normalizeEmotion(emotion)}") }
                            )

                            Text(
                                text = emotionEmoji(emotion),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = advice,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // ---------- Tools Panel ----------
                Spacer(modifier = Modifier.height(14.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tools", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { toolsExpanded = !toolsExpanded }) {
                            Text(if (toolsExpanded) "Hide" else "Show")
                        }
                    }

                    if (toolsExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = sortExpanded,
                            onExpandedChange = { sortExpanded = !sortExpanded }
                        ) {
                            OutlinedTextField(
                                value = sortMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sort method") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.28f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.20f)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = sortExpanded,
                                onDismissRequest = { sortExpanded = false }
                            ) {
                                listOf("Bubble", "Insertion", "Selection").forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method) },
                                        onClick = {
                                            sortMethod = method
                                            sortExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val temp = journalEntries.toMutableList()

                                when (sortMethod) {
                                    "Bubble" -> SortUtils.bubbleSort(temp)
                                    "Insertion" -> SortUtils.insertionSort(temp)
                                    "Selection" -> SortUtils.selectionSort(temp)
                                }

                                journalEntries.clear()
                                journalEntries.addAll(temp)
                            },
                            enabled = journalEntries.size > 1,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Sort History by Emotion")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = searchEmotion,
                            onValueChange = { searchEmotion = it },
                            label = { Text("Search emotion (e.g., JOY)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                focusedContainerColor = Color.White.copy(alpha = 0.28f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.20f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ExposedDropdownMenuBox(
                            expanded = searchExpanded,
                            onExpandedChange = { searchExpanded = !searchExpanded }
                        ) {
                            OutlinedTextField(
                                value = searchMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Search method") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = searchExpanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                    focusedContainerColor = Color.White.copy(alpha = 0.28f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.20f)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = searchExpanded,
                                onDismissRequest = { searchExpanded = false }
                            ) {
                                listOf("Binary Tree", "HashMap", "Doubly Linked List").forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method) },
                                        onClick = {
                                            searchMethod = method
                                            searchExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val results = when (searchMethod) {
                                    "Binary Tree" -> SearchUtils.searchWithBinaryTree(journalEntries, searchEmotion)
                                    "HashMap" -> SearchUtils.searchWithHashMap(journalEntries, searchEmotion)
                                    else -> SearchUtils.searchWithDoublyLinkedList(journalEntries, searchEmotion)
                                }
                                searchResults.clear()
                                searchResults.addAll(results)
                            },
                            enabled = journalEntries.isNotEmpty() && searchEmotion.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Search")
                        }
                    }
                }

                // ---------- Search Results ----------
                if (searchResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Search Results", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    searchResults.forEach { entry ->
                        GlassJournalEntryCard(entry = entry)
                    }
                }

                // ---------- History ----------
                if (journalEntries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Journal History (Drag to delete)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    DragDropDeleteArea(
                        entries = journalEntries.asReversed(),
                        onDelete = { toDelete ->
                            journalEntries.removeAll { it.id == toDelete.id }
                            searchResults.removeAll { it.id == toDelete.id }

                            if (journalEntries.isEmpty()) {
                                emotion = ""
                                advice = ""
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        // ---------- Chart Dialog ----------
        if (showChart) {
            AlertDialog(
                onDismissRequest = { showChart = false },
                confirmButton = { TextButton(onClick = { showChart = false }) { Text("Close") } },
                title = { Text("Emotion Distribution") },
                text = {
                    val counts = emotionCounts(journalEntries)
                    val total = journalEntries.size.coerceAtLeast(1)

                    Column(modifier = Modifier.fillMaxWidth()) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(top = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmotionPieChart(entries = journalEntries)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        counts.forEach { (emo, count) ->
                            val percent = (count * 100) / total

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${emotionEmoji(emo)} $emo",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "$percent%",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            )
        }

        // ---------- Help Dialog ----------
        if (showHelp) {
            HelpDialog(onClose = { showHelp = false })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedGradientTopBar(
    title: String,
    subtitle: String,
    onChart: () -> Unit,
    chartEnabled: Boolean,
    onHelp: () -> Unit
) {
    val infinite = rememberInfiniteTransition(label = "topbar")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB9C8FF),
            Color(0xFF9DE3FF),
            Color(0xFFD7B8FF),
            Color(0xFFAAD7FF)
        ),
        start = Offset(0f + 900f * t, 0f),
        end = Offset(900f * (1f - t), 520f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .background(brush)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.04f))
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            renderEffect = RenderEffect
                                .createBlurEffect(8f, 8f, Shader.TileMode.CLAMP)
                                .asComposeRenderEffect()
                        }
                    } else Modifier
                )
        )

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = {
                Column {
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                }
            },
            actions = {
                IconButton(onClick = onChart, enabled = chartEnabled) {
                    Icon(Icons.Default.BarChart, contentDescription = "Chart")
                }
                IconButton(onClick = onHelp) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help")
                }
            }
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(26.dp)

    Card(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x22000000)
            )
            .clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.42f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.40f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

@Composable
fun GlassJournalEntryCard(entry: JournalEntry) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        AssistChip(
            onClick = {},
            label = { Text("${normalizeEmotion(entry.emotion)} ${emotionEmoji(entry.emotion)}") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            entry.text,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(entry.advice, style = MaterialTheme.typography.bodySmall)
    }
}

private fun normalizeEmotion(raw: String): String {
    return raw.trim()
        .uppercase()
        .replace("-", " ")
        .replace("_", " ")
}

private fun emotionEmoji(rawEmotion: String): String {
    val emotion = normalizeEmotion(rawEmotion)

    return when (emotion) {
        "JOY", "HAPPY", "HAPPINESS" -> "😊"
        "SAD", "SADNESS", "DEPRESSED", "DOWN" -> "😢"
        "ANGER", "ANGRY", "IRRITATED", "MAD" -> "😠"
        "FEAR", "FEARFUL", "ANXIOUS", "ANXIETY", "WORRIED", "NERVOUS" -> "😨"
        "DISGUST", "DISGUSTED" -> "🤢"
        "SURPRISE", "SURPRISED" -> "😲"
        "STRESS", "STRESSED", "OVERWHELMED" -> "😮‍💨"
        "TIRED", "EXHAUSTED", "SLEEPY" -> "😴"
        "LONELY" -> "🥺"
        "GUILT", "GUILTY" -> "😔"
        "EMBARRASSED" -> "😳"
        "FRUSTRATED" -> "😤"
        "EMPTY", "NUMB" -> "😶"
        "CALM", "RELAXED", "CONTENT" -> "😌"
        "CONFUSED" -> "😕"
        "ERROR" -> "⚠️"
        else -> "🙂"
    }
}

fun emotionCounts(entries: List<JournalEntry>): Map<String, Int> {
    return entries
        .groupBy { normalizeEmotion(it.emotion) }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .toMap()
}