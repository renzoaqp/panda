package com.panbu.app.di

import com.panbu.app.data.repository.CalmSessionRepository
import com.panbu.app.data.repository.GoalRepository
import com.panbu.app.data.repository.MoodRepository
import com.panbu.app.data.repository.RoomCalmSessionRepository
import com.panbu.app.data.repository.RoomGoalRepository
import com.panbu.app.data.repository.RoomMoodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMoodRepository(impl: RoomMoodRepository): MoodRepository

    @Binds
    @Singleton
    abstract fun bindCalmSessionRepository(impl: RoomCalmSessionRepository): CalmSessionRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(impl: RoomGoalRepository): GoalRepository
}
