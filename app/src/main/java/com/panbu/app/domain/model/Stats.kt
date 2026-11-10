package com.panbu.app.domain.model

import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

/**
 * Periodos de la pantalla Estadísticas (docs/02-producto.md §2).
 * Se guarda en el estado de la pantalla por su `name`.
 */
enum class StatsPeriod { WEEK, MONTH, TOTAL }

/** Días de calendario `[start, endExclusive)`. */
data class DateSpan(val start: LocalDate, val endExclusive: LocalDate) {
    init {
        require(!endExclusive.isBefore(start)) { "El fin no puede ser anterior al inicio" }
    }

    val days: Long get() = ChronoUnit.DAYS.between(start, endExclusive)

    operator fun contains(date: LocalDate): Boolean = !date.isBefore(start) && date.isBefore(endExclusive)

    /** El último día incluido. */
    val lastDay: LocalDate get() = endExclusive.minusDays(1)
}

/** Una barra del gráfico: un día (Semana), una semana (Mes) o un mes (Total). */
data class CalmBar(val span: DateSpan, val calm: Duration)

/** Cuántas veces se eligió un estado de ánimo en el periodo y su porcentaje (ya ajustado para sumar 100). */
data class MoodShare(val mood: Mood, val count: Int, val percent: Int)

/** La dona de "¿Cómo te sientes?". [shares] va de mayor a menor; el primero es el más frecuente. */
data class MoodDistribution(val shares: List<MoodShare>) {
    init {
        require(shares.isNotEmpty()) { "Sin registros no hay distribución: se usa null" }
    }

    val mostFrequent: MoodShare get() = shares.first()
    val total: Int get() = shares.sumOf { it.count }
}

/** Todo lo que muestra Estadísticas para un periodo. */
data class PeriodStats(
    val period: StatsPeriod,
    /** El día en que se calculó (para resaltar la barra de hoy). */
    val today: LocalDate,
    /** El periodo que se muestra: semana de lunes a domingo, mes calendario o desde el primer registro. */
    val span: DateSpan,
    /** Suma de las sesiones de calma del periodo (las de menos de 30 s no cuentan). */
    val calmTotal: Duration,
    /**
     * Cambio en % frente al mismo tramo del periodo anterior. Es null en Total, y cuando el periodo actual
     * o el anterior están en 0 (no hay con qué comparar, y un "↓ 100 %" solo desanima).
     */
    val calmChangePercent: Int?,
    val bars: List<CalmBar>,
    /** null si no hay registros de ánimo en el periodo. */
    val moods: MoodDistribution?,
) {
    val hasCalm: Boolean get() = !calmTotal.isZero
    val isEmpty: Boolean get() = !hasCalm && moods == null
}

/**
 * Reglas de cálculo de Estadísticas (docs/05-modelo-de-datos.md, "Reglas de cálculo").
 * Kotlin puro: recibe "hoy", la zona horaria y los registros, y no lee el reloj ni la base de datos.
 */
object StatsCalculator {

    /** En Total se dibujan como máximo los últimos 12 meses (la suma sí incluye todo). */
    const val MAX_TOTAL_BARS = 12

    /**
     * Días que hay que leer de la base para calcular [period]: el periodo actual y el anterior.
     * En Total es null: se lee todo hasta hoy.
     */
    fun queryRange(period: StatsPeriod, today: LocalDate): DateSpan? = when (period) {
        StatsPeriod.WEEK -> {
            val start = weekStart(today)
            DateSpan(start.minusWeeks(1), start.plusWeeks(1))
        }
        StatsPeriod.MONTH -> {
            val start = today.withDayOfMonth(1)
            DateSpan(start.minusMonths(1), start.plusMonths(1))
        }
        StatsPeriod.TOTAL -> null
    }

    fun build(
        period: StatsPeriod,
        today: LocalDate,
        zone: ZoneId,
        sessions: List<CalmSession>,
        moods: List<MoodEntry>,
    ): PeriodStats {
        // Cada registro pertenece al día (local) en que empieza.
        val counted = sessions.counted().map { DatedSession(it.startedAt.localDate(zone), it.duration) }
        val datedMoods = moods.map { DatedMood(it.createdAt.localDate(zone), it.createdAt, it.mood) }

        val span = currentSpan(period, today, counted, datedMoods)
        val previous = previousSpan(period, today)

        val calmTotal = counted.sumIn(span)
        val calmChange = previous?.let { changePercent(current = counted.sumIn(elapsed(span, today)), previous = counted.sumIn(it)) }

        return PeriodStats(
            period = period,
            today = today,
            span = span,
            calmTotal = calmTotal,
            calmChangePercent = calmChange,
            bars = barSpans(period, span, today).map { CalmBar(it, counted.sumIn(it)) },
            moods = moodDistribution(datedMoods.filter { it.date in span }),
        )
    }

    /**
     * `(actual − anterior) / anterior × 100`, redondeado al entero más cercano.
     * null si alguno de los dos es 0.
     */
    fun changePercent(current: Duration, previous: Duration): Int? {
        if (current.isZero || previous.isZero) return null
        val currentSeconds = current.seconds.toDouble()
        val previousSeconds = previous.seconds.toDouble()
        return ((currentSeconds - previousSeconds) * 100.0 / previousSeconds).roundToInt()
    }

    /**
     * Porcentaje de cada estado de ánimo, ajustado con el método del mayor residuo para que sumen 100.
     * Orden: más registros primero; si empatan, el elegido más recientemente.
     */
    internal fun moodDistribution(entries: List<DatedMood>): MoodDistribution? {
        if (entries.isEmpty()) return null
        val total = entries.size

        data class Tally(val mood: Mood, val count: Int, val lastAt: Instant) {
            val floor get() = count * 100 / total
            val remainder get() = count * 100 % total
        }

        val tallies = entries.groupBy { it.mood }.map { (mood, list) ->
            Tally(mood, list.size, list.maxOf { it.at })
        }
        val byPreference = compareByDescending<Tally> { it.count }.thenByDescending { it.lastAt }

        // Los puntos que faltan para llegar a 100 van a los residuos más grandes.
        var missing = 100 - tallies.sumOf { it.floor }
        val bonus = tallies
            .sortedWith(compareByDescending<Tally> { it.remainder }.then(byPreference))
            .associate { tally -> tally.mood to if (missing-- > 0) 1 else 0 }

        val shares = tallies
            .sortedWith(byPreference)
            .map { MoodShare(it.mood, it.count, it.floor + bonus.getValue(it.mood)) }
        return MoodDistribution(shares)
    }

    // --- Periodos ---

    private fun weekStart(date: LocalDate): LocalDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    private fun currentSpan(
        period: StatsPeriod,
        today: LocalDate,
        sessions: List<DatedSession>,
        moods: List<DatedMood>,
    ): DateSpan = when (period) {
        StatsPeriod.WEEK -> weekStart(today).let { DateSpan(it, it.plusWeeks(1)) }
        StatsPeriod.MONTH -> today.withDayOfMonth(1).let { DateSpan(it, it.plusMonths(1)) }
        StatsPeriod.TOTAL -> {
            val first = (sessions.map { it.date } + moods.map { it.date }).minOrNull() ?: today
            DateSpan(minOf(first, today), today.plusDays(1))
        }
    }

    /** Lo que va del periodo actual: desde su inicio hasta hoy, incluido. */
    private fun elapsed(span: DateSpan, today: LocalDate): DateSpan =
        DateSpan(span.start, minOf(span.endExclusive, today.plusDays(1)).coerceAtLeastDate(span.start))

    /**
     * El mismo tramo del periodo anterior, para que la comparación sea justa: un miércoles se compara
     * de lunes a miércoles contra de lunes a miércoles de la semana pasada (no contra la semana entera).
     */
    private fun previousSpan(period: StatsPeriod, today: LocalDate): DateSpan? = when (period) {
        StatsPeriod.WEEK -> {
            val start = weekStart(today)
            val daysElapsed = ChronoUnit.DAYS.between(start, today) + 1
            val previousStart = start.minusWeeks(1)
            DateSpan(previousStart, previousStart.plusDays(daysElapsed))
        }
        StatsPeriod.MONTH -> {
            val previousStart = today.withDayOfMonth(1).minusMonths(1)
            val days = minOf(today.dayOfMonth, previousStart.lengthOfMonth())
            DateSpan(previousStart, previousStart.plusDays(days.toLong()))
        }
        StatsPeriod.TOTAL -> null
    }

    /** Semana: un día por barra. Mes: semanas de lunes a domingo, recortadas al mes. Total: un mes por barra. */
    private fun barSpans(period: StatsPeriod, span: DateSpan, today: LocalDate): List<DateSpan> = when (period) {
        StatsPeriod.WEEK -> (0 until span.days).map { offset ->
            val day = span.start.plusDays(offset)
            DateSpan(day, day.plusDays(1))
        }
        StatsPeriod.MONTH -> buildList {
            var start = span.start
            while (start.isBefore(span.endExclusive)) {
                val end = minOf(start.with(TemporalAdjusters.next(DayOfWeek.MONDAY)), span.endExclusive)
                add(DateSpan(start, end))
                start = end
            }
        }
        StatsPeriod.TOTAL -> {
            val lastMonth = today.withDayOfMonth(1)
            val firstMonth = maxOf(span.start.withDayOfMonth(1), lastMonth.minusMonths(MAX_TOTAL_BARS - 1L))
            generateSequence(firstMonth) { it.plusMonths(1) }
                .takeWhile { !it.isAfter(lastMonth) }
                .map { DateSpan(it, it.plusMonths(1)) }
                .toList()
        }
    }

    // --- Ayudantes ---

    internal data class DatedSession(val date: LocalDate, val duration: Duration)

    internal data class DatedMood(val date: LocalDate, val at: Instant, val mood: Mood)

    private fun List<DatedSession>.sumIn(span: DateSpan): Duration =
        filter { it.date in span }.fold(Duration.ZERO) { total, session -> total.plus(session.duration) }

    private fun Instant.localDate(zone: ZoneId): LocalDate = atZone(zone).toLocalDate()

    private fun LocalDate.coerceAtLeastDate(minimum: LocalDate): LocalDate = if (isBefore(minimum)) minimum else this
}
