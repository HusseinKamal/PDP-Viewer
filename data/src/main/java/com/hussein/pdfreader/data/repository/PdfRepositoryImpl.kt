package com.hussein.pdfreader.data.repository

import android.net.Uri
import com.hussein.pdfreader.data.local.dao.PdfHistoryDao
import com.hussein.pdfreader.data.mapper.toDomain
import com.hussein.pdfreader.data.mapper.toEntity
import com.hussein.pdfreader.data.parser.PdfParser
import com.hussein.pdfreader.domain.model.PdfDocument
import com.hussein.pdfreader.domain.model.PdfHistory
import com.hussein.pdfreader.domain.repository.PdfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PdfRepositoryImpl @Inject constructor(
    private val dao: PdfHistoryDao,
    private val parser: PdfParser
) : PdfRepository {
    override fun getHistory(): Flow<List<PdfHistory>> {
        return dao.getHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun savePdf(pdf: PdfHistory) {
        if (pdf.uri.isNotBlank() && (pdf.uri.startsWith("content://") || pdf.uri.startsWith("file://"))) {
            dao.insert(pdf.toEntity())
        }
    }

    override suspend fun deletePdf(pdf: PdfHistory) {
        dao.delete(pdf.toEntity())
    }

    override suspend fun deleteAllPdfs() {
        dao.deleteAll()
    }

    override suspend fun parse(uri: Uri): Result<PdfDocument> {
        return parser.parse(uri)
    }
}