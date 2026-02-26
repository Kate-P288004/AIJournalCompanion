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

            OutlinedTextField(
                value = journalText,
                onValueChange = { journalText = it },
                label = { Text("Write your journal...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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

                            // Add to history
                            journalEntries.add(
                                JournalEntry(
                                    text = journalText,
                                    emotion = response.emotion,
                                    advice = response.advice
                                )
                            )

                            journalText = ""

                        } catch (e: Exception) {
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

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (emotion.isNotEmpty()) {
                Text(
                    text = "Emotion: $emotion",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Advice: $advice",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // History list
            if (journalEntries.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Journal History",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                journalEntries.asReversed().forEach { entry ->
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
}