package com.hussein.pdfreader.domain.model

data class PdfDocument(
    val id: String,
    val fileName: String,
    val pageCount: Int,
    val rootNodes: List<PdfNode>
)

data class PdfNode(
    val id: String,
    val title: String,
    val level: Int,
    val elements: List<PdfElement>,
    val children: List<PdfNode>
)

sealed interface PdfElement {
    data class Paragraph(val text: String) : PdfElement
    data class Image(val uri: String, val width: Float, val height: Float) : PdfElement
    data class Table(val rows: List<PdfTableRow>) : PdfElement
    data class PageBreak(val pageNumber: Int) : PdfElement
}

data class PdfTableRow(val cells: List<PdfTableCell>)
data class PdfTableCell(val text: String, val columnIndex: Int)
