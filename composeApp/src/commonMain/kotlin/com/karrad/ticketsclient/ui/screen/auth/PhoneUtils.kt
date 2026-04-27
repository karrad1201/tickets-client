package com.karrad.ticketsclient.ui.screen.auth

/** Приводит номер к виду +7XXXXXXXXXX */
fun normalizePhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8")) ->
            "+7${digits.drop(1)}"
        digits.length == 10 -> "+7$digits"
        else -> raw.trim()
    }
}
