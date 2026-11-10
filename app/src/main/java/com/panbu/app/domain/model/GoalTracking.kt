package com.panbu.app.domain.model

/** Una meta con su avance del día. [progress] es null si la meta todavía no tiene de dónde sacar datos. */
data class GoalItem(val goal: Goal, val progress: GoalProgress?)

/**
 * Avance de una meta diaria a partir de las sesiones de calma de hoy (docs/05-modelo-de-datos.md).
 * Devuelve null para las metas cuyos datos todavía no existen en la app (sueño, tiempo de pantalla,
 * personalizadas): así nunca se muestra un número inventado.
 */
fun Goal.progressFrom(todaySessions: List<CalmSession>): GoalProgress? {
    val counted = todaySessions.counted()
    val current = when (type) {
        GoalType.CALM_MOMENTS -> counted.size
        GoalType.MINDFULNESS -> counted.count {
            it.type == CalmActivityType.BREATHING || it.type == CalmActivityType.MINDFULNESS
        }
        GoalType.NIGHT_SCREEN_LIMIT, GoalType.SLEEP, GoalType.CUSTOM -> return null
    }
    return GoalProgress(this, current)
}

/** Metas que trae la app la primera vez (docs/02-producto.md, "Metas iniciales"). */
object DefaultGoals {
    val all: List<Goal> = listOf(
        Goal(type = GoalType.CALM_MOMENTS, title = "", target = 3, unit = GoalUnit.TIMES, period = GoalPeriod.DAILY),
        Goal(type = GoalType.MINDFULNESS, title = "", target = 2, unit = GoalUnit.TIMES, period = GoalPeriod.DAILY),
    )
}
