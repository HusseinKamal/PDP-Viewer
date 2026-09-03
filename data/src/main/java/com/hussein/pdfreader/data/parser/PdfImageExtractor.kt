package com.hussein.pdfreader.data.parser

import android.content.Context
import android.graphics.Bitmap
import com.tomroush.pdfbox.cos.COSName
import com.tomroush.pdfbox.pdmodel.PDPage
import com.tomroush.pdfbox.pdmodel.graphics.image.PDImageXObject
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

class PdfImageExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun extract(page: PDPage, pageNumber: Int, docId: String): List<PdfBlock> {
        val blocks = mutableListOf<PdfBlock>()
        val resources = page.resources ?: return emptyList()
        
        resources.xObjectNames.forEach { name: COSName ->
            val xObject = resources.getXObject(name)
            if (xObject is PDImageXObject) {
                val bitmap = xObject.image
                val imageId = UUID.randomUUID().toString()
                val file = saveToCache(bitmap, docId, imageId)
                
                blocks.add(PdfBlock(
                    type = BlockType.IMAGE,
                    imageUri = file.absolutePath,
                    width = xObject.width.toFloat(),
                    height = xObject.height.toFloat(),
                    pageNumber = pageNumber
                ))
            }
        }
        return blocks
    }

    private fun saveToCache(bitmap: Bitmap, docId: String, imageId: String): File {
        val cacheDir = File(context.cacheDir, "pdf/$docId")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        
        val file = File(cacheDir, "$imageId.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }
}
