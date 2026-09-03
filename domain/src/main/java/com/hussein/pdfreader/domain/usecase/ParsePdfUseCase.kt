package com.hussein.pdfreader.domain.usecase

import android.net.Uri
import com.hussein.pdfreader.domain.model.PdfDocument
import com.hussein.pdfreader.domain.repository.PdfRepository
import javax.inject.Inject

class ParsePdfUseCase @Inject constructor(
    private val repository: PdfRepository
) {
    suspend operator fun invoke(uri: Uri): Result<PdfDocument> = repository.parse(uri)
}
