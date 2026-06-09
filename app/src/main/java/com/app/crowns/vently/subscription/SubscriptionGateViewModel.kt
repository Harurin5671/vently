package com.app.crowns.vently.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.common.Result
import com.app.crowns.domain.auth.usecase.CheckSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionGateViewModel @Inject constructor(
    private val checkSubscription: CheckSubscriptionUseCase,
) : ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    fun requireSubscription(onAllowed: () -> Unit) {
        viewModelScope.launch {
            when (checkSubscription()) {
                is Result.Success -> onAllowed()
                is Result.Failure -> _showDialog.value = true
            }
        }
    }

    fun dismissDialog() {
        _showDialog.value = false
    }
}
