package com.example.finbuddy.data.repository

import com.example.finbuddy.data.model.Account
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AccountRepository {
    suspend fun createDefaultAccount(userId: String): Result<Unit>
    suspend fun getPrimaryAccountId(userId: String): Result<String?>
}

class AccountRepositoryImpl : AccountRepository {
    private val postgrest = SupabaseClient.client.postgrest

    override suspend fun createDefaultAccount(userId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val defaultAccount = Account(
                    user_id = userId,
                    name = "Cash Wallet",
                    type = "Cash",
                    balance = 0.0
                )
                postgrest.from("accounts").insert(defaultAccount)
                Unit
            }
        }

    override suspend fun getPrimaryAccountId(userId: String): Result<String?> =
        withContext(Dispatchers.IO) {
            runCatching {
                val userAccounts = postgrest.from("accounts")
                    .select {
                        filter {
                            eq("user_id", userId)
                        }
                    }
                    .decodeList<Account>()

                userAccounts.firstOrNull()?.id
            }
        }
}
