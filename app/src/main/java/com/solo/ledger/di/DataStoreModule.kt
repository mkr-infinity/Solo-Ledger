package com.solo.ledger.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// SettingsDataStore uses @Singleton + @Inject constructor — Hilt binds it automatically.
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule
