package com.hussein.pdfreader.feature.history.mvi

import com.hussein.pdfreader.domain.model.PdfHistory

data class HistoryState(
    val history: List<PdfHistory> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface HistoryIntent {
    object LoadHistory : HistoryIntent
    data class DeleteHistory(val pdf: PdfHistory) : HistoryIntent
    data class OnHistoryClick(val pdf: PdfHistory) : HistoryIntent
    object DeleteAllHistory : HistoryIntent
}

sealed interface HistoryEffect {
    data class NavigateToPdf(val uri: String) : HistoryEffect
    data class ShowError(val message: String) : HistoryEffect
}