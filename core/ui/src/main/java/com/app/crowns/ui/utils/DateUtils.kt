package com.app.crowns.ui.utils

fun formatDateTime(dateTime: String): String {
    return try {
        val date = dateTime.take(10)
        val time = dateTime.substring(11, 16)
        val parts = date.split("-")
        "${parts[2]} ${monthName(parts[1].toInt())} ${parts[0]}, $time"
    } catch (_: Exception) {
        dateTime
    }
}

fun formatDate(dateTime: String): String {
    return try {
        val date = dateTime.take(10)
        val parts = date.split("-")
        "${parts[2]} ${monthName(parts[1].toInt())} ${parts[0]}"
    } catch (_: Exception) {
        dateTime
    }
}

private fun monthName(month: Int): String = when (month) {
    1 -> "Ene"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Abr"
    5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Ago"
    9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dic"
    else -> ""
}
