package com.app.crowns.auth.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.auth.di.SupabaseEntryPoint
import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.auth.ui.AuthEvent
import com.app.crowns.auth.ui.AuthNavigationEvent
import com.app.crowns.auth.ui.AuthUiState
import com.app.crowns.auth.ui.AuthViewModel
import com.app.crowns.auth.ui.toMessage
import com.app.crowns.auth.ui.login.components.TextRegister
import com.app.crowns.auth.ui.login.components.TextTermsAndConditions
import com.app.crowns.designsystem.R
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.ui.components.VentlyLoadingScreen
import dagger.hilt.android.EntryPointAccessors
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun LoginScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToOnboarding -> onNavigateToOnboarding()
                is AuthNavigationEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    val context = LocalContext.current
    val supabaseClient = remember {
        EntryPointAccessors
            .fromApplication<SupabaseEntryPoint>(context.applicationContext)
            .supabaseClient()
    }

    val googleSignInState = supabaseClient.composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            viewModel.onEvent(AuthEvent.HandleGoogleResult(result))
        }
    )

    LoginContent(
        state = state,
        onLoginClick = {
            viewModel.onEvent(AuthEvent.SignInWithGoogle)
            googleSignInState.startFlow()
        }
    )
}

@Composable
fun LoginContent(
    state: AuthUiState,
    onLoginClick: () -> Unit,
) {
    if (state.isLoading || state.isAuthenticated) {
        VentlyLoadingScreen(
            message = if (state.isAuthenticated) "Preparando tu espacio..." else "Iniciando sesión..."
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.vently_logo_icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Vently",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            lineHeight = 28.sp
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
                // Título
                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ingresa para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                GoogleSignInButton(
                    onClick = onLoginClick
                )

                Spacer(Modifier.height(16.dp))
                TextRegister {}

                Spacer(Modifier.height(24.dp))
                TextTermsAndConditions()

                // Error
                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error.toMessage(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun LoginScreenPreview() {
    VentlyTheme {
        Surface {
            LoginContent(
                state = AuthUiState(),
                onLoginClick = {}
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoginScreenLoadingPreview() {
    VentlyTheme {
        Surface {
            LoginContent(
                state = AuthUiState(isLoading = true),
                onLoginClick = {}
            )
        }
    }
}

@ThemePreviews
@Composable
private fun LoginScreenErrorPreview() {
    VentlyTheme {
        Surface {
            LoginContent(
                state = AuthUiState(error = AuthError.NetworkError),
                onLoginClick = {}
            )
        }
    }
}
