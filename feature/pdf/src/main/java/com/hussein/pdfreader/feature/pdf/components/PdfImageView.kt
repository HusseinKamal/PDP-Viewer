package com.hussein.pdfreader.feature.pdf.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun PdfImageView(uri: String) {
    AsyncImage(
        model = uri,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentScale = ContentScale.Fit
    )
}
