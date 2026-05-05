package com.karrad.ticketsclient.ui.util

/** Возвращает читаемое сообщение об ошибке из исключения или запасной текст. */
fun Exception.toAuthErrorMessage(fallback: String): String = message?.takeIf { it.isNotBlank() } ?: fallback
