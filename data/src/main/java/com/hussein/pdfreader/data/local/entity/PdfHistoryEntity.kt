package com.hussein.pdfreader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pdf_history")
data class PdfHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val uri: String,
    val timestamp: Long
)