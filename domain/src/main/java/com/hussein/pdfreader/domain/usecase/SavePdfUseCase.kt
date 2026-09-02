package com.hussein.pdfreader.domain.usecase

import com.hussein.pdfreader.domain.model.PdfHistory
import com.hussein.pdfreader.domain.repository.PdfRepository
import javax.inject.Inject

class SavePdfUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(pdf: PdfHistory) = repository.savePdf(pdf)
}