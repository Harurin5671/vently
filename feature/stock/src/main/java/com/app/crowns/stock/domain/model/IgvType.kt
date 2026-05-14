package com.app.crowns.stock.domain.model

enum class IgvType(val label: String, val code: String) {
    GRAVADO("Gravado 18%", "10"),
    EXONERADO("Exonerado", "20"),
    INAFECTO("Inafecto", "30"),
}