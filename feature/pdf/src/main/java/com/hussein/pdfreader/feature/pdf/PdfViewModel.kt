package com.hussein.pdfreader.feature.pdf

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.viewModelScope
import com.hussein.pdfreader.core.mvi.MviViewModel
import com.hussein.pdfreader.domain.model.PdfHistory
import com.hussein.pdfreader.domain.usecase.SavePdfUseCase
import com.hussein.pdfreader.feature.pdf.mvi.PdfEffect
import com.hussein.pdfreader.feature.pdf.mvi.PdfIntent
import com.hussein.pdfreader.feature.pdf.mvi.PdfState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savePdfUseCase: SavePdfUseCase
) : MviViewModel<PdfState, PdfIntent, PdfEffect>(PdfState()) {

    override fun onIntent(intent: PdfIntent) {
        when (intent) {
            is PdfIntent.LoadPdf -> loadPdf(intent.uri)
            is PdfIntent.UpdateScale -> updateScale(intent.scale)
            PdfIntent.ToggleZoom -> toggleZoom()
        }
    }

    private fun loadPdf(uri: Uri) {
        val title = getFileName(context, uri)
        updateState { copy(uri = uri, title = title, scale = 1f) }
        
        viewModelScope.launch {
            savePdfUseCase(PdfHistory(name = title, uri = uri.toString()))
        }
    }

    private fun updateScale(scale: Float) {
        updateState { copy(scale = scale.coerceIn(1f, 5f)) }
    }

    private fun toggleZoom() {
        val newScale = if (currentState.scale > 1f) 1f else 2.5f
        updateState { copy(scale = newScale) }
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "PDF Viewer"
    }
}