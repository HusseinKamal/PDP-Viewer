package com.hussein.pdfreader.domain.model

data class PdfHistory(
    val name: String,
    val uri: String,
    val timestamp: Long = System.currentTimeMillis()
)