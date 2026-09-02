package com.hussein.pdfreader.data.di

import android.content.Context
import androidx.room.Room
import com.hussein.pdfreader.data.local.PdfDatabase
import com.hussein.pdfreader.data.local.dao.PdfHistoryDao
import com.hussein.pdfreader.data.repository.PdfRepositoryImpl
import com.hussein.pdfreader.domain.repository.PdfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providePdfDatabase(@ApplicationContext context: Context): PdfDatabase {
        return Room.databaseBuilder(
            context,
            PdfDatabase::class.java,
            "pdf_db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePdfHistoryDao(db: PdfDatabase): PdfHistoryDao = db.dao

    @Provides
    @Singleton
    fun providePdfRepository(dao: PdfHistoryDao): PdfRepository {
        return PdfRepositoryImpl(dao)
    }
}