package com.hussein.pdfreader

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
@Composable
fun PdfViewer(uri: android.net.Uri, context: android.content.Context) {
    val renderer = remember(uri) { PdfRenderManager(context, uri) }

    // We only track scale. Offset is removed to prevent "changing position"
    var scale by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    // Zoom restricted between 1x (Fit) and 5x
                    scale = (scale * zoom).coerceIn(1f, 5f)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = {
                    // Double tap to toggle zoom
                    scale = if (scale > 1f) 1f else 2.5f
                })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    // By not applying translationX/Y, the image stays centered
                ),
            // Keep vertical scrolling enabled
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
                        // ContentScale.Fit ensures the image fits the screen width initially
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