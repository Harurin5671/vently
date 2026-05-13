package com.app.crowns.vently.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.app.crowns.vently.splash.SplashScreen
import com.app.crowns.auth.ui.login.LoginScreen
import androidx.navigation3.ui.NavDisplay
import com.app.crowns.home.ui.HomeScreen
import com.app.crowns.onboarding.ui.pages.OnboardingScreen
import com.app.crowns.vently.navigation.transition.DefaultTransitions

@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        transitionSpec = { DefaultTransitions.enter togetherWith DefaultTransitions.exit },
        popTransitionSpec = { DefaultTransitions.popEnter togetherWith DefaultTransitions.popExit },
        entryProvider = { destination ->
            when (destination as AppDestination) {
                is AppDestination.Splash -> NavEntry(destination) {
                    SplashScreen(
                        onNavigateToAuth = {
                            backStack.clear()
                            backStack.add(AppDestination.Auth)
                        },
                        onNavigateToOnboarding = {
                            backStack.clear()
                            backStack.add(AppDestination.Onboarding)
                        },
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(AppDestination.Home)
                        }
                    )
                }

                is AppDestination.Auth -> NavEntry(destination) {
                    LoginScreen(
                        onNavigateToOnboarding = {
                            backStack.clear()
                            backStack.add(AppDestination.Onboarding)
                        }
                    )
                }

                is AppDestination.Onboarding -> NavEntry(destination) {
                    OnboardingScreen(
                        onFinished = {
                            backStack.clear()
                            backStack.add(AppDestination.Home)
                        }
                    )
                }

                is AppDestination.Home -> NavEntry(destination) {
                    HomeScreen(
                        dashboardContent = { Text("Dashboard") },
                        stockContent = { Text("Stock") },
                        historyContent = { Text("Historial") },
                        settingsContent = { Text("Ajustes") },
                        onFabClick = {
                            // aquí después abres el BottomSheet de boleta/factura
                        }
                    )
                }
            }
        }
    )
}