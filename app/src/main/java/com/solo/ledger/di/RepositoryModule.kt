package com.solo.ledger.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * TransactionRepository, CategoryRepository, and BudgetRepository all use
 * @Inject constructor with @Singleton, so Hilt provides them automatically
 * via constructor injection. No explicit bindings are needed here.
 *
 * This module is retained as an extension point for future interface-based
 * repository abstractions.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
