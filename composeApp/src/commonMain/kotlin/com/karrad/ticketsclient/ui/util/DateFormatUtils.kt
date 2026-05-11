package com.karrad.ticketsclient.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val MONTHS_GEN = listOf(
    "января","февраля","марта","апреля","мая","июня",
    "июля","августа","сентября","октября","ноября","декабря"
)

private fun hhmm(h: Int, m: Int) = "${h.toString().padStart(2,'0')}:${m.toString().padStart(2,'0')}"

/** "15 мая, 19:00" */
fun String.formatEventDate(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    "${ldt.dayOfMonth} ${MONTHS_GEN[ldt.monthNumber - 1]}, ${hhmm(ldt.hour, ldt.minute)}"
}.getOrNull()

/** "19:00" */
fun String.formatTimeOnly(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    hhmm(ldt.hour, ldt.minute)
}.getOrNull()

/** "15 января 2026, 19:00" */
fun String.formatEventDateFull(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    "${ldt.dayOfMonth} ${MONTHS_GEN[ldt.monthNumber - 1]} ${ldt.year}, ${hhmm(ldt.hour, ldt.minute)}"
}.getOrNull()

/**
 * Компактная строка для карточки с несколькими сеансами.
 * Один день  → "15 мая · 14:00 · 19:00"
 * Разные дни → "15 мая, 14:00 · 16 мая, 19:00" (до 3 элементов)
 */
fun formatSessionsCompact(times: List<String>): String {
    if (times.isEmpty()) return ""
    val tz = TimeZone.currentSystemDefault()
    val ldts = times.mapNotNull { runCatching { Instant.parse(it).toLocalDateTime(tz) }.getOrNull() }
    if (ldts.isEmpty()) return times.first()
    val firstDate = ldts.first().date
    return if (ldts.all { it.date == firstDate }) {
        val mon = "${firstDate.dayOfMonth} ${MONTHS_GEN[firstDate.monthNumber - 1]}"
        val tms = ldts.map { hhmm(it.hour, it.minute) }.joinToString(" · ")
        "$mon · $tms"
    } else {
        ldts.take(3).joinToString(" · ") {
            "${it.dayOfMonth} ${MONTHS_GEN[it.monthNumber - 1]}, ${hhmm(it.hour, it.minute)}"
        }
    }
}

/**
 * Метка чипа сеанса на SeatMap/TicketType.
 * Если все сеансы в один день — "19:00", иначе — "15 мая, 19:00".
 */
fun sessionChipLabel(thisTime: String, allTimes: List<String>): String {
    val tz = TimeZone.currentSystemDefault()
    val ldts = allTimes.mapNotNull { runCatching { Instant.parse(it).toLocalDateTime(tz) }.getOrNull() }
    val allSameDay = ldts.size > 1 && ldts.all { it.date == ldts.first().date }
    return if (allSameDay) thisTime.formatTimeOnly() ?: thisTime
    else thisTime.formatEventDate() ?: thisTime
}

/** "15.01.2026, 19:00" */
fun String.formatEventTime(): String = try {
    val parts = this.split("T")
    if (parts.size == 2) {
        val date = parts[0].split("-")
        val time = parts[1].removeSuffix("Z").take(5)
        "${date[2]}.${date[1]}.${date[0]}, $time"
    } else this
} catch (_: Exception) { this }
