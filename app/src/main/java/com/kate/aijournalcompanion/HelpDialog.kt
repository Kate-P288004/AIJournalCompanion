package com.kate.aijournalcompanion

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpDialog(onClose: () -> Unit) {

    Dialog(onDismissRequest = onClose) {
        Surface {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Help") },
                        actions = {
                            TextButton(onClick = onClose) { Text("Close") }
                        }
                    )
                }
            ) { innerPadding ->

                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    factory = { context ->

                        val html = context.assets.open("help.html")
                            .bufferedReader()
                            .use { it.readText() }

                        WebView(context).apply {
                            webViewClient = WebViewClient()

                            // Static HTML, no JS needed
                            settings.javaScriptEnabled = false
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                            settings.allowContentAccess = true

                            // Scrolling works and scrollbars show
                            isVerticalScrollBarEnabled = true
                            isHorizontalScrollBarEnabled = false
                            overScrollMode = WebView.OVER_SCROLL_ALWAYS

                            loadDataWithBaseURL(
                                "file:///android_asset/",
                                html,
                                "text/html",
                                "UTF-8",
                                null
                            )

                        }
                    }
                )
            }
        }
    }
}