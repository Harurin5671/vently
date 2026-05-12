package com.app.crowns.onboarding.ui.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.onboarding.ui.OnboardingEvent
import com.app.crowns.onboarding.ui.OnboardingNavigationEvent
import com.app.crowns.onboarding.ui.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToHome -> onFinished()
            }
        }
    }

    AnimatedContent(
        targetState = state.currentPage,
        transitionSpec = {
            if (targetState > initialState) {
                // avanzando → entra por la derecha
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            } else {
                // retrocediendo → entra por la izquierda
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            }
        },
        label = "onboarding_page"
    ) { page ->
        when (page) {
            0 -> VincularRucScreen(
                currentStep = 1,
                totalSteps = state.totalPages,
                onBack = { viewModel.onEvent(OnboardingEvent.PreviousPage) },
                onContinue = { viewModel.onEvent(OnboardingEvent.NextPage) },
                ruc = state.ruc,
                onChangedRuc = { rucVal -> viewModel.onEvent(OnboardingEvent.OnChangedRuc(ruc = rucVal)) },
                isValid = state.isValid,
                isSearching = state.isSearching,
                searchError = state.searchError,
                businessName = state.companyInfo?.businessName ?: ""
            )
            1 -> OnboardingSuccessScreen(
                businessName = state.companyInfo?.businessName ?: "",
                ruc = state.companyInfo?.documentNumber ?: state.ruc,
                address = state.companyInfo?.address ?: "",
                currentStep = 2,
                totalSteps = state.totalPages,
                onNavigateToApp = { viewModel.onEvent(OnboardingEvent.Finish) },
                onAddPhoto = { /* TODO: Implement photo selection */ }
            )
        }
    }
}