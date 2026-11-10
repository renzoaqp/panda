package com.panbu.app.di

import android.content.Context
import androidx.room.Room
import com.panbu.app.data.local.CalmSessionDao
import com.panbu.app.data.local.GoalDao
import com.panbu.app.data.local.MoodDao
import com.panbu.app.data.local.PanbuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // Sin fallbackToDestructiveMigration: borraría el diario y el historial (guardrail 19).
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PanbuDatabase =
        Room.databaseBuilder(context, PanbuDatabase::class.java, PanbuDatabase.NAME).build()

    @Provides
    fun provideMoodDao(database: PanbuDatabase): MoodDao = database.moodDao()

    @Provides
    fun provideCalmSessionDao(database: PanbuDatabase): CalmSessionDao = database.calmSessionDao()

    @Provides
    fun provideGoalDao(database: PanbuDatabase): GoalDao = database.goalDao()
}
