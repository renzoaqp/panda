package com.panbu.app.device

import com.panbu.app.domain.model.BreathingPattern
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Contrato entre la app y el peluche Panbu.
 *
 * Las pantallas y ViewModels solo conocen esta interfaz. Hoy se usa [NoPanbuDevice]; en la Fase 5
 * se agrega una implementación Bluetooth (BlePanbuDevice) cambiando solo di/DeviceModule.kt.
 * Diseño completo en docs/07-bluetooth.md.
 */
interface PanbuDevice {
    val connectionState: StateFlow<DeviceConnectionState>

    /** Eventos que envía el peluche (abrazos, batería…). */
    val events: Flow<DeviceEvent>

    /** Pide al peluche que acompañe la respiración (vibración o luz). */
    suspend fun startBreathingGuide(pattern: BreathingPattern)

    suspend fun stopBreathingGuide()
}

sealed interface DeviceConnectionState {
    data object NotPaired : DeviceConnectionState
    data object Connecting : DeviceConnectionState
    data class Connected(val batteryPercent: Int?) : DeviceConnectionState
    data object Disconnected : DeviceConnectionState
}

sealed interface DeviceEvent {
    data class HugStarted(val at: Instant) : DeviceEvent
    data class HugEnded(val at: Instant) : DeviceEvent
    data class BatteryChanged(val percent: Int) : DeviceEvent
}
