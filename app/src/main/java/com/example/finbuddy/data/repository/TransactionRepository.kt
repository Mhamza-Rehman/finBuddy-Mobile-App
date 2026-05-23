package com.example.finbuddy.data.repository

import com.example.finbuddy.data.model.Account
import com.example.finbuddy.data.model.Transaction
import com.example.finbuddy.data.network.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

interface TransactionRepository {
    suspend fun addManualTransaction(
        userId: String,
        accountId: String,
        amount: Double,
        type: String,
        category: String
    ): Result<Unit>
}

class TransactionRepositoryImpl : TransactionRepository {
    private val postgrest = SupabaseClient.client.postgrest

    override suspend fun addManualTransaction(
        userId: String,
        accountId: String,
        amount: Double,
        type: String,
        category: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val transaction = Transaction(
                user_id = userId,
                account_id = accountId,
                amount = amount,
                type = type,
                source = if (category.startsWith("Auto SMS")) "SMS" else "Manual",
                category = category
            )

            postgrest.from("transactions").insert(transaction)

            val account = postgrest.from("accounts")
                .select {
                    filter { eq("id", accountId) }
                }
                .decodeSingle<Account>()

            val newBalance = if (type == "Income") {
                account.balance + amount
            } else {
                account.balance - amount
            }

            postgrest.from("accounts").update(
                buildJsonObject { put("balance", newBalance) }
            ) {
                filter { eq("id", accountId) }
            }

            Unit
        }
    }
}
