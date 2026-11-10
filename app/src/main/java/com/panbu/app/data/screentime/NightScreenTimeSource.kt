package com.panbu.app.data.screentime

import java.time.Instant

/**
 * Tiempo de uso del celular durante la noche (Fase 2).
 *
 * La implementación usará UsageStatsManager y necesita que la persona active
 * "Acceso a datos de uso" en Ajustes del sistema (permiso PACKAGE_USAGE_STATS, opcional).
 * Reglas de cálculo en docs/05-modelo-de-datos.md.
 */
interface NightScreenTimeSource {
    /** true si la persona concedió el acceso a datos de uso. */
    fun hasPermission(): Boolean

    /** Minutos con la pantalla encendida en [from, to), o null si no hay permiso. */
    suspend fun minutesBetween(from: Instant, to: Instant): Int?
}
