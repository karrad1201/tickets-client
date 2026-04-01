package com.karrad.ticketsclient.ui.util

/** Форматирует целое число как цену с неразрывным пробелом-разделителем тысяч. */
fun Int.formatPrice(): String {
    val s = this.toString()
    return if (s.length <= 3) s else s.dropLast(3) + "\u00A0" + s.takeLast(3)
}
