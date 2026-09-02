package com.hussein.pdfreader.feature.pdf.mvi

import android.net.Uri

data class PdfState(
    val uri: Uri? = null,
    val title: String = "PDF Viewer",
    val scale: Float = 1f,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PdfIntent {
    data class LoadPdf(val uri: Uri) : PdfIntent
    data class UpdateScale(val scale: Float) : PdfIntent
    object ToggleZoom : PdfIntent
}

sealed interface PdfEffect {
    data class ShowError(val message: String) : PdfEffect
}