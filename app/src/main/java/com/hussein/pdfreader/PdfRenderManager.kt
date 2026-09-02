package com.hussein.pdfreader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.graphics.createBitmap

class PdfRenderManager(context: Context, uri: Uri) {
    private val contentResolver = context.contentResolver
    private var pdfRenderer: PdfRenderer? = null
    private var fileDescriptor: ParcelFileDescriptor? = null

    init {
        fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        fileDescriptor?.let {
            pdfRenderer = PdfRenderer(it)
        }
    }

    val pageCount: Int get() = pdfRenderer?.pageCount ?: 0

    suspend fun renderPage(pageIndex: Int): Bitmap? = withContext(Dispatchers.IO) {
        try {
            pdfRenderer?.let { renderer ->
                val page = renderer.openPage(pageIndex)
                // High quality bitmap: Using 2x or 3x scale for better zoom clarity
                val bitmap = createBitmap(page.width * 2, page.height * 2)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                return@withContext bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    fun close() {
        pdfRenderer?.close()
        fileDescriptor?.close()
    }
}