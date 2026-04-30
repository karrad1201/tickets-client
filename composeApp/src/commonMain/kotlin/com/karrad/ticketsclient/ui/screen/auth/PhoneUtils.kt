package com.karrad.ticketsclient.ui.screen.auth

/**
 * Приводит номер телефона к формату +7XXXXXXXXXX.
 * Принимает любые форматы: 89161234567, 8 (916) 123-45-67,
 * +7-916-123-45-67, 7 916 123 45 67, 9161234567 и т.д.
 */
fun normalizePhone(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.length == 11 && (digits.startsWith("7") || digits.startsWith("8")) ->
            "+7${digits.drop(1)}"
        digits.length == 10 -> "+7$digits"
        else -> raw.trim()
    }
}
