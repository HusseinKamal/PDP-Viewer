package com.hussein.pdfreader.feature.pdf.components

import androidx.compose.runtime.Composable
import com.hussein.pdfreader.domain.model.PdfElement

@Composable
fun PdfElementView(element: PdfElement) {
    when (element) {
        is PdfElement.Paragraph -> PdfParagraphView(element.text)
        is PdfElement.Image -> PdfImageView(element.uri)
        is PdfElement.Table -> PdfTableView(element.rows)
        is PdfElement.PageBreak -> { /* Could show a divider or page number */ }
    }
}
