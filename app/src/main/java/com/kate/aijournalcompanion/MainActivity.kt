package com.kate.aijournalcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
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

    // Tools panel
    var toolsExpanded by remember { mutableStateOf(false) }

    // Store journal history in memory
    val journalEntries = remember { mutableStateListOf<JournalEntry>() }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Journal Companion")
                        Text("Track, Reflect, Grow", style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showChart = true },
                        enabled = journalEntries.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = "Show Chart"
                        )
                    }
                    IconButton(onClick = { showHelp = true }) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Help"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ---------- Input Card ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text("Write your journal entry", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = journalText,
                        onValueChange = { journalText = it },
                        placeholder = { Text("What happened today? How did you feel?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
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
                                            text = journalText,
                                            emotion = response.emotion,
                                            advice = response.advice
                                        )
                                    )

                                    journalText = ""
                                } catch (_: Exception) {
                                    emotion = "ERROR"
                                    advice = "Could not connect to backend."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Analyze")
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // ---------- Latest Result ----------
            if (emotion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

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
            }

            // ---------- Tools Panel (Sort + Search) ----------
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

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

                        // ---- Sort dropdown ----
                        ExposedDropdownMenuBox(
                            expanded = sortExpanded,
                            onExpandedChange = { sortExpanded = !sortExpanded }
                        ) {
                            OutlinedTextField(
                                value = sortMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Sort method") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sort History by Emotion")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ---- Search input ----
                        OutlinedTextField(
                            value = searchEmotion,
                            onValueChange = { searchEmotion = it },
                            label = { Text("Search emotion (e.g., JOY)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // ---- Search dropdown ----
                        ExposedDropdownMenuBox(
                            expanded = searchExpanded,
                            onExpandedChange = { searchExpanded = !searchExpanded }
                        ) {
                            OutlinedTextField(
                                value = searchMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Search method") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = searchExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Search")
                        }
                    }
                }
            }

            // ---------- Search Results ----------
            if (searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text("Search Results", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                searchResults.forEach { entry ->
                    JournalEntryCard(entry = entry)
                }
            }

            // ---------- History ----------
            if (journalEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Journal History", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                journalEntries.asReversed().forEach { entry ->
                    JournalEntryCard(entry = entry)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // ---------- Chart Dialog ----------
        if (showChart) {
            AlertDialog(
                onDismissRequest = { showChart = false },
                confirmButton = {
                    TextButton(onClick = { showChart = false }) { Text("Close") }
                },
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
                            Text(
                                text = "$emo: $count ($percent%)",
                                style = MaterialTheme.typography.bodyMedium
                            )
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

@Composable
private fun JournalEntryCard(entry: JournalEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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

private fun emotionCounts(entries: List<JournalEntry>): Map<String, Int> {
    return entries
        .groupBy { normalizeEmotion(it.emotion) }
        .mapValues { it.value.size }
        .toList()
        .sortedByDescending { it.second }
        .toMap()
}