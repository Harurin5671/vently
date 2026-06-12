package com.app.crowns.common.util

/**
 * Parses a [Throwable] message to extract a user-friendly error string.
 * Handles Supabase edge function structured errors ({ code, error, detail }),
 * Postgrest exceptions, network errors, and Postgres constraint violations.
 */
fun Throwable.toUserFriendlyMessage(
    defaultMessage: String = "Ocurrió un error inesperado"
): String {
    // Errores de red por tipo
    if (this is java.net.UnknownHostException ||
        this is java.net.SocketTimeoutException ||
        this is java.io.IOException && message?.contains("Failed to connect") == true) {
        return "No se pudo conectar al servidor. Verifica tu conexión a internet."
    }

    val rawMessage = this.message ?: return defaultMessage

    // Errores estructurados de edge functions: { "code": "...", "error": "...", "detail"?: "..." }
    // El SDK de Supabase (functions-kt) incluye el body JSON en el mensaje de la excepción.
    val jsonBody = extractJsonObject(rawMessage)
    if (jsonBody != null) {
        try {
            val json = org.json.JSONObject(jsonBody)
            val code = json.optString("code", "")
            val errorMsg = json.optString("error", "")
            val detail = json.optString("detail", "")
            val userMsg = mapEdgeFunctionCode(code, errorMsg)
            if (userMsg.isNotBlank()) {
                return if (detail.isNotBlank()) "$userMsg\n$detail" else userMsg
            }
        } catch (_: Exception) {}
    }

    // Unique constraint violations de Postgres (código 23505)
    if (rawMessage.contains("23505") || rawMessage.contains("duplicate key")) {
        if (rawMessage.contains("categories_company_name_unique")) return "Ya existe una categoría con ese nombre en tu empresa."
        return "Este registro ya existe."
    }

    return rawMessage.lines().firstOrNull()?.trim() ?: defaultMessage
}

/** Extrae el primer objeto JSON `{...}` encontrado en un string. */
private fun extractJsonObject(text: String): String? {
    val start = text.indexOf('{')
    if (start < 0) return null
    var depth = 0
    for (i in start until text.length) {
        when (text[i]) {
            '{' -> depth++
            '}' -> { depth--; if (depth == 0) return text.substring(start, i + 1) }
        }
    }
    return null
}

/** Mapea el campo `code` de las edge functions a un mensaje en español para el usuario. */
private fun mapEdgeFunctionCode(code: String, fallback: String): String = when (code) {
    // Certificado
    "INVALID_CERTIFICATE_FORMAT"  -> "El certificado no es válido. Sube un archivo .p12 o .pfx"
    "CERTIFICATE_NOT_CONFIGURED"  -> "No tienes un certificado digital configurado. Agrégalo en Ajustes."
    // Comprobante
    "INVOICE_NOT_FOUND"           -> "Comprobante no encontrado."
    "INVOICE_NOT_SIGNED"          -> "El comprobante aún no ha sido firmado."
    "INVALID_INVOICE_STATUS"      -> "El comprobante no se puede procesar en su estado actual."
    "CANCELLATION_WINDOW_EXPIRED" -> "El plazo para anular este comprobante ha vencido."
    "XML_NOT_GENERATED"           -> "El XML del comprobante no ha sido generado todavía."
    // XML / campos
    "MISSING_FIELDS"              -> "Faltan datos requeridos para generar el comprobante."
    "INVALID_DOC_TYPE_FOR_SERIES" -> "La serie y el tipo de documento del cliente no son compatibles."
    "INVALID_PAYMENT_METHOD"      -> "Método de pago no reconocido."
    "INVALID_CURRENCY"            -> "Moneda no válida."
    "INVALID_DOC_TYPE"            -> "Tipo de documento del cliente no válido."
    "DOC_NUMBER_TOO_LONG"         -> "El número de documento del cliente es demasiado largo."
    "DOC_TYPE_NOT_FOR_COMPANY"    -> "Ese tipo de documento no es válido para Facturas. Se requiere RUC."
    "COMPANY_NOT_FOUND"           -> "Empresa no encontrada. Verifica tu sesión."
    "SERIES_NOT_FOUND"            -> "No se encontró una serie activa para este tipo de comprobante."
    "PRODUCTS_NOT_FOUND"          -> "Uno o más productos no fueron encontrados en tu empresa."
    // SUNAT
    "COMPANY_RUC_MISSING"         -> "Tu empresa no tiene RUC configurado. Complétalo en Ajustes."
    "SOL_CREDENTIALS_MISSING"     -> "Las credenciales SOL no están configuradas. Agrégalas en Ajustes."
    "SIGN_API_FAILED"             -> "Ocurrió un error al firmar el comprobante. Intenta de nuevo."
    // Cuenta
    "MISSING_AUTH"                -> "Sesión expirada. Vuelve a iniciar sesión."
    "UNAUTHORIZED"                -> "No tienes permiso para realizar esta acción."
    "DELETE_ACCOUNT_FAILED"       -> "No se pudo eliminar la cuenta. Intenta de nuevo."
    // Genérico — usar el campo `error` de la edge function (ya en español)
    else                          -> fallback
}
