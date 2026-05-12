package com.app.crowns.vently.navigation.controller

import androidx.navigation3.runtime.NavKey

sealed interface NavigationCommand {
    data class NavigateTo(val destination: NavKey): NavigationCommand
    data object NavigateBack: NavigationCommand
    data class Replace(val destination: NavKey) : NavigationCommand
    data class NavigateToAndClearStack(val destination: NavKey) : NavigationCommand
}