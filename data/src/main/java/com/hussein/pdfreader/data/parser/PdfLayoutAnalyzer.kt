package com.hussein.pdfreader.data.parser

import javax.inject.Inject

class PdfLayoutAnalyzer @Inject constructor() {
    fun analyze(blocks: List<PdfBlock>): List<PdfBlock> {
        if (blocks.isEmpty()) return emptyList()

        // Calculate average font size to determine headings
        val fontSizes = blocks.filter { it.type == BlockType.PARAGRAPH }.map { it.fontSize }
        val avgFontSize = if (fontSizes.isNotEmpty()) fontSizes.average().toFloat() else 12f

        return blocks.map { block ->
            if (block.type == BlockType.PARAGRAPH) {
                val newType = when {
                    block.fontSize > avgFontSize * 1.5f -> BlockType.HEADING_1
                    block.fontSize > avgFontSize * 1.2f -> BlockType.HEADING_2
                    block.fontSize > avgFontSize * 1.1f -> BlockType.HEADING_3
                    else -> BlockType.PARAGRAPH
                }
                block.copy(type = newType)
            } else {
                block
            }
        }
    }
}
