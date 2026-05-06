package com.app.crowns.vently.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import com.app.crowns.vently.splash.SplashScreen
import com.app.crowns.auth.ui.login.LoginScreen
import androidx.navigation3.ui.NavDisplay

@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        entryProvider = { destination ->
            when (destination as AppDestination) {
                is AppDestination.Splash -> NavEntry(destination) {
                    SplashScreen(
                        onSplashFinished = {
                            backStack.clear()
                            backStack.add(AppDestination.Auth)
                        }
                    )
                }

                is AppDestination.Auth -> NavEntry(destination) {
                    LoginScreen()
                }

                is AppDestination.Onboarding -> NavEntry(destination) { }

                is AppDestination.Home -> NavEntry(destination) { }
            }
        }
    )
}