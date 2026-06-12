package com.app.crowns.vently.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination : NavKey {
    @Serializable
    data object Splash : AppDestination

    @Serializable
    data object Auth : AppDestination

    @Serializable
    data object Onboarding : AppDestination

    @Serializable
    data class Home(val initialTab: Int = 0) : AppDestination

    @Serializable
    data class AddProduct(val id: String, val productId: String? = null) : AppDestination

    @Serializable
    data class CreateInvoice(val isFactura: Boolean, val id: String) : AppDestination

    @Serializable
    data class InvoicePreview(val id: String) : AppDestination

    @Serializable
    data object EditBusinessProfile : AppDestination
}

