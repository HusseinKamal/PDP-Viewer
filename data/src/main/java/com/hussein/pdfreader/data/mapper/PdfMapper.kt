package com.hussein.pdfreader.data.mapper

import com.hussein.pdfreader.data.local.entity.PdfHistoryEntity
import com.hussein.pdfreader.domain.model.PdfHistory

fun PdfHistoryEntity.toDomain() = PdfHistory(
    id = id,
    name = name,
    uri = uri,
    timestamp = timestamp
)

fun PdfHistory.toEntity() = PdfHistoryEntity(
    id = id,
    name = name,
    uri = uri,
    timestamp = timestamp
)