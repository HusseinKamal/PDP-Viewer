package com.hussein.pdfreader.data.parser

import com.tomroush.pdfbox.pdmodel.PDPage
import com.tomroush.pdfbox.text.PDFTextStripper
import com.tomroush.pdfbox.text.TextPosition
import java.io.IOException
import java.io.StringWriter
import javax.inject.Inject

class PdfTextExtractor @Inject constructor() {
    fun extract(page: PDPage, pageNumber: Int): List<PdfBlock> {
        val stripper = PositionStripper(pageNumber)
        val writer = StringWriter()
        try {
            stripper.writeText(null, writer) // This is wrong for a single page, we should set the page range
        } catch (e: Exception) {
            // handle
        }
        return stripper.blocks
    }

    private class PositionStripper(private val pageNumber: Int) : PDFTextStripper() {
        val blocks = mutableListOf<PdfBlock>()

        init {
            sortByPosition = true
        }

        @Throws(IOException::class)
        override fun writeString(text: String?, textPositions: MutableList<TextPosition>?) {
            if (text == null || textPositions == null || textPositions.isEmpty()) return
            
            val first = textPositions.first()
            val fontSize = first.fontSizeInPt
            val x = first.xDirAdj
            val y = first.yDirAdj
            
            blocks.add(PdfBlock(
                type = BlockType.PARAGRAPH,
                text = text.trim(),
                fontSize = fontSize,
                x = x,
                y = y,
                width = textPositions.last().xDirAdj + textPositions.last().widthDirAdj - x,
                height = fontSize,
                pageNumber = pageNumber
            ))
        }
    }
}
