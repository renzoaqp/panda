package com.panbu.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Reloj inyectado: todo cálculo de "hoy", "ayer" o "esta semana" lo usa,
     * para que los tests puedan fijar la fecha con Clock.fixed.
     */
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()
}
