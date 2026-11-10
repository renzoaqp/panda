package com.panbu.app.di

import com.panbu.app.reminders.AlarmReminderScheduler
import com.panbu.app.reminders.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: AlarmReminderScheduler): ReminderScheduler
}
