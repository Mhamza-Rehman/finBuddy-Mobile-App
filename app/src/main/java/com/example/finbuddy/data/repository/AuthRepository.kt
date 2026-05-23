package com.example.finbuddy.data.repository

import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface AuthRepository {
    suspend fun signUp(email: String, password: String, fullName: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    fun getCurrentUserId(): String?
}

class AuthRepositoryImpl : AuthRepository {
    override suspend fun signUp(email: String, password: String, fullName: String) {
        SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            // Custom user metadata
            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }

    override suspend fun signIn(email: String, password: String) {
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut() {
        SupabaseClient.client.auth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return SupabaseClient.client.auth.currentUserOrNull()?.id
    }
}
