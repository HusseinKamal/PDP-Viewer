package com.hussein.pdfreader.domain.usecase

import com.hussein.pdfreader.domain.repository.PdfRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    operator fun invoke() = repository.getHistory()
}