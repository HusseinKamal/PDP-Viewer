package com.hussein.pdfreader.feature.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hussein.pdfreader.feature.pdf.components.PdfNodeTree
import com.hussein.pdfreader.feature.pdf.mvi.PdfIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(
    initialUri: Uri? = null,
    onNavigateToHistory: () -> Unit,
    viewModel: PdfViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(initialUri) {
        if (initialUri != null) {
            viewModel.onIntent(PdfIntent.OpenPdf(initialUri))
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.onIntent(PdfIntent.OpenPdf(uri))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.document?.fileName ?: "PDF Reader") },
                actions = {
                    IconButton(onClick = { viewModel.onIntent(PdfIntent.ExpandAll) }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand All")
                    }
                    IconButton(onClick = { viewModel.onIntent(PdfIntent.CollapseAll) }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Collapse All")
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { pickerLauncher.launch("application/pdf") }) {
                        Icon(Icons.Default.Add, contentDescription = "Open File")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.document != null -> {
                    SelectionContainer {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(state.document!!.rootNodes) { node ->
                                PdfNodeTree(
                                    node = node,
                                    expandedNodes = state.expandedNodes,
                                    onToggle = { viewModel.onIntent(PdfIntent.ToggleNode(it)) }
                                )
                            }
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tap the + icon to open a PDF")
                    }
                }
            }
        }
    }
}
