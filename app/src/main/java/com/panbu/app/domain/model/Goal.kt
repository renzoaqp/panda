package com.panbu.app.domain.model

/** Tipos de meta. Se guardan por `name`: no renombrar valores publicados. */
enum class GoalType {
    /** "Menos tiempo de celular en la noche": es un LÍMITE, menos es mejor. */
    NIGHT_SCREEN_LIMIT,
    CALM_MOMENTS,
    MINDFULNESS,
    SLEEP,
    CUSTOM,
}

enum class GoalUnit { MINUTES, TIMES }

enum class GoalPeriod { DAILY, WEEKLY }

data class Goal(
    val id: Long = 0,
    val type: GoalType,
    val title: String,
    val target: Int,
    val unit: GoalUnit,
    val period: GoalPeriod,
    val isActive: Boolean = true,
)

/**
 * Avance de una meta en su periodo. Reglas en docs/05-modelo-de-datos.md.
 *
 * @param current lo logrado; en metas de límite, lo usado.
 */
data class GoalProgress(val goal: Goal, val current: Int) {

    val isLimit: Boolean get() = goal.type == GoalType.NIGHT_SCREEN_LIMIT

    /** De 0 a 1, para dibujar la barra. */
    val fraction: Float
        get() = if (goal.target <= 0) 0f else (current.toFloat() / goal.target).coerceIn(0f, 1f)

    /**
     * Porcentaje entero redondeado hacia abajo, de 0 a 100 (7 h 10 min de 8 h = 89 %, como en el mockup).
     * Se calcula con enteros para evitar errores de redondeo de punto flotante.
     */
    val percent: Int
        get() = if (goal.target <= 0) 0 else (current.toLong() * 100 / goal.target).toInt().coerceIn(0, 100)

    /** Metas normales: llegar al objetivo. Metas de límite: no pasarse. */
    val isAchieved: Boolean
        get() = if (isLimit) current <= goal.target else goal.target > 0 && current >= goal.target
}
