package com.panbu.app.di

import com.panbu.app.device.NoPanbuDevice
import com.panbu.app.device.PanbuDevice
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceModule {

    /** Fase 5: cambiar NoPanbuDevice por BlePanbuDevice (docs/07-bluetooth.md). */
    @Binds
    @Singleton
    abstract fun bindPanbuDevice(impl: NoPanbuDevice): PanbuDevice
}
