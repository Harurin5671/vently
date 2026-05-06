package com.app.crowns.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.auth.di.SupabaseEntryPoint
import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.ui.AuthEvent
import com.app.crowns.auth.ui.AuthUiState
import com.app.crowns.auth.ui.AuthViewModel
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import dagger.hilt.android.EntryPointAccessors
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import io.github.jan.supabase.compose.auth.composeAuth

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
        onLoginClick = { googleSignInState.startFlow() },
        onLogoutClick = { viewModel.onEvent(AuthEvent.Logout) }
    )
}

@Composable
fun LoginContent(
    state: AuthUiState,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit = {}
) {
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

            if (state.isAuthenticated) {
                Text(
                    text = "¡Estás autenticado!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Usuario: ${state.user?.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onLogoutClick) {
                    Text("Cerrar sesión")
                }
            } else {
                // Título
                Text(
                    text = "Bienvenido a Vently",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Inicia sesión para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Botón o loader
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    GoogleSignInButton(
                        onClick = onLoginClick
                    )
                }
            }

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

private fun AuthError.toMessage(): String = when (this) {
    is AuthError.NetworkError -> "Sin conexión a internet"
    is AuthError.InvalidCredentials -> "Credenciales inválidas"
    is AuthError.UserNotFound -> "Usuario no encontrado"
    is AuthError.EmailAlreadyInUse -> "El correo ya está en uso"
    is AuthError.Unknown -> message
}