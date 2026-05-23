package com.example.finbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.model.UserProfile
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val profile: UserProfile, val displayName: String) : SettingsUiState
    data class Error(val message: String) : SettingsUiState
}

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun fetchProfile() {
        _uiState.value = SettingsUiState.Loading
        viewModelScope.launch {
            runCatching {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                    ?: error("No authenticated user found")

                val profile = SupabaseClient.client.postgrest
                    .from("profiles")
                    .select {
                        filter { eq("id", userId) }
                    }
                    .decodeSingle<UserProfile>()

                val displayName = profile.fullName
                    ?.takeIf { it.isNotBlank() }
                    ?: profile.email.substringBefore("@")

                SettingsUiState.Success(profile = profile, displayName = displayName)
            }.onSuccess { state ->
                _uiState.value = state
            }.onFailure { e ->
                _uiState.value = SettingsUiState.Error(
                    e.localizedMessage ?: "Failed to load profile"
                )
            }
        }
    }
}
