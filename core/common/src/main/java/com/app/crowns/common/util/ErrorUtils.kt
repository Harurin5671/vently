package com.app.crowns.common.util

/**
 * Parses a [Throwable] message to extract a user-friendly error string.
 * This is particularly useful for Supabase/Postgrest exceptions which often
 * include technical details (headers, URL, etc.) after the first line.
 */
fun Throwable.toUserFriendlyMessage(
    defaultMessage: String = "Ocurrió un error inesperado"
): String {
    // Errores de red por tipo, no por texto
    if (this is java.net.UnknownHostException ||
        this is java.net.SocketTimeoutException ||
        this is java.io.IOException && message?.contains("Failed to connect") == true) {
        return "No se pudo conectar al servidor. Verifica tu conexión a internet."
    }

    val rawMessage = this.message ?: return defaultMessage

    // Unique constraint violations de Postgres (código 23505) — detectados por nombre de constraint
    if (rawMessage.contains("23505") || rawMessage.contains("duplicate key")) {
        if (rawMessage.contains("categories_company_name_unique")) return "Ya existe una categoría con ese nombre en tu empresa."
        return "Este registro ya existe."
    }

    return rawMessage.lines().firstOrNull()?.trim() ?: defaultMessage
}

