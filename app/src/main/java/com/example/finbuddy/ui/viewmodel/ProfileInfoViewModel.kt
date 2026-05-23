package com.example.finbuddy.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finbuddy.data.model.UserProfile
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed interface ProfileInfoUiState {
    data object Loading : ProfileInfoUiState
    data class Editing(
        val profile: UserProfile,
        val fullNameInput: String,
        val phoneInput: String
    ) : ProfileInfoUiState
    data object Saving : ProfileInfoUiState
    data class Error(val message: String) : ProfileInfoUiState
    data object Saved : ProfileInfoUiState
}

class ProfileInfoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileInfoUiState>(ProfileInfoUiState.Loading)
    val uiState: StateFlow<ProfileInfoUiState> = _uiState.asStateFlow()

    fun loadProfile() {
        _uiState.value = ProfileInfoUiState.Loading
        viewModelScope.launch {
            runCatching {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                    ?: error("No authenticated user found")
                SupabaseClient.client.postgrest.from("profiles")
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<UserProfile>()
            }.onSuccess { profile ->
                _uiState.value = ProfileInfoUiState.Editing(
                    profile = profile,
                    fullNameInput = profile.fullName.orEmpty(),
                    phoneInput = profile.phoneNumber.orEmpty()
                )
            }.onFailure {
                _uiState.value = ProfileInfoUiState.Error(
                    it.localizedMessage ?: "Failed to load profile"
                )
            }
        }
    }

    fun onFullNameChanged(value: String) {
        val current = _uiState.value as? ProfileInfoUiState.Editing ?: return
        _uiState.value = current.copy(fullNameInput = value)
    }

    fun onPhoneChanged(value: String) {
        val current = _uiState.value as? ProfileInfoUiState.Editing ?: return
        _uiState.value = current.copy(phoneInput = value)
    }

    fun uploadAvatar(context: Context, uri: Uri) {
        val current = _uiState.value as? ProfileInfoUiState.Editing ?: return
        viewModelScope.launch {
            runCatching {
                val userId = current.profile.id
                val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    ?: error("Could not read selected image")
                val filePath = "$userId/avatar.jpg"
                SupabaseClient.client.storage.from("avatars").upload(filePath, bytes) { upsert = true }
                val publicUrl = "https://pcezsvqkqinrakvxwrhp.supabase.co/storage/v1/object/public/avatars/$filePath"

                SupabaseClient.client.postgrest.from("profiles").update(
                    buildJsonObject { put("avatar_url", publicUrl) }
                ) {
                    filter { eq("id", userId) }
                }

                current.profile.copy(avatarUrl = publicUrl)
            }.onSuccess { updatedProfile ->
                _uiState.value = current.copy(profile = updatedProfile)
            }.onFailure {
                _uiState.value = ProfileInfoUiState.Error(
                    it.localizedMessage ?: "Failed to upload avatar"
                )
            }
        }
    }

    fun saveProfile() {
        val current = _uiState.value as? ProfileInfoUiState.Editing ?: return
        _uiState.value = ProfileInfoUiState.Saving
        viewModelScope.launch {
            runCatching {
                SupabaseClient.client.postgrest.from("profiles").update(
                    buildJsonObject {
                        put("full_name", current.fullNameInput.ifBlank { null })
                        put("phone_number", current.phoneInput.ifBlank { null })
                        put("avatar_url", current.profile.avatarUrl)
                    }
                ) {
                    filter { eq("id", current.profile.id) }
                }
            }.onSuccess {
                _uiState.value = ProfileInfoUiState.Saved
            }.onFailure {
                _uiState.value = current
            }
        }
    }
}
