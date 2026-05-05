package com.karrad.ticketsclient.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val MONTHS_SHORT = listOf("янв","фев","мар","апр","май","июн","июл","авг","сен","окт","ноя","дек")
private val MONTHS_FULL  = listOf("января","февраля","марта","апреля","мая","июня",
    "июля","августа","сентября","октября","ноября","декабря")

/** "15 янв в 19:00" */
fun String.formatEventDate(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    "${ldt.dayOfMonth} ${MONTHS_SHORT[ldt.monthNumber - 1]} в " +
        "${ldt.hour.toString().padStart(2,'0')}:${ldt.minute.toString().padStart(2,'0')}"
}.getOrNull()

/** "15 января 2026, 19:00" */
fun String.formatEventDateFull(): String? = runCatching {
    val ldt = Instant.parse(this).toLocalDateTime(TimeZone.currentSystemDefault())
    "${ldt.dayOfMonth} ${MONTHS_FULL[ldt.monthNumber - 1]} ${ldt.year}, " +
        "${ldt.hour.toString().padStart(2,'0')}:${ldt.minute.toString().padStart(2,'0')}"
}.getOrNull()

/** "15.01.2026, 19:00" */
fun String.formatEventTime(): String = try {
    val parts = this.split("T")
    if (parts.size == 2) {
        val date = parts[0].split("-")
        val time = parts[1].removeSuffix("Z").take(5)
        "${date[2]}.${date[1]}.${date[0]}, $time"
    } else this
} catch (_: Exception) { this }
