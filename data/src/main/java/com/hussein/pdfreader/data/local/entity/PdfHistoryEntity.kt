package com.hussein.pdfreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_history")
data class PdfHistoryEntity(
    @PrimaryKey val uri: String,
    val name: String,
    val timestamp: Long
)