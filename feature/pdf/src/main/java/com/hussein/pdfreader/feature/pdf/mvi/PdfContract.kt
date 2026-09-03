package com.hussein.pdfreader.feature.pdf.mvi

import android.net.Uri
import com.hussein.pdfreader.domain.model.PdfDocument

data class PdfState(
    val uri: Uri? = null,
    val document: PdfDocument? = null,
    val expandedNodes: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface PdfIntent {
    data class OpenPdf(val uri: Uri) : PdfIntent
    data class ToggleNode(val nodeId: String) : PdfIntent
    object ExpandAll : PdfIntent
    object CollapseAll : PdfIntent
}

sealed interface PdfEffect {
    data class ShowError(val message: String) : PdfEffect
}
