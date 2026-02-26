package com.kate.aijournalcompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
             JournalScreen()
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

    var showChart by remember { mutableStateOf(false) }

    // Search UI state
    var searchMethod by remember { mutableStateOf("Binary Tree") }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchEmotion by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<JournalEntry>() }

    var showHelp by remember { mutableStateOf(false) }

    // Store journal history in memory
    val journalEntries = remember { mutableStateListOf<JournalEntry>() }

    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = { TopAppBar(title = { Text("AI Journal Companion") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ---- Input ----
            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                label = { Text("Write your journal...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Analyze ----
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

            Spacer(modifier = Modifier.height(12.dp))

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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    modifier = Modifier
                        .menuAnchor()
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

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showChart = true },
                enabled = journalEntries.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Show Chart")
            }
            // ---- Search input ----
            OutlinedTextField(
                value = searchEmotion,
                onValueChange = { searchEmotion = it },
                label = { Text("Search emotion (e.g., JOY)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = searchExpanded) },
                    modifier = Modifier
                        .menuAnchor()
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

            Spacer(modifier = Modifier.height(12.dp))

            // ---- Search button ----
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

            Spacer(modifier = Modifier.height(24.dp))

            // ---- Loading ----
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ---- Latest result ----
            if (emotion.isNotEmpty()) {
                Text(text = "Emotion: $emotion", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Advice: $advice", style = MaterialTheme.typography.bodyLarge)
            }

            // ---- Search Results ----
            if (searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Search Results", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                searchResults.forEach { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = entry.text)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Emotion: ${entry.emotion}")
                            Text(text = "Advice: ${entry.advice}")
                        }
                    }
                }
            }
            if (showChart) {
                AlertDialog(
                    onDismissRequest = { showChart = false },
                    confirmButton = {
                        TextButton(onClick = { showChart = false }) { Text("Close") }
                    },
                    title = { Text("Emotion Distribution") },
                    text = { EmotionPieChart(entries = journalEntries) }
                )
            }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showHelp = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Help")
                }

                if (showHelp) {
                    HelpDialog(onClose = { showHelp = false })
                }
            }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { showHelp = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Help")
        }

        if (showHelp) {
            HelpDialog(onClose = { showHelp = false })
        }
            // ---- History ----
            if (journalEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Journal History", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                journalEntries.forEach { entry ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = entry.text)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Emotion: ${entry.emotion}")
                            Text(text = "Advice: ${entry.advice}")
                        }
                    }
                }

            }
        }
    }

