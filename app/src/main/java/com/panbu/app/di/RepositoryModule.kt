package com.panbu.app.di

import com.panbu.app.data.repository.MoodRepository
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
}
