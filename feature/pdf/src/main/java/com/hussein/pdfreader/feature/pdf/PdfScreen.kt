package com.hussein.pdfreader.feature.pdf

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hussein.pdfreader.feature.pdf.mvi.PdfIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfScreen(
    initialUri: Uri? = null,
    viewModel: PdfViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(initialUri) {
        if (initialUri != null) {
            viewModel.onIntent(PdfIntent.LoadPdf(initialUri))
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) viewModel.onIntent(PdfIntent.LoadPdf(uri))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
                actions = {
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
            state.uri?.let { uri ->
                PdfViewer(uri, state.scale, viewModel::onIntent)
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tap the + icon to open a PDF")
            }
        }
    }
}

@Composable
fun PdfViewer(
    uri: Uri,
    scale: Float,
    onIntent: (PdfIntent) -> Unit
) {
    val context = LocalContext.current
    val renderer = remember(uri) { PdfRenderManager(context, uri) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    onIntent(PdfIntent.UpdateScale(scale * zoom))
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    onIntent(PdfIntent.ToggleZoom)
                })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                ),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(renderer.pageCount) { index ->
                var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

                LaunchedEffect(index) {
                    bitmap = renderer.renderPage(index)
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    DisposableEffect(uri) {
        onDispose { renderer.close() }
    }
}