package com.app.crowns.home.ui.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.TextMuted
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun GreetingSection(userName: String) {
    val now = LocalDateTime.now()
    val greeting = when (now.hour) {
        in 0..11 -> "Buenos días"
        in 12..17 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    val dayFormat = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "PE"))

    Column {
        Text(
            text = "$greeting, $userName",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
        )
        Text(
            text = now.format(dayFormat).replaceFirstChar { it.uppercase() },
            fontSize = 14.sp,
            color = TextMuted,
        )
    }
}