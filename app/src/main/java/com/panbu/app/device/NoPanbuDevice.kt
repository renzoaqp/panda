package com.panbu.app.device

import com.panbu.app.domain.model.BreathingPattern
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow

/** Implementación para cuando no hay peluche conectado (hasta la Fase 5). No hace nada. */
class NoPanbuDevice @Inject constructor() : PanbuDevice {

    private val state = MutableStateFlow<DeviceConnectionState>(DeviceConnectionState.NotPaired)

    override val connectionState: StateFlow<DeviceConnectionState> = state.asStateFlow()

    override val events: Flow<DeviceEvent> = emptyFlow()

    override suspend fun startBreathingGuide(pattern: BreathingPattern) = Unit

    override suspend fun stopBreathingGuide() = Unit
}
