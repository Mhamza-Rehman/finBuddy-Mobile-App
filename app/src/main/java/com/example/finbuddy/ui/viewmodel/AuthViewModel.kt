package com.example.finbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.network.SupabaseClient
import com.example.finbuddy.data.repository.AccountRepository
import com.example.finbuddy.data.repository.AccountRepositoryImpl
import com.example.finbuddy.data.repository.AuthRepository
import com.example.finbuddy.data.repository.AuthRepositoryImpl
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl(),
    private val accountRepository: AccountRepository = AccountRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signUp(email: String, password: String, phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                repository.signUp(email, password, phone)
                val userId = getCurrentUserId()
                if (userId == null) {
                    _uiState.value = AuthUiState(errorMessage = "Could not resolve user session after sign up")
                } else {
                    verifyAndSeedUserAssets(userId) {
                        _uiState.value = AuthUiState(isLoggedIn = true)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Sign up failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                repository.signIn(email, password)
                val userId = getCurrentUserId()
                if (userId == null) {
                    _uiState.value = AuthUiState(errorMessage = "Could not resolve user session after login")
                } else {
                    verifyAndSeedUserAssets(userId) {
                        _uiState.value = AuthUiState(isLoggedIn = true)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Login failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                repository.signOut()
                _uiState.value = AuthUiState(isLoggedIn = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun verifyAndSeedUserAssets(userId: String, onVerificationComplete: () -> Unit) {
        viewModelScope.launch {
            accountRepository.getPrimaryAccountId(userId)
                .onSuccess { accountId ->
                    if (accountId == null) {
                        accountRepository.createDefaultAccount(userId)
                            .onSuccess { onVerificationComplete() }
                            .onFailure { e ->
                                _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Failed creating default account")
                            }
                    } else {
                        onVerificationComplete()
                    }
                }
                .onFailure { e ->
                    _uiState.value = AuthUiState(errorMessage = e.localizedMessage ?: "Failed verifying user accounts")
                }
        }
    }

    private fun getCurrentUserId(): String? {
        return SupabaseClient.client.auth.currentUserOrNull()?.id
    }
}
