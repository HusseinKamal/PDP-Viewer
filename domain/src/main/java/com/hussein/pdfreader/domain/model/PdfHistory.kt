package com.hussein.pdfreader.domain.model

data class PdfHistory(
    val id: Int = 0,
    val name: String,
    val uri: String,
    val timestamp: Long = System.currentTimeMillis()
)