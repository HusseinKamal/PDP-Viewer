package com.hussein.pdfreader

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun PdfViewer(uri: Uri, context: Context) {
    val renderer = remember(uri) { PdfRenderManager(context, uri) }

    // Zoom/Pan State
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // 1. Handle Zoom
                    val oldScale = scale
                    scale = (scale * zoom).coerceIn(1f, 10f)

                    // 2. Handle Drag (Panning)
                    // We only allow panning if the image is zoomed in
                    if (scale > 1f) {
                        // This logic ensures that the "drag" follows the finger correctly
                        offset = Offset(
                            x = offset.x + pan.x,
                            y = offset.y + pan.y
                        )
                    } else {
                        // Reset offset when zoomed out to 1x
                        offset = Offset.Zero
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Toggle between 1x and 3x zoom on double tap
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 3f
                        }
                    }
                )
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            // Disable scrolling of the list itself when zoomed in
            // so the "Drag" pans the document instead of scrolling the list
            userScrollEnabled = scale <= 1.1f
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
                            .padding(vertical = 4.dp),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }

    DisposableEffect(uri) {
        onDispose { renderer.close() }
    }
}