package com.app.crowns.vently.navigation

import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.app.crowns.auth.ui.login.LoginScreen
import com.app.crowns.history.ui.HistoryScreen
import com.app.crowns.home.ui.HomeScreen
import com.app.crowns.home.ui.dashboard.DashboardScreen
import com.app.crowns.invoice.ui.create.CreateInvoiceScreen
import com.app.crowns.invoice.ui.preview.InvoicePreviewScreen
import com.app.crowns.onboarding.ui.pages.OnboardingScreen
import com.app.crowns.settings.ui.SettingsScreen
import com.app.crowns.settings.ui.business.EditBusinessProfileScreen
import com.app.crowns.stock.ui.StockScreen
import com.app.crowns.stock.ui.addproduct.AddProductScreen
import com.app.crowns.vently.navigation.transition.DefaultTransitions
import com.app.crowns.vently.splash.SplashScreen
import com.app.crowns.vently.subscription.SubscriptionGateViewModel
import com.app.crowns.vently.subscription.SubscriptionRequiredDialog
import java.util.UUID

@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    var selectedHomeTab by rememberSaveable { mutableIntStateOf(0) }
    val subscriptionGate: SubscriptionGateViewModel = hiltViewModel()
    val showSubscriptionDialog by subscriptionGate.showDialog.collectAsStateWithLifecycle()

    if (showSubscriptionDialog) {
        SubscriptionRequiredDialog(onDismiss = { subscriptionGate.dismissDialog() })
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        transitionSpec = { DefaultTransitions.enter togetherWith DefaultTransitions.exit },
        popTransitionSpec = { DefaultTransitions.popEnter togetherWith DefaultTransitions.popExit },
        entryProvider = { navKey ->
            when (val destination = navKey as AppDestination) {
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
                        },
                    )
                }

                is AppDestination.Auth -> NavEntry(destination) {
                    LoginScreen(
                        onNavigateToOnboarding = {
                            backStack.clear()
                            backStack.add(AppDestination.Onboarding)
                        },
                        onNavigateToHome = {
                            backStack.clear()
                            backStack.add(AppDestination.Home)
                        },
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
                        selectedTab = selectedHomeTab,
                        onTabSelected = { selectedHomeTab = it },
                        dashboardContent = {
                            DashboardScreen(
                                onNavigateToNewBoleta = {
                                    subscriptionGate.requireSubscription {
                                        backStack.add(AppDestination.CreateInvoice(isFactura = false, id = UUID.randomUUID().toString()))
                                    }
                                },
                                onNavigateToNewFactura = {
                                    subscriptionGate.requireSubscription {
                                        backStack.add(AppDestination.CreateInvoice(isFactura = true, id = UUID.randomUUID().toString()))
                                    }
                                },
                                onNavigateToStock = { selectedHomeTab = 1 },
                                onNavigateToHistory = { selectedHomeTab = 2 },
                                onNavigateToSettings = { selectedHomeTab = 3 },
                            )
                        },
                        stockContent = {
                            StockScreen(
                                onNavigateToAddProduct = {
                                    subscriptionGate.requireSubscription {
                                        backStack.add(AppDestination.AddProduct(id = UUID.randomUUID().toString()))
                                    }
                                },
                                onNavigateToEditProduct = { productId ->
                                    subscriptionGate.requireSubscription {
                                        backStack.add(AppDestination.AddProduct(id = UUID.randomUUID().toString(), productId = productId))
                                    }
                                },
                            )
                        },
                        historyContent = { HistoryScreen() },
                        settingsContent = {
                            SettingsScreen(
                                onNavigateToEditBusiness = {
                                    backStack.add(AppDestination.EditBusinessProfile)
                                },
                                onLogout = {
                                    backStack.clear()
                                    backStack.add(AppDestination.Auth)
                                },
                                onDeleteAccount = {
                                    backStack.clear()
                                    backStack.add(AppDestination.Auth)
                                },
                            )
                        },
                        onFabClick = { tabIndex ->
                            when (tabIndex) {
                                0 -> { /* Acción para Dashboard */ }
                                1 -> subscriptionGate.requireSubscription {
                                    backStack.add(AppDestination.AddProduct(id = UUID.randomUUID().toString()))
                                }
                                2 -> { /* Acción para Historial */ }
                                3 -> { /* Acción para Ajustes */ }
                            }
                        }
                    )
                }

                is AppDestination.AddProduct -> NavEntry(destination) {
                    AddProductScreen(
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                        productId = destination.productId,
                        viewModel = hiltViewModel(key = destination.id)
                    )
                }

                is AppDestination.CreateInvoice -> NavEntry(destination) {
                    CreateInvoiceScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onNavigateToPreview = {
                            backStack.add(AppDestination.InvoicePreview)
                        },
                        onNavigateToAddProduct = {
                            backStack.add(AppDestination.AddProduct(id = UUID.randomUUID().toString()))
                        },
                        isFactura = destination.isFactura,
                        viewModel = hiltViewModel(key = destination.id)
                    )
                }

                is AppDestination.InvoicePreview -> NavEntry(destination) {
                    InvoicePreviewScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onInvoiceEmitted = {
                            backStack.clear()
                            backStack.add(AppDestination.Home)
                        },
                        onNavigateToHistory = {
                            backStack.clear()
                            backStack.add(AppDestination.Home)
                            selectedHomeTab = 2
                        }
                    )
                }

                is AppDestination.EditBusinessProfile -> NavEntry(destination) {
                    EditBusinessProfileScreen(
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        }
    )
}
