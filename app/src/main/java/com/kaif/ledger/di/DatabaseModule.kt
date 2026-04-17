package com.kaif.ledger.di

import android.content.Context
import com.kaif.ledger.data.database.AppDatabase
import com.kaif.ledger.data.database.dao.CategoryDao
import com.kaif.ledger.data.database.dao.ExpenseDao
import com.kaif.ledger.data.database.dao.SettingsDao
import com.kaif.ledger.data.repository.CategoryRepository
import com.kaif.ledger.data.repository.ExpenseRepository
import com.kaif.ledger.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao {
        return database.expenseDao()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Singleton
    @Provides
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }

    @Singleton
    @Provides
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository {
        return ExpenseRepository(expenseDao)
    }

    @Singleton
    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepository(categoryDao)
    }

    @Singleton
    @Provides
    fun provideSettingsRepository(settingsDao: SettingsDao): SettingsRepository {
        return SettingsRepository(settingsDao)
    }
}
