package com.hussein.pdfreader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hussein.pdfreader.data.local.dao.PdfHistoryDao
import com.hussein.pdfreader.data.local.entity.PdfHistoryEntity

@Database(entities = [PdfHistoryEntity::class], version = 1, exportSchema = false)
abstract class PdfDatabase : RoomDatabase() {
    abstract val dao: PdfHistoryDao
}