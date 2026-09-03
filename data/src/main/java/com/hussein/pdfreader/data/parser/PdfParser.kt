package com.hussein.pdfreader.data.parser

import android.content.Context
import android.net.Uri
import com.hussein.pdfreader.domain.model.PdfDocument
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class PdfParser @Inject constructor(
    @ApplicationContext private val context: Context,
    private val textExtractor: PdfTextExtractor,
    private val layoutAnalyzer: PdfLayoutAnalyzer,
    private val hierarchyBuilder: PdfHierarchyBuilder,
    private val imageExtractor: PdfImageExtractor
) {
    init {
        PDFBoxResourceLoader.init(context)
    }

    suspend fun parse(uri: Uri): Result<PdfDocument> = withContext(Dispatchers.IO) {
        runCatching {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Cannot open URI: $uri")
            
            val document = PDDocument.load(inputStream)
            val docId = UUID.randomUUID().toString()
            val fileName = getFileName(uri)
            
            try {
                val pages = document.numberOfPages
                val blocks = mutableListOf<PdfBlock>()
                
                for (i in 0 until pages) {
                    val page = document.getPage(i)
                    // Extract text blocks
                    blocks.addAll(textExtractor.extract(page, i + 1))
                    // Extract images
                    blocks.addAll(imageExtractor.extract(page, i + 1, docId))
                }
                
                val analyzedBlocks = layoutAnalyzer.analyze(blocks)
                val rootNodes = hierarchyBuilder.build(analyzedBlocks)
                
                PdfDocument(
                    id = docId,
                    fileName = fileName,
                    pageCount = pages,
                    rootNodes = rootNodes
                )
            } finally {
                document.close()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        return uri.lastPathSegment ?: "document.pdf"
    }
}

data class PdfBlock(
    val type: BlockType,
    val text: String = "",
    val fontSize: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f,
    val pageNumber: Int,
    val imageUri: String? = null
)

enum class BlockType {
    HEADING_1, HEADING_2, HEADING_3, PARAGRAPH, IMAGE, TABLE, PAGE_BREAK
}
