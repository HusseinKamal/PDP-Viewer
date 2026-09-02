package com.hussein.pdfreader.feature.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor

class PdfRenderManager(context: Context, uri: Uri) {
    private var fileDescriptor: ParcelFileDescriptor? = null
    private var renderer: PdfRenderer? = null

    init {
        try {
            fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            if (fileDescriptor != null) {
                renderer = PdfRenderer(fileDescriptor!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val pageCount: Int get() = renderer?.pageCount ?: 0

    fun renderPage(pageIndex: Int): Bitmap? {
        if (renderer == null || pageIndex >= pageCount) return null
        
        return try {
            val page = renderer!!.openPage(pageIndex)
            // Create a high-quality bitmap
            val bitmap = Bitmap.createBitmap(
                page.width * 2,
                page.height * 2,
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun close() {
        try {
            renderer?.close()
            fileDescriptor?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}