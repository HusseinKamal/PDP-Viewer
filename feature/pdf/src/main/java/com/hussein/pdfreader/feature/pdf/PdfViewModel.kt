package com.hussein.pdfreader.feature.pdf

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.hussein.pdfreader.core.mvi.MviViewModel
import com.hussein.pdfreader.domain.model.PdfDocument
import com.hussein.pdfreader.domain.model.PdfHistory
import com.hussein.pdfreader.domain.model.PdfNode
import com.hussein.pdfreader.domain.usecase.ParsePdfUseCase
import com.hussein.pdfreader.domain.usecase.SavePdfUseCase
import com.hussein.pdfreader.feature.pdf.mvi.PdfEffect
import com.hussein.pdfreader.feature.pdf.mvi.PdfIntent
import com.hussein.pdfreader.feature.pdf.mvi.PdfState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfViewModel @Inject constructor(
    private val parsePdfUseCase: ParsePdfUseCase,
    private val savePdfUseCase: SavePdfUseCase
) : MviViewModel<PdfState, PdfIntent, PdfEffect>(PdfState()) {

    override fun onIntent(intent: PdfIntent) {
        when (intent) {
            is PdfIntent.OpenPdf -> openPdf(intent.uri)
            is PdfIntent.ToggleNode -> toggleNode(intent.nodeId)
            PdfIntent.ExpandAll -> expandAll()
            PdfIntent.CollapseAll -> collapseAll()
        }
    }

    private fun openPdf(uri: Uri) {
        updateState { copy(isLoading = true, error = null, uri = uri) }
        
        viewModelScope.launch {
            parsePdfUseCase(uri)
                .onSuccess { doc ->
                    updateState { copy(isLoading = false, document = doc) }
                    savePdfUseCase(PdfHistory(name = doc.fileName, uri = uri.toString()))
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false, error = error.message) }
                    sendEffect(PdfEffect.ShowError(error.message ?: "Unknown error"))
                }
        }
    }

    private fun toggleNode(nodeId: String) {
        updateState {
            val newExpanded = if (expandedNodes.contains(nodeId)) {
                expandedNodes - nodeId
            } else {
                expandedNodes + nodeId
            }
            copy(expandedNodes = newExpanded)
        }
    }

    private fun expandAll() {
        val allNodeIds = mutableSetOf<String>()
        currentState.document?.rootNodes?.let { nodes ->
            collectAllNodeIds(nodes, allNodeIds)
        }
        updateState { copy(expandedNodes = allNodeIds) }
    }

    private fun collapseAll() {
        updateState { copy(expandedNodes = emptySet()) }
    }

    private fun collectAllNodeIds(nodes: List<PdfNode>, ids: MutableSet<String>) {
        nodes.forEach { node ->
            ids.add(node.id)
            collectAllNodeIds(node.children, ids)
        }
    }
}
