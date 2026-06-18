package com.app.crowns.onboarding.ui.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.common.util.ImageUtils
import com.app.crowns.onboarding.ui.OnboardingEvent
import com.app.crowns.onboarding.ui.OnboardingNavigationEvent
import com.app.crowns.onboarding.ui.OnboardingViewModel

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToHome -> onFinished()
                is OnboardingNavigationEvent.NavigateToLogin -> onNavigateToLogin()
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
            0 -> OnboardingFirstPage(
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
                logoUri = state.logoUri,
                isFinishing = state.isFinishing,
                error = state.searchError,
                logoUploadFailed = state.logoUploadFailed,
                onNavigateToApp = {
                    val logoBytes = state.logoUri?.let { uri ->
                        ImageUtils.compressImage(context, uri)
                    }
                    viewModel.onEvent(OnboardingEvent.Finish(logoBytes = logoBytes))
                },
                onSkipLogo = { viewModel.onEvent(OnboardingEvent.FinishWithoutLogo) },
                onLogoSelected = { uri -> viewModel.onEvent(OnboardingEvent.OnLogoSelected(uri)) }
            )
        }
    }
}