package com.panbu.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.panbu.app.data.preferences.DataStorePreferencesRepository
import com.panbu.app.data.preferences.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Preferencias en DataStore: un solo archivo (`panbu_prefs`) y una sola instancia para toda la app. */
@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(impl: DataStorePreferencesRepository): PreferencesRepository

    companion object {
        const val FILE_NAME = "panbu_prefs"

        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(FILE_NAME) }
    }
}
