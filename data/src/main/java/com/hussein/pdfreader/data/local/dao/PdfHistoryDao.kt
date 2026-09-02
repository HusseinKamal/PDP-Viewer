package com.hussein.pdfreader.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hussein.pdfreader.data.local.entity.PdfHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PdfHistoryDao {
    @Query("SELECT * FROM pdf_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<PdfHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pdf: PdfHistoryEntity)

    @Delete
    suspend fun delete(pdf: PdfHistoryEntity)

    @Query("DELETE FROM pdf_history")
    suspend fun deleteAll()
}