package com.example.finbuddy.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String? = null,
    val user_id: String,
    val account_id: String,
    val amount: Double,
    val type: String,
    val source: String,
    val category: String,
    @SerialName("timestamp")
    val timestamp: String? = null,
    val created_at: String? = null
)
