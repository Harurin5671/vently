package com.app.crowns.stock.ui.addproduct

import android.net.Uri
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.IgvType

sealed class AddProductEvent {
    data class OnImageSelected(val uri: Uri) : AddProductEvent()
    data class OnNameChanged(val value: String) : AddProductEvent()
    data class OnDescriptionChanged(val value: String) : AddProductEvent()
    data class OnSkuChanged(val value: String) : AddProductEvent()
    data class OnCategorySelected(val category: Category?) : AddProductEvent()
    data class OnSalePriceChanged(val value: String) : AddProductEvent()
    data class OnCostPriceChanged(val value: String) : AddProductEvent()
    data class OnIgvTypeSelected(val igvType: IgvType) : AddProductEvent()
    data class OnUnitOfMeasureSelected(val unit: String) : AddProductEvent()
    data class OnTrackStockChanged(val value: Boolean) : AddProductEvent()
    data class OnCurrentStockChanged(val value: Int) : AddProductEvent()
    data class OnMinStockAlertChanged(val value: String) : AddProductEvent()
    data class OnNewCategoryNameChanged(val value: String) : AddProductEvent()
    data object OnShowAddCategoryDialog : AddProductEvent()
    data object OnDismissAddCategoryDialog : AddProductEvent()
    data object OnSaveCategory : AddProductEvent()
    data object OnSaveProduct : AddProductEvent()
}

sealed class AddProductNavigationEvent {
    data object NavigateBack : AddProductNavigationEvent()
}