package com.hussein.pdfreader.feature.history

import androidx.lifecycle.viewModelScope
import com.hussein.pdfreader.core.mvi.MviViewModel
import com.hussein.pdfreader.domain.usecase.GetHistoryUseCase
import com.hussein.pdfreader.domain.repository.PdfRepository
import com.hussein.pdfreader.feature.history.mvi.HistoryEffect
import com.hussein.pdfreader.feature.history.mvi.HistoryIntent
import com.hussein.pdfreader.feature.history.mvi.HistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val repository: PdfRepository
) : MviViewModel<HistoryState, HistoryIntent, HistoryEffect>(HistoryState()) {

    init {
        onIntent(HistoryIntent.LoadHistory)
    }

    override fun onIntent(intent: HistoryIntent) {
        when (intent) {
            HistoryIntent.LoadHistory -> loadHistory()
            is HistoryIntent.DeleteHistory -> deleteHistory(intent.pdf)
            is HistoryIntent.OnHistoryClick -> sendEffect(HistoryEffect.NavigateToPdf(intent.pdf.uri))
            HistoryIntent.DeleteAllHistory -> deleteAllHistory()
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getHistoryUseCase().collectLatest { list ->
                updateState { copy(history = list, isLoading = false) }
            }
        }
    }

    private fun deleteHistory(pdf: com.hussein.pdfreader.domain.model.PdfHistory) {
        viewModelScope.launch {
            repository.deletePdf(pdf)
        }
    }

    private fun deleteAllHistory() {
        viewModelScope.launch {
            repository.deleteAllPdfs()
        }
    }
}