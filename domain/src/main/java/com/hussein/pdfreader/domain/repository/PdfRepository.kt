package com.hussein.pdfreader.domain.repository

import com.hussein.pdfreader.domain.model.PdfHistory
import kotlinx.coroutines.flow.Flow

interface PdfRepository {
    fun getHistory(): Flow<List<PdfHistory>>
    suspend fun savePdf(pdf: PdfHistory)
    suspend fun deletePdf(pdf: PdfHistory)
    suspend fun deleteAllPdfs()
}